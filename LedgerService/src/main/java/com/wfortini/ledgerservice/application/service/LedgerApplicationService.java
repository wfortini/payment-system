package com.wfortini.ledgerservice.application.service;

import com.wfortini.ledgerservice.application.port.in.CreateLedgerEntryCommand;
import com.wfortini.ledgerservice.application.port.in.CreateLedgerEntryUseCase;
import com.wfortini.ledgerservice.application.port.in.GetAccountBalanceUseCase;
import com.wfortini.ledgerservice.application.port.in.GetLedgerEntryUseCase;
import com.wfortini.ledgerservice.application.port.out.LedgerEntryRepository;
import com.wfortini.ledgerservice.domain.model.AccountBalance;
import com.wfortini.ledgerservice.domain.model.LedgerEntry;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class LedgerApplicationService implements
        CreateLedgerEntryUseCase,
        GetLedgerEntryUseCase,
        GetAccountBalanceUseCase {

    private final LedgerEntryRepository repository;
    private final Clock clock;

    public LedgerApplicationService(LedgerEntryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public LedgerEntry create(CreateLedgerEntryCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        var currency = toCurrency(command.currency());
        var entry = new LedgerEntry(
                UUID.randomUUID(),
                command.accountId(),
                command.type(),
                command.amount(),
                currency,
                command.description().trim(),
                Instant.now(clock)
        );
        return repository.save(entry);
    }

    @Override
    public Optional<LedgerEntry> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public AccountBalance getBalance(UUID accountId, String currencyCode) {
        var currency = toCurrency(currencyCode);
        var balance = repository.findByAccountIdAndCurrency(accountId, currency).stream()
                .map(LedgerEntry::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AccountBalance(accountId, currency, balance);
    }

    private Currency toCurrency(String currencyCode) {
        Objects.requireNonNull(currencyCode, "currency must not be null");
        return Currency.getInstance(currencyCode.toUpperCase(Locale.ROOT));
    }
}
