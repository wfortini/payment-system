package com.wfortini.ledgerservice.infrastructure.adapter.in.rest;

import com.wfortini.ledgerservice.domain.model.EntryType;
import com.wfortini.ledgerservice.domain.model.LedgerEntry;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LedgerEntryResponse(
        UUID id,
        UUID transactionId,
        UUID accountId,
        EntryType type,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) {
    static LedgerEntryResponse from(LedgerEntry entry) {
        return new LedgerEntryResponse(
                entry.id(),
                entry.transactionId(),
                entry.accountId(),
                entry.type(),
                entry.amount(),
                entry.currency().getCurrencyCode(),
                entry.occurredAt()
        );
    }
}
