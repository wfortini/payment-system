package com.wfortini.paymentservice.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessedMessage(
        @JsonProperty("payment_order_id") UUID paymentOrderId,
        String status,
        @JsonProperty("seller_account") UUID sellerAccount,
        @JsonProperty("buyer_account") UUID buyerAccount,
        BigDecimal amount,
        String description,
        @JsonProperty("transaction_type") String transactionType,
        String currency,
        @JsonProperty("external_id") UUID externalId
) {
}
