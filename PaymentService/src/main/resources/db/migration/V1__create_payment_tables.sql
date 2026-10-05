CREATE TABLE payment_event (
    checkout_id VARCHAR(255) PRIMARY KEY,
    buyer_info VARCHAR(2000) NOT NULL,
    seller_info VARCHAR(2000) NOT NULL,
    credit_card_info VARCHAR(2000) NOT NULL,
    is_payment_done BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE payment_order (
    payment_order_id VARCHAR(255) PRIMARY KEY,
    buyer_account VARCHAR(255) NOT NULL,
    amount VARCHAR(255) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    checkout_id VARCHAR(255) NOT NULL,
    payment_order_status VARCHAR(32) NOT NULL,
    ledger_updated BOOLEAN NOT NULL DEFAULT FALSE,
    wallet_updated BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_payment_order_payment_event
        FOREIGN KEY (checkout_id) REFERENCES payment_event (checkout_id),
    CONSTRAINT ck_payment_order_status
        CHECK (payment_order_status IN ('NOT_STARTED', 'EXECUTING', 'SUCCESS', 'FAILED'))
);

CREATE INDEX idx_payment_order_checkout_id ON payment_order (checkout_id);
