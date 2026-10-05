package com.wfortini.ledgerservice.application.port.out;

import com.wfortini.ledgerservice.domain.model.LedgerEntry;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LedgerEntryRepository {

    LedgerEntry save(LedgerEntry entry);

    Optional<LedgerEntry> findById(UUID id);

    List<LedgerEntry> findByAccountIdAndCurrency(UUID accountId, Currency currency);
}
