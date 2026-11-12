package com.wfortini.ledgerservice.infrastructure.adapter.out.persistence;

import com.wfortini.ledgerservice.application.port.out.LedgerEntryRepository;
import com.wfortini.ledgerservice.application.port.out.LedgerTransactionRepository;
import com.wfortini.ledgerservice.domain.model.LedgerEntry;
import com.wfortini.ledgerservice.domain.model.LedgerTransaction;
import org.springframework.stereotype.Repository;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaLedgerRepository implements LedgerEntryRepository, LedgerTransactionRepository {

    private final SpringDataLedgerTransactionRepository transactions;
    private final SpringDataLedgerEntryRepository entries;

    JpaLedgerRepository(
            SpringDataLedgerTransactionRepository transactions,
            SpringDataLedgerEntryRepository entries
    ) {
        this.transactions = transactions;
        this.entries = entries;
    }

    @Override
    public LedgerTransaction save(LedgerTransaction transaction) {
        return toDomain(transactions.save(toEntity(transaction)));
    }

    @Override
    public Optional<LedgerTransaction> findByExternalReference(String externalReference) {
        return transactions.findByExternalReference(externalReference).map(this::toDomain);
    }

    @Override
    public Optional<LedgerEntry> findById(UUID id) {
        return entries.findById(id).map(this::toDomain);
    }

    @Override
    public List<LedgerEntry> findByAccountIdAndCurrency(UUID accountId, Currency currency) {
        return entries.findByAccountIdAndCurrency(accountId, currency.getCurrencyCode()).stream()
                .map(this::toDomain)
                .toList();
    }

    private LedgerTransactionJpaEntity toEntity(LedgerTransaction source) {
        var entity = new LedgerTransactionJpaEntity();
        entity.transactionId = source.transactionId();
        entity.paymentOrderId = source.paymentOrderId();
        entity.externalReference = source.externalReference();
        entity.transactionType = source.transactionType();
        entity.status = source.status();
        entity.currency = source.currency().getCurrencyCode();
        entity.description = source.description();
        entity.reversalOfId = source.reversalOfId();
        entity.createdAt = source.createdAt();
        entity.postedAt = source.postedAt();
        entity.entries = source.entries().stream().map(entry -> {
            var entryEntity = new LedgerEntryJpaEntity();
            entryEntity.entryId = entry.id();
            entryEntity.transaction = entity;
            entryEntity.transactionId = entity.transactionId;
            entryEntity.accountId = entry.accountId();
            entryEntity.entryType = entry.type();
            entryEntity.amount = entry.amount();
            entryEntity.currency = entry.currency().getCurrencyCode();
            entryEntity.createdAt = entry.occurredAt();
            return entryEntity;
        }).toList();
        return entity;
    }

    private LedgerTransaction toDomain(LedgerTransactionJpaEntity source) {
        return new LedgerTransaction(
                source.transactionId,
                source.paymentOrderId,
                source.externalReference,
                source.transactionType,
                source.status,
                Currency.getInstance(source.currency),
                source.description,
                source.reversalOfId,
                source.createdAt,
                source.postedAt,
                source.entries.stream().map(this::toDomain).toList()
        );
    }

    private LedgerEntry toDomain(LedgerEntryJpaEntity source) {
        return new LedgerEntry(
                source.entryId,
                source.transactionId,
                source.accountId,
                source.entryType,
                source.amount,
                Currency.getInstance(source.currency),
                source.createdAt
        );
    }
}
