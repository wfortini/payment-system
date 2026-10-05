package com.wfortini.ledgerservice.application.port.in;

import com.wfortini.ledgerservice.domain.model.LedgerEntry;

public interface CreateLedgerEntryUseCase {

    LedgerEntry create(CreateLedgerEntryCommand command);
}
