CREATE TABLE IF NOT EXISTS cart_items (
    id           UUID           PRIMARY KEY,
    cart_id      UUID           NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id   VARCHAR(255)   NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    quantity     INT            NOT NULL,
    unit_price   NUMERIC(19, 2) NOT NULL,
    subtotal     NUMERIC(19, 2) NOT NULL
);
