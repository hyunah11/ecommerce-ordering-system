### ecommerce 상품 주문 서비스 프로젝트

- `e-커머스 상품 주문 서비스 시나리오`입니다.
- 상품 주문에 필요한 상품 정보를 구성하고 **재고 관리 정합성**을 보장합니다.
- 사용자는 여러 상품을 선택해 주문하고 **미리 충전한 잔액**으로 결제합니다.
- 주문 내역을 분석해 **판매량 TOP 상품**을 추천합니다.

### 문서
- [API 명세서](./docs/api.md)
- [ERD](./docs/erd.md)
- [인프라 구성도 및 컴포넌트 설명](./docs/infra.md)

### Architecture

Clean Layered Architecture 기반 의존성 역전 구조

```
Controller → Application → Domain Port ← Infrastructure
```

- **Controller**: HTTP 요청/응답 처리
- **Application**: 비즈니스 유스케이스 및 트랜잭션 경계  
- **Domain Port**: 도메인 계층의 데이터 접근 추상화 (Repository Interface)
- **Infrastructure**: JPA 기반 실제 구현체
- **Domain**: 순수 POJO 기반 비즈니스 로직

### Prerequisites
- JDK 17
- Gradle (Wrapper로 실행 가능 ./gradlew)
- Docker & Docker Compose

### Running Docker Containers

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행해주셔야 합니다.

```bash
docker-compose up -d
```

MySQL 8.0 컨테이너가 3306 포트로 기동됩니다.<br/>

컨테이너 정상 동작 확인:
```bash
docker ps
docker logs ecommerce-ordering-system-mysql-1 --tail=20
```

DB 접속 테스트:
```bash
docker exec -it ecommerce-ordering-system-mysql-1 \
  mysql -uapplication -papplication ecommerce -e "SELECT 1;"
```

#### Application Run
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

#### Concurrency & Consistency

- 선착순 쿠폰: Redis 원자 연산(+TTL)
- 결제 멱등성: Idempotency Key로 관리
- 외부 전송: Transactional Outbox + 재시도/백오프

#### Roadmap

- 잔액 충전/조회 TDD
- 상품 조회 TDD
- 선착순 쿠폰 TDD
- 주문/결제(재고/잔액/쿠폰/멱등성) TDD
- 상위 상품 조회(집계 전략) TDD
- Outbox + Relay(Mock)
- 부하/동시성 테스트 시나리오 작성