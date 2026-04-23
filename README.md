# order_service

Microserviço de pedidos e carrinhos do e-commerce. Expõe uma API REST para manipular carrinhos, fazer checkout, e consultar/atualizar pedidos, seguindo arquitetura em camadas (domain / application / infrastructure) consistente com `user_service` e `product_service`.

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
│   ├── entity          → Order, OrderItem, Cart, CartItem (entidades puras, sem JPA)
│   ├── enums           → OrderStatus
│   ├── event           → DomainEvent, OrderCreatedEvent, OrderStatusChangedEvent,
│   │                     CartCheckedOutEvent
│   ├── exception       → InvalidOrderDataException, OrderNotFoundException,
│   │                     InvalidCartDataException, CartNotFoundException
│   └── repository      → OrderRepository, CartRepository (interfaces)
├── application
│   ├── dto             → OrderItemInput
│   ├── usecase         → Order + Cart use case interfaces
│   └── interactor      → implementações @Service
└── infrastructure
    ├── persistence
    │   ├── jpa         → OrderJpaEntity, OrderItemJpaEntity, CartJpaEntity,
    │   │                 CartItemJpaEntity, *JpaRepository
    │   ├── mapper      → OrderJpaMapper, CartJpaMapper (domain ↔ JPA)
    │   └── adapter     → OrderRepositoryAdapter, CartRepositoryAdapter
    └── web
        ├── controller  → OrderController, CartController
        ├── dto         → request / response records
        ├── mapper      → OrderWebMapper, CartWebMapper
        └── handler     → GlobalExceptionHandler
```

A camada de domínio não depende de Spring nem JPA. O acesso a dados é feito por um adapter que converte entre a entidade de domínio (`Order`) e a entidade JPA (`OrderJpaEntity`).

## Modelo

### Cart

**Cart** — agregado raiz com lista de `CartItem`. **Um carrinho por usuário** (constraint `UNIQUE` em `user_id`). Itens mutáveis: `quantity` pode ser alterada ou somada.

**Merge por productId:** se o mesmo `productId` já existe no carrinho, a `quantity` é somada no item existente em vez de criar uma nova linha.

**Fluxo Cart → Order:**

```
 user adiciona itens               checkout
┌──────────────────┐            ┌──────────────┐
│      Cart        │  ─────▶    │    Order     │   (Cart é deletado
│  (PENDING add)   │            │  (PENDING)   │    na mesma transação)
└──────────────────┘            └──────────────┘
```

### Order

**Order** — agregado raiz com lista de `OrderItem`. Status inicial: `PENDING`.

Transições válidas:

```
PENDING ──▶ CONFIRMED ──▶ SHIPPED ──▶ DELIVERED
   │            │             │
   └────────────┴─────────────┴─────▶ CANCELLED
```

Pedidos `DELIVERED` ou `CANCELLED` não podem mudar de status.

## Endpoints

Base: `http://localhost:8083`

### Cart

| Método | Rota                                       | Descrição                                         |
|--------|--------------------------------------------|---------------------------------------------------|
| GET    | `/api/carts/user/{userId}`                 | Busca carrinho (404 se não existe)                |
| POST   | `/api/carts/user/{userId}/items`           | Adiciona item (cria carrinho automaticamente)     |
| PATCH  | `/api/carts/user/{userId}/items/{itemId}`  | Atualiza quantidade de um item                    |
| DELETE | `/api/carts/user/{userId}/items/{itemId}`  | Remove item do carrinho                           |
| DELETE | `/api/carts/user/{userId}`                 | Esvazia o carrinho                                |
| POST   | `/api/carts/user/{userId}/checkout`        | Converte carrinho em pedido + limpa (transacional)|

### Order

| Método | Rota                       | Descrição                          |
|--------|----------------------------|------------------------------------|
| POST   | `/api/orders`              | Cria um pedido com itens           |
| GET    | `/api/orders/{id}`         | Busca pedido por ID                |
| GET    | `/api/orders/user/{userId}`| Lista pedidos de um usuário        |
| PATCH  | `/api/orders/{id}/status`  | Atualiza o status do pedido        |

### Exemplos

**Adicionar item ao carrinho**

```bash
curl -X POST http://localhost:8083/api/carts/user/user-123/items \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "prod-1",
    "productName": "Notebook",
    "quantity": 1,
    "unitPrice": 3500.00
  }'
```

**Atualizar quantidade de um item**

```bash
curl -X PATCH http://localhost:8083/api/carts/user/user-123/items/{itemId} \
  -H "Content-Type: application/json" \
  -d '{"quantity": 3}'
```

**Checkout (carrinho → pedido)**

```bash
curl -X POST http://localhost:8083/api/carts/user/user-123/checkout
```

Retorna o `Order` criado. O carrinho é deletado na mesma transação.

**Criar pedido diretamente (sem passar por carrinho)**

```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-123",
    "items": [
      {"productId": "prod-1", "productName": "Notebook", "quantity": 1, "unitPrice": 3500.00},
      {"productId": "prod-2", "productName": "Mouse", "quantity": 2, "unitPrice": 80.00}
    ]
  }'
```

**Atualizar status de pedido**

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
- `V3__create_carts_table.sql` — tabela `carts` (user_id UNIQUE)
- `V4__create_cart_items_table.sql` — tabela `cart_items` (FK para `carts`, ON DELETE CASCADE)

`spring.jpa.hibernate.ddl-auto` está como `none` — o schema é gerenciado exclusivamente pelo Flyway.

## Portas

| Serviço         | Porta host | Porta container |
|-----------------|------------|-----------------|
| order_service   | 8083       | 8080            |
| postgres        | 5435       | 5432            |
