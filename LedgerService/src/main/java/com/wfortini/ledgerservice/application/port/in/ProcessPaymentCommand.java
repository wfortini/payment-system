package com.wfortini.ledgerservice.application.port.in;

import com.wfortini.ledgerservice.domain.model.PaymentProcessedStatus;
import com.wfortini.ledgerservice.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessPaymentCommand(
        UUID paymentOrderId,
        PaymentProcessedStatus status,
        UUID sellerAccount,
        UUID buyerAccount,
        BigDecimal amount,
        String description,
        TransactionType transactionType,
        String currency,
        UUID externalId
) {
}
