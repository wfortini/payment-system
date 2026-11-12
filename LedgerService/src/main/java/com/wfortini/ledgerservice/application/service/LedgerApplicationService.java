package com.wfortini.ledgerservice.application.service;

import com.wfortini.ledgerservice.application.port.in.GetAccountBalanceUseCase;
import com.wfortini.ledgerservice.application.port.in.GetLedgerEntryUseCase;
import com.wfortini.ledgerservice.application.port.in.ProcessPaymentCommand;
import com.wfortini.ledgerservice.application.port.in.ProcessPaymentUseCase;
import com.wfortini.ledgerservice.application.port.out.LedgerEntryRepository;
import com.wfortini.ledgerservice.application.port.out.LedgerTransactionRepository;
import com.wfortini.ledgerservice.domain.model.AccountBalance;
import com.wfortini.ledgerservice.domain.model.LedgerEntry;
import com.wfortini.ledgerservice.domain.model.EntryType;
import com.wfortini.ledgerservice.domain.model.LedgerTransaction;
import com.wfortini.ledgerservice.domain.model.LedgerTransactionStatus;
import com.wfortini.ledgerservice.domain.model.PaymentProcessedStatus;
import com.wfortini.ledgerservice.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class LedgerApplicationService implements
        ProcessPaymentUseCase,
        GetLedgerEntryUseCase,
        GetAccountBalanceUseCase {

    private final LedgerEntryRepository entryRepository;
    private final LedgerTransactionRepository transactionRepository;
    private final Clock clock;

    public LedgerApplicationService(
            LedgerEntryRepository entryRepository,
            LedgerTransactionRepository transactionRepository,
            Clock clock
    ) {
        this.entryRepository = Objects.requireNonNull(entryRepository);
        this.transactionRepository = Objects.requireNonNull(transactionRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public LedgerTransaction process(ProcessPaymentCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Objects.requireNonNull(command.externalId(), "externalId must not be null");
        var externalReference = command.externalId().toString();
        var existing = transactionRepository.findByExternalReference(externalReference);
        if (existing.isPresent()) {
            return existing.get();
        }

        var currency = toCurrency(command.currency());
        var now = Instant.now(clock);
        var transactionId = UUID.randomUUID();
        var posted = command.status() == PaymentProcessedStatus.SUCCESS;
        var entries = posted
                ? createBalancedEntries(command, transactionId, currency, now)
                : java.util.List.<LedgerEntry>of();
        var transaction = new LedgerTransaction(
                transactionId,
                command.paymentOrderId(),
                externalReference,
                command.transactionType(),
                posted ? LedgerTransactionStatus.POSTED : LedgerTransactionStatus.FAILED,
                currency,
                command.description(),
                null,
                now,
                posted ? now : null,
                entries
        );
        return transactionRepository.save(transaction);
    }

    private java.util.List<LedgerEntry> createBalancedEntries(
            ProcessPaymentCommand command,
            UUID transactionId,
            Currency currency,
            Instant occurredAt
    ) {
        Objects.requireNonNull(command.amount(), "amount must not be null");
        if (command.amount().signum() <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        var reverseFlow = command.transactionType() == TransactionType.REFUND
                || command.transactionType() == TransactionType.REVERSAL;
        var debitAccount = reverseFlow ? command.sellerAccount() : command.buyerAccount();
        var creditAccount = reverseFlow ? command.buyerAccount() : command.sellerAccount();

        return java.util.List.of(
                new LedgerEntry(UUID.randomUUID(), transactionId, debitAccount, EntryType.DEBIT,
                        command.amount(), currency, occurredAt),
                new LedgerEntry(UUID.randomUUID(), transactionId, creditAccount, EntryType.CREDIT,
                        command.amount(), currency, occurredAt)
        );
    }

    @Override
    public Optional<LedgerEntry> findById(UUID id) {
        return entryRepository.findById(id);
    }

    @Override
    public AccountBalance getBalance(UUID accountId, String currencyCode) {
        var currency = toCurrency(currencyCode);
        var balance = entryRepository.findByAccountIdAndCurrency(accountId, currency).stream()
                .map(LedgerEntry::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AccountBalance(accountId, currency, balance);
    }

    private Currency toCurrency(String currencyCode) {
        Objects.requireNonNull(currencyCode, "currency must not be null");
        return Currency.getInstance(currencyCode.toUpperCase(Locale.ROOT));
    }
}
