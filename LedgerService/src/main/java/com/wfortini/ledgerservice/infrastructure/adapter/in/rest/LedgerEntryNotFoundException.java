package com.wfortini.ledgerservice.infrastructure.adapter.in.rest;

import java.util.UUID;

final class LedgerEntryNotFoundException extends RuntimeException {

    LedgerEntryNotFoundException(UUID id) {
        super("Ledger entry not found: " + id);
    }
}
