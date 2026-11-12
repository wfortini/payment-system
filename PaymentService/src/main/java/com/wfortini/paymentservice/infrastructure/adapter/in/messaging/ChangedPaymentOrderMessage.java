package com.wfortini.paymentservice.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;

import java.util.UUID;

public record ChangedPaymentOrderMessage(
        @JsonProperty("payment_order_id") UUID paymentOrderId,
        PaymentOrderStatus status,
        String message
) {
}
