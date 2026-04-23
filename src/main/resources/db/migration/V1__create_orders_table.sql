CREATE TABLE IF NOT EXISTS orders (
    id          UUID         PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);
