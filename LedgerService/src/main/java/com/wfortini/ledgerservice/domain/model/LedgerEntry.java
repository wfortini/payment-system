package com.wfortini.ledgerservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public record LedgerEntry(
        UUID id,
        UUID accountId,
        EntryType type,
        BigDecimal amount,
        Currency currency,
        String description,
        Instant occurredAt
) {
    public LedgerEntry {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(accountId, "accountId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }

    public BigDecimal signedAmount() {
        return type == EntryType.CREDIT ? amount : amount.negate();
    }
}
