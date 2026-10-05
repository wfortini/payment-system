package com.wfortini.ledgerservice.application.port.in;

import com.wfortini.ledgerservice.domain.model.LedgerEntry;

import java.util.Optional;
import java.util.UUID;

public interface GetLedgerEntryUseCase {

    Optional<LedgerEntry> findById(UUID id);
}
