package com.wfortini.ledgerservice.application.port.in;

import com.wfortini.ledgerservice.domain.model.EntryType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLedgerEntryCommand(
        UUID accountId,
        EntryType type,
        BigDecimal amount,
        String currency,
        String description
) {
}
