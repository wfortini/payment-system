package com.wfortini.ledgerservice.infrastructure.adapter.out.persistence;

import com.wfortini.ledgerservice.domain.model.EntryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_entry")
class LedgerEntryJpaEntity {

    @Id
    @Column(name = "entry_id", nullable = false)
    UUID entryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    LedgerTransactionJpaEntity transaction;

    @Column(name = "transaction_id", nullable = false, insertable = false, updatable = false)
    UUID transactionId;

    @Column(name = "account_id", nullable = false)
    UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 10)
    EntryType entryType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "CHAR(3)")
    String currency;

    @Column(name = "created_at", nullable = false)
    Instant createdAt;

    protected LedgerEntryJpaEntity() {
    }
}
