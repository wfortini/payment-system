package com.wfortini.ledgerservice.application.service;

import com.wfortini.ledgerservice.application.port.in.ProcessPaymentCommand;
import com.wfortini.ledgerservice.application.port.out.LedgerEntryRepository;
import com.wfortini.ledgerservice.application.port.out.LedgerTransactionRepository;
import com.wfortini.ledgerservice.domain.model.EntryType;
import com.wfortini.ledgerservice.domain.model.LedgerEntry;
import com.wfortini.ledgerservice.domain.model.LedgerTransaction;
import com.wfortini.ledgerservice.domain.model.LedgerTransactionStatus;
import com.wfortini.ledgerservice.domain.model.PaymentProcessedStatus;
import com.wfortini.ledgerservice.domain.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LedgerApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-01T12:00:00Z");

    @Test
    void shouldCreateBalancedEntriesForSuccessfulPayment() {
        var repository = new TestLedgerRepository();
        var service = service(repository);
        var buyer = UUID.randomUUID();
        var seller = UUID.randomUUID();

        var transaction = service.process(command(
                PaymentProcessedStatus.SUCCESS, buyer, seller, UUID.randomUUID()
        ));

        assertThat(transaction.status()).isEqualTo(LedgerTransactionStatus.POSTED);
        assertThat(transaction.postedAt()).isEqualTo(NOW);
        assertThat(transaction.entries()).hasSize(2);
        assertThat(transaction.entries())
                .anySatisfy(entry -> {
                    assertThat(entry.accountId()).isEqualTo(buyer);
                    assertThat(entry.type()).isEqualTo(EntryType.DEBIT);
                    assertThat(entry.amount()).isEqualByComparingTo("10.00");
                })
                .anySatisfy(entry -> {
                    assertThat(entry.accountId()).isEqualTo(seller);
                    assertThat(entry.type()).isEqualTo(EntryType.CREDIT);
                    assertThat(entry.amount()).isEqualByComparingTo("10.00");
                });
        assertThat(service.getBalance(buyer, "BRL").balance()).isEqualByComparingTo("-10.00");
        assertThat(service.getBalance(seller, "BRL").balance()).isEqualByComparingTo("10.00");
    }

    @Test
    void shouldBeIdempotentByExternalId() {
        var repository = new TestLedgerRepository();
        var service = service(repository);
        var externalId = UUID.randomUUID();
        var command = command(PaymentProcessedStatus.SUCCESS, UUID.randomUUID(), UUID.randomUUID(), externalId);

        var first = service.process(command);
        var duplicate = service.process(command);

        assertThat(duplicate).isEqualTo(first);
        assertThat(repository.transactions).hasSize(1);
        assertThat(repository.entries).hasSize(2);
    }

    @Test
    void shouldPersistFailedTransactionWithoutFinancialEntries() {
        var repository = new TestLedgerRepository();
        var transaction = service(repository).process(command(
                PaymentProcessedStatus.FAILED, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        ));

        assertThat(transaction.status()).isEqualTo(LedgerTransactionStatus.FAILED);
        assertThat(transaction.postedAt()).isNull();
        assertThat(transaction.entries()).isEmpty();
    }

    private LedgerApplicationService service(TestLedgerRepository repository) {
        return new LedgerApplicationService(repository, repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private ProcessPaymentCommand command(
            PaymentProcessedStatus status,
            UUID buyer,
            UUID seller,
            UUID externalId
    ) {
        return new ProcessPaymentCommand(
                UUID.randomUUID(), status, seller, buyer, new BigDecimal("10.00"),
                "payment sent with success", TransactionType.PAYMENT, "BRL", externalId
        );
    }

    private static final class TestLedgerRepository
            implements LedgerEntryRepository, LedgerTransactionRepository {

        private final List<LedgerTransaction> transactions = new ArrayList<>();
        private final List<LedgerEntry> entries = new ArrayList<>();

        @Override
        public LedgerTransaction save(LedgerTransaction transaction) {
            transactions.add(transaction);
            entries.addAll(transaction.entries());
            return transaction;
        }

        @Override
        public Optional<LedgerTransaction> findByExternalReference(String externalReference) {
            return transactions.stream()
                    .filter(transaction -> transaction.externalReference().equals(externalReference))
                    .findFirst();
        }

        @Override
        public Optional<LedgerEntry> findById(UUID id) {
            return entries.stream().filter(entry -> entry.id().equals(id)).findFirst();
        }

        @Override
        public List<LedgerEntry> findByAccountIdAndCurrency(UUID accountId, Currency currency) {
            return entries.stream()
                    .filter(entry -> entry.accountId().equals(accountId))
                    .filter(entry -> entry.currency().equals(currency))
                    .toList();
        }
    }
}
