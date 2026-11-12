package com.wfortini.ledgerservice.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataLedgerTransactionRepository
        extends JpaRepository<LedgerTransactionJpaEntity, UUID> {

    Optional<LedgerTransactionJpaEntity> findByExternalReference(String externalReference);
}
