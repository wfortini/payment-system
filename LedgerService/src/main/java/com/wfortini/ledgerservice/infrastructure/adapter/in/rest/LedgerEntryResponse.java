package com.wfortini.ledgerservice.infrastructure.adapter.in.rest;

import com.wfortini.ledgerservice.domain.model.EntryType;
import com.wfortini.ledgerservice.domain.model.LedgerEntry;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LedgerEntryResponse(
        UUID id,
        UUID accountId,
        EntryType type,
        BigDecimal amount,
        String currency,
        String description,
        Instant occurredAt
) {
    static LedgerEntryResponse from(LedgerEntry entry) {
        return new LedgerEntryResponse(
                entry.id(),
                entry.accountId(),
                entry.type(),
                entry.amount(),
                entry.currency().getCurrencyCode(),
                entry.description(),
                entry.occurredAt()
        );
    }
}
