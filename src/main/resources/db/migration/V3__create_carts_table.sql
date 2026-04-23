CREATE TABLE IF NOT EXISTS carts (
    id           UUID           PRIMARY KEY,
    user_id      VARCHAR(255)   NOT NULL UNIQUE,
    total_amount NUMERIC(19, 2) NOT NULL,
    created_at   TIMESTAMP      NOT NULL,
    updated_at   TIMESTAMP      NOT NULL
);
