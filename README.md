### ecommerce 상품 주문 서비스 프로젝트

## Getting Started

### Prerequisites
- JDK 17
- Gradle (Wrapper로 실행 가능 ./gradlew)
- Docker & Docker Compose

#### Running Docker Containers

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