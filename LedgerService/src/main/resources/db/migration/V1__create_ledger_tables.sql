CREATE TABLE ledger_transaction (
    transaction_id UUID PRIMARY KEY,
    payment_order_id UUID,
    external_reference VARCHAR(100) NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    currency CHAR(3) NOT NULL,
    description VARCHAR(255),
    reversal_of_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    posted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_ledger_transaction_reference UNIQUE (external_reference),
    CONSTRAINT fk_ledger_transaction_reversal
        FOREIGN KEY (reversal_of_id) REFERENCES ledger_transaction (transaction_id),
    CONSTRAINT ck_ledger_transaction_type
        CHECK (transaction_type IN ('PAYMENT', 'REFUND', 'REVERSAL', 'FEE', 'ADJUSTMENT')),
    CONSTRAINT ck_ledger_transaction_status
        CHECK (status IN ('PENDING', 'POSTED', 'REVERSED', 'FAILED'))
);

CREATE TABLE ledger_entry (
    entry_id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    account_id UUID NOT NULL,
    entry_type VARCHAR(10) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency CHAR(3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_ledger_entry_transaction
        FOREIGN KEY (transaction_id) REFERENCES ledger_transaction (transaction_id),
    CONSTRAINT ck_ledger_entry_type CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    CONSTRAINT ck_ledger_entry_amount CHECK (amount > 0)
);

CREATE INDEX idx_ledger_entry_account_currency
    ON ledger_entry (account_id, currency, created_at);

CREATE INDEX idx_ledger_entry_transaction
    ON ledger_entry (transaction_id);

CREATE INDEX idx_ledger_transaction_payment_order
    ON ledger_transaction (payment_order_id);
