package com.wfortini.ledgerservice.infrastructure.adapter.in.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.ledgerservice.application.port.in.ProcessPaymentCommand;
import com.wfortini.ledgerservice.domain.model.PaymentProcessedStatus;
import com.wfortini.ledgerservice.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessedEvent(
        @JsonProperty("payment_order_id") UUID paymentOrderId,
        PaymentProcessedStatus status,
        @JsonProperty("seller_account") UUID sellerAccount,
        @JsonProperty("buyer_account") UUID buyerAccount,
        BigDecimal amount,
        String description,
        @JsonProperty("transaction_type") TransactionType transactionType,
        String currency,
        @JsonProperty("external_id") UUID externalId
) {
    ProcessPaymentCommand toCommand() {
        return new ProcessPaymentCommand(
                paymentOrderId,
                status,
                sellerAccount,
                buyerAccount,
                amount,
                description,
                transactionType,
                currency,
                externalId
        );
    }
}
