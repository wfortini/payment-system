package com.wfortini.ledgerservice.infrastructure.adapter.out.persistence;

import com.wfortini.ledgerservice.domain.model.LedgerTransactionStatus;
import com.wfortini.ledgerservice.domain.model.TransactionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ledger_transaction")
class LedgerTransactionJpaEntity {

    @Id
    @Column(name = "transaction_id", nullable = false)
    UUID transactionId;

    @Column(name = "payment_order_id")
    UUID paymentOrderId;

    @Column(name = "external_reference", nullable = false, unique = true, length = 100)
    String externalReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    LedgerTransactionStatus status;

    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "CHAR(3)")
    String currency;

    @Column(name = "description", length = 255)
    String description;

    @Column(name = "reversal_of_id")
    UUID reversalOfId;

    @Column(name = "created_at", nullable = false)
    Instant createdAt;

    @Column(name = "posted_at")
    Instant postedAt;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER,
            orphanRemoval = true)
    List<LedgerEntryJpaEntity> entries = new ArrayList<>();

    protected LedgerTransactionJpaEntity() {
    }
}
