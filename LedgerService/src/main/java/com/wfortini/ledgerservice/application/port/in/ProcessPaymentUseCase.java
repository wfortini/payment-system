package com.wfortini.ledgerservice.application.port.in;

import com.wfortini.ledgerservice.domain.model.LedgerTransaction;

public interface ProcessPaymentUseCase {

    LedgerTransaction process(ProcessPaymentCommand command);
}
