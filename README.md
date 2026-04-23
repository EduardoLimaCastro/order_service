# order_service

Microserviço de pedidos do e-commerce. Expõe uma API REST para criação, consulta e atualização de status de pedidos, seguindo arquitetura em camadas (domain / application / infrastructure) consistente com `user_service` e `product_service`.

## Stack

- Java 21
- Spring Boot 3.5.13 (Web, Data JPA, Validation, Actuator)
- PostgreSQL 15
- Flyway (migrations)
- Lombok
- Testcontainers (testes)

## Arquitetura

```
src/main/java/com/eduardocastro/order_service
├── domain
│   ├── entity          → Order, OrderItem (entidades puras, sem JPA)
│   ├── enums           → OrderStatus
│   ├── event           → DomainEvent, OrderCreatedEvent, OrderStatusChangedEvent
│   ├── exception       → InvalidOrderDataException, OrderNotFoundException
│   └── repository      → OrderRepository (interface)
├── application
│   ├── dto             → OrderItemInput
│   ├── usecase         → Create/Get/List/UpdateStatus UseCase (interfaces)
│   └── interactor      → implementações @Service
└── infrastructure
    ├── persistence
    │   ├── jpa         → OrderJpaEntity, OrderItemJpaEntity, OrderJpaRepository
    │   ├── mapper      → OrderJpaMapper (domain ↔ JPA)
    │   └── adapter     → OrderRepositoryAdapter (@Repository)
    └── web
        ├── controller  → OrderController
        ├── dto         → request / response records
        ├── mapper      → OrderWebMapper
        └── handler     → GlobalExceptionHandler
```

A camada de domínio não depende de Spring nem JPA. O acesso a dados é feito por um adapter que converte entre a entidade de domínio (`Order`) e a entidade JPA (`OrderJpaEntity`).

## Modelo

**Order** — agregado raiz com lista de `OrderItem`. Status inicial: `PENDING`.

Transições válidas:

```
PENDING ──▶ CONFIRMED ──▶ SHIPPED ──▶ DELIVERED
   │            │             │
   └────────────┴─────────────┴─────▶ CANCELLED
```

Pedidos `DELIVERED` ou `CANCELLED` não podem mudar de status.

## Endpoints

Base: `http://localhost:8083/api/orders`

| Método | Rota                       | Descrição                          |
|--------|----------------------------|------------------------------------|
| POST   | `/api/orders`              | Cria um pedido com itens           |
| GET    | `/api/orders/{id}`         | Busca pedido por ID                |
| GET    | `/api/orders/user/{userId}`| Lista pedidos de um usuário        |
| PATCH  | `/api/orders/{id}/status`  | Atualiza o status do pedido        |

### Exemplos

**Criar pedido**

```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-123",
    "items": [
      {
        "productId": "prod-1",
        "productName": "Notebook",
        "quantity": 1,
        "unitPrice": 3500.00
      },
      {
        "productId": "prod-2",
        "productName": "Mouse",
        "quantity": 2,
        "unitPrice": 80.00
      }
    ]
  }'
```

**Atualizar status**

```bash
curl -X PATCH http://localhost:8083/api/orders/{id}/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}'
```

## Executando localmente

### Subir apenas o Postgres

```bash
docker-compose up -d postgres
./mvnw spring-boot:run
```

O serviço sobe em `http://localhost:8083`. A conexão local usa `jdbc:postgresql://localhost:5435/order_service` (porta mapeada no host).

### Subir tudo via Docker

```bash
docker-compose up --build
```

## Banco de dados

Migrations Flyway em `src/main/resources/db/migration/`:

- `V1__create_orders_table.sql` — tabela `orders`
- `V2__create_order_items_table.sql` — tabela `order_items` (FK para `orders`)

`spring.jpa.hibernate.ddl-auto` está como `none` — o schema é gerenciado exclusivamente pelo Flyway.

## Portas

| Serviço         | Porta host | Porta container |
|-----------------|------------|-----------------|
| order_service   | 8083       | 8080            |
| postgres        | 5435       | 5432            |
