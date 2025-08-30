## API 명세서

**버전**: v1.0<br/>
**에러 포맷**
```json
{
  "code": "WALLET_INSUFFICIENT_BALANCE",
  "message": "잔액이 부족합니다."
}
```
<br/>

### 시나리오
- 사용자는 **지갑(선충전)** 으로 결제합니다.
- **선착순 쿠폰**을 발급받아 주문 시 전체 금액에 할인 적용할 수 있습니다.
- **재고**는 강한 일관성으로 관리합니다.
- **주문 결제 성공 이벤트**는 외부 데이터 플랫폼으로 실시간 전송합니다.
- **상위 상품 조회**는 최근 3일간 판매량 기준 Top 5를 제공합니다.
<br/><br/>
---

## 1) 잔액 충전 / 조회

### 1.1 잔액 충전
- **POST** `/v1/wallets/{userId}/charge`
- 설명: 사용자의 지갑에 금액을 충전합니다.

**Request**
```json
{
  "amount": 50000
}
```

**Response 200**
```json
{
  "userId": 1001,
  "balance": 120000,
  "updatedAt": "1693353600"
}
```

**에러**
- `INVALID_AMOUNT` (amount <= 0)
- `USER_NOT_FOUND`

### 1.2 잔액 조회
- **GET** `/v1/wallets/{userId}`

**Response 200**
```json
{
  "userId": 1001,
  "balance": 120000,
  "updatedAt": "2025-08-30T00:00:00Z"
}
```

---

## 2) 상품 조회

### 2.1 상품 리스트
- **GET** `/v1/products?keyword=&cursor=&limit=20`
- 설명: 상품(가격, 재고 포함) 목록 조회. 재고는 조회 시점 스냅샷입니다.

**Response 200**
```json
{
  "items": [
    {
      "productId": 101,
      "name": "유기농 우유 1L",
      "price": 3200,
      "stock": 57,
      "status": "ON_SALE"
    }
  ],
  "nextCursor": "eyJwY2lIjoiMiJ9"
}
```

### 2.2 상품 단건
- **GET** `/v1/products/{productId}`

**Response 200**
```json
{
  "productId": 101,
  "name": "유기농 우유 1L",
  "price": 3200,
  "stock": 57,
  "status": "ON_SALE",
  "description": "원유 100%",
  "images": ["https://.../tempMilk.jpg"]
}
```

---

## 3) 선착순 쿠폰

### 3.1 쿠폰 발급
- **POST** `/v1/coupons/{couponId}/issue`
- 설명: 선착순 수량 한도 내에서 사용자에게 1매 발급.

**Request**
```json
{
  "userId": 1001
}
```

**Response 201**
```json
{
  "couponIssueId": 55501,
  "couponId": 9001,
  "userId": 1001,
  "status": "ISSUED",
  "discount": {
    "type": "PERCENT", 
    "value": 10,
    "maxDiscount": 5000
  },
  "expiresAt": "1694736000"
}
```

**에러**
- `COUPON_SOLD_OUT`
- `COUPON_EXPIRED`
- `COUPON_ALREADY_ISSUED`
- `USER_NOT_FOUND`

### 3.2 보유 쿠폰 목록
- **GET** `/v1/users/{userId}/coupons?status=ISSUED|USED|EXPIRED`

**Response 200**
```json
{
  "items": [
    {
      "couponIssueId": 55501,
      "couponId": 9001,
      "status": "ISSUED",
      "discount": {"type":"PERCENT","value":10,"maxDiscount":3000},
      "expiresAt": "1694736000",
      "usable": true,
      "reason": null
    }
  ]
}
```

---

## 4) 주문 / 결제

### 4.1 체크아웃 (재고 차감 + 쿠폰 적용 + 지갑 차감)
- **POST** `/v1/orders/checkout`
- 설명: 재고 확보 → 금액 계산(쿠폰) → 지갑 차감 → 주문/결제 확정(원자적 트랜잭션 혹은 SAGA로 정합성 보장)

**Request**
```json
{
  "userId": 1001,
  "items": [
    {"productId": 101, "qty": 2},
    {"productId": 102, "qty": 1}
  ],
  "couponIssueId": 55501
}
```

**Response 201**
```json
{
  "orderId": 7000123,
  "status": "PAID",
  "amount": {
    "subtotal": 9600,
    "discount": 960,
    "total": 8640
  },
  "paidBy": "WALLET",
  "wallet": {
    "balanceAfter": 111360
  },
  "items": [
    {"productId":101,"qty":2,"unitPrice":3200,"subtotal":6400},
    {"productId":102,"qty":1,"unitPrice":3200,"subtotal":3200}
  ],
  "createdAt": "2025-08-30T00:00:00Z"
}
```

**에러**
- `OUT_OF_STOCK`
- `WALLET_INSUFFICIENT_BALANCE`
- `COUPON_INVALID` / `COUPON_EXPIRED` / `COUPON_ALREADY_USED`

**주요 규칙**
- 쿠폰 사용 마킹과 지갑 차감은 동일 트랜잭션(모놀리식) 또는 SAGA로 보상 처리합니다.
- 성공 시 **Outbox**에 `ORDER_PAID` 이벤트 기록 → 비동기로 데이터 플랫폼에 송신합니다.

---

## 5) 상위 상품 조회 (최근 3일)
- **GET** `/v1/products/top`
- 설명: 최근 N일 판매량 기준 Top 5

**Response 200**
```json
{
  "rangeDays": 3,
  "items": [
    {"productId": 101, "name":"유기농 우유 1L", "soldQty": 812},
    {"productId": 103, "name":"그릭요거트", "soldQty": 799}
  ]
}
```

---

### 보안/운영
- 인증/인가: JWT
- 제한: 사용자/리소스별 rate limit, 재시도는 지수백오프로 제한합니다.
