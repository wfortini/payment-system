package com.wfortini.pspemulator.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.pspemulator.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatedPayment(
        @JsonProperty("payment_order_id") UUID paymentOrderId,
        @JsonProperty("buyer_account") String buyerAccount,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        @JsonProperty("external_id") UUID externalId
) {
}
