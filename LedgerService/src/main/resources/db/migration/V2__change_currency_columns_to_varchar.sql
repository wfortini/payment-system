ALTER TABLE ledger_entry
    ALTER COLUMN currency SET DATA TYPE VARCHAR(3);

ALTER TABLE ledger_transaction
    ALTER COLUMN currency SET DATA TYPE VARCHAR(3);
