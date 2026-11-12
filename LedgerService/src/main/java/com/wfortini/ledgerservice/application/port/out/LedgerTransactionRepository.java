package com.wfortini.ledgerservice.application.port.out;

import com.wfortini.ledgerservice.domain.model.LedgerTransaction;

import java.util.Optional;

public interface LedgerTransactionRepository {

    LedgerTransaction save(LedgerTransaction transaction);

    Optional<LedgerTransaction> findByExternalReference(String externalReference);
}
