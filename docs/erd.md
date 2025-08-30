# ERD

> **재고/쿠폰 경쟁 환경에서의 정합성**과 **결제/이벤트 발행의 원자성** 을 보장합니다.

```mermaid
erDiagram
    USERS ||--o{ WALLETS : has
    USERS ||--o{ WALLET_TRANSACTIONS : makes
    USERS ||--o{ ORDERS : places
    USERS ||--o{ COUPON_ISSUES : owns

    PRODUCTS ||--o{ ORDER_ITEMS : contains
    ORDERS ||--o{ ORDER_ITEMS : includes
    ORDERS ||--|| PAYMENTS : "has 1"

    COUPONS ||--o{ COUPON_ISSUES : issues
    PRODUCTS ||--o{ PRODUCT_STOCK_LEDGER : logs

    ORDERS ||--o{ OUTBOX_EVENTS : emits
    PRODUCTS ||--o{ PRODUCT_SALES_DAILY : aggregates

    USERS {
      bigint id PK
      text name
      bigint created_at
    }

    WALLETS {
      bigint user_id PK,FK
      bigint balance
      bigint updated_at
    }

    WALLET_TRANSACTIONS {
      bigint id PK
      bigint user_id FK
      bigint amount  "signed delta"
      bigint balance_after
      text reason
      text idempotency_key
      bigint created_at
      %% UNIQUE(user_id, idempotency_key)
      unique user_id__idempotency_key
    }

    PRODUCTS {
      bigint id PK
      text name
      bigint price
      int stock
      text status  "ON_SALE, PAUSED, OFF"
      bigint created_at
      bigint updated_at
    }

    PRODUCT_STOCK_LEDGER {
      bigint id PK
      bigint product_id FK
      int delta_qty "(-) for sell"
      text reason "ORDER, CANCEL, ADJUST"
      bigint order_item_id
      bigint created_at
    }

    COUPONS {
      bigint id PK
      text name
      text discount_type "AMOUNT|PERCENT"
      bigint amount "if AMOUNT"
      int percent "if PERCENT"
      bigint max_discount "cap for PERCENT"
      bigint min_order_amount
      int total_quantity "FCFS cap"
      bigint starts_at
      bigint expires_at
      int per_user_limit
    }

    COUPON_ISSUES {
      bigint id PK
      bigint coupon_id FK
      bigint user_id FK
      text status "ISSUED|USED|EXPIRED"
      bigint issued_at
      bigint used_at
      %% UNIQUE(coupon_id, user_id)
      unique coupon_id__user_id "1/user"
    }

    ORDERS {
      bigint id PK
      bigint user_id FK
      text status "CREATED|PAID|CANCELED"
      bigint subtotal
      bigint discount
      bigint total_amount
      bigint created_at
      bigint updated_at
    }

    ORDER_ITEMS {
      bigint id PK
      bigint order_id FK
      bigint product_id FK
      int qty
      bigint unit_price
      bigint subtotal
    }

    PAYMENTS {
      bigint id PK
      bigint order_id FK
      %% UNIQUE(order_id)
      bigint user_id FK
      text method "WALLET"
      bigint amount
      text status "SUCCEEDED|FAILED"
      bigint created_at
    }

    OUTBOX_EVENTS {
      bigint id PK
      text aggregate_type "ORDER"
      bigint aggregate_id
      text event_type "ORDER_PAID"
      jsonb payload
      text status "READY|SENT|FAILED"
      bigint created_at
      %% INDEX(status, created_at)
      index status__created_at
    }

    PRODUCT_SALES_DAILY {
      bigint id PK
      bigint product_id FK
      date d
      int qty
      %% UNIQUE(product_id, d)
      unique product_id__d
    }
```
## 정합성 전략
- **재고 차감**: 원자성으로 재고를 차감하고 `PRODUCT_STOCK_LEDGER` 기록합니다.
- **쿠폰 발급(FCFS)**: Redis `DECR` + 사용자 중복 체크(`%% UNIQUE(coupon_id, user_id)
      unique coupon_id__user_id`) 및 Lua 스크립트로 원자화합니다.
- **결제/주문**: 단일 DB 트랜잭션(모놀리식)으로 주문/결제/쿠폰사용/재고차감을 우선 구현합니다. 분리 시 SAGA + 보상 사용합니다.
- **이벤트 발행**: Outbox 테이블에 기록 후 전용 워커가 Kafka/HTTP로 전송(최소 1회 보장, 멱등 처리).
- **상위상품 집계**: `ORDER_ITEMS` 스트림을 일자별로 roll-up → `PRODUCT_SALES_DAILY`에 upsert. 조회 시 최근 3일 합계합니다.
