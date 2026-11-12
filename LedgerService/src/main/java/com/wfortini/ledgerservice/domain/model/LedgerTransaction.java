package com.wfortini.ledgerservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record LedgerTransaction(
        UUID transactionId,
        UUID paymentOrderId,
        String externalReference,
        TransactionType transactionType,
        LedgerTransactionStatus status,
        Currency currency,
        String description,
        UUID reversalOfId,
        Instant createdAt,
        Instant postedAt,
        List<LedgerEntry> entries
) {
    public LedgerTransaction {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(externalReference, "externalReference must not be null");
        Objects.requireNonNull(transactionType, "transactionType must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        entries = List.copyOf(Objects.requireNonNull(entries, "entries must not be null"));

        if (externalReference.isBlank()) {
            throw new IllegalArgumentException("externalReference must not be blank");
        }
        if (status == LedgerTransactionStatus.POSTED) {
            validatePostedEntries(transactionId, currency, entries);
        }
    }

    private static void validatePostedEntries(
            UUID transactionId,
            Currency currency,
            List<LedgerEntry> entries
    ) {
        if (entries.size() < 2) {
            throw new IllegalArgumentException("posted transaction must contain at least two entries");
        }

        var debit = BigDecimal.ZERO;
        var credit = BigDecimal.ZERO;
        for (var entry : entries) {
            if (!entry.transactionId().equals(transactionId)) {
                throw new IllegalArgumentException("entry belongs to another transaction");
            }
            if (!entry.currency().equals(currency)) {
                throw new IllegalArgumentException("entry currency must match transaction currency");
            }
            if (entry.type() == EntryType.DEBIT) {
                debit = debit.add(entry.amount());
            } else {
                credit = credit.add(entry.amount());
            }
        }

        if (debit.compareTo(credit) != 0) {
            throw new IllegalArgumentException("total debit must equal total credit");
        }
    }
}
