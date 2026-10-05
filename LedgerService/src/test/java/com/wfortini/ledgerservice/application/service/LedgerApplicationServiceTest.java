package com.wfortini.ledgerservice.application.service;

import com.wfortini.ledgerservice.application.port.in.CreateLedgerEntryCommand;
import com.wfortini.ledgerservice.application.port.out.LedgerEntryRepository;
import com.wfortini.ledgerservice.domain.model.EntryType;
import com.wfortini.ledgerservice.domain.model.LedgerEntry;
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

    @Test
    void shouldCreateEntriesAndCalculateBalance() {
        var repository = new TestLedgerEntryRepository();
        var instant = Instant.parse("2026-01-01T12:00:00Z");
        var service = new LedgerApplicationService(repository, Clock.fixed(instant, ZoneOffset.UTC));
        var accountId = UUID.randomUUID();

        var credit = service.create(command(accountId, EntryType.CREDIT, "100.00"));
        service.create(command(accountId, EntryType.DEBIT, "30.50"));

        assertThat(credit.occurredAt()).isEqualTo(instant);
        assertThat(service.findById(credit.id())).contains(credit);
        assertThat(service.getBalance(accountId, "brl").balance()).isEqualByComparingTo("69.50");
    }

    private CreateLedgerEntryCommand command(UUID accountId, EntryType type, String amount) {
        return new CreateLedgerEntryCommand(
                accountId,
                type,
                new BigDecimal(amount),
                "BRL",
                "Test entry"
        );
    }

    private static final class TestLedgerEntryRepository implements LedgerEntryRepository {
        private final List<LedgerEntry> entries = new ArrayList<>();

        @Override
        public LedgerEntry save(LedgerEntry entry) {
            entries.add(entry);
            return entry;
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
