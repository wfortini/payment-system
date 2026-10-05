package com.wfortini.ledgerservice.infrastructure.adapter.out.persistence;

import com.wfortini.ledgerservice.application.port.out.LedgerEntryRepository;
import com.wfortini.ledgerservice.domain.model.LedgerEntry;
import org.springframework.stereotype.Repository;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryLedgerEntryRepository implements LedgerEntryRepository {

    private final ConcurrentHashMap<UUID, LedgerEntry> entries = new ConcurrentHashMap<>();

    @Override
    public LedgerEntry save(LedgerEntry entry) {
        entries.put(entry.id(), entry);
        return entry;
    }

    @Override
    public Optional<LedgerEntry> findById(UUID id) {
        return Optional.ofNullable(entries.get(id));
    }

    @Override
    public List<LedgerEntry> findByAccountIdAndCurrency(UUID accountId, Currency currency) {
        return entries.values().stream()
                .filter(entry -> entry.accountId().equals(accountId))
                .filter(entry -> entry.currency().equals(currency))
                .toList();
    }
}
