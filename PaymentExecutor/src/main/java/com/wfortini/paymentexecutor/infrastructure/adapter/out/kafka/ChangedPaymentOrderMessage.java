package com.wfortini.paymentexecutor.infrastructure.adapter.out.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.paymentexecutor.domain.model.ChangedPaymentOrderEvent;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;

import java.util.UUID;

public record ChangedPaymentOrderMessage(
        @JsonProperty("payment_order_id") UUID paymentOrderId,
        @JsonProperty("external_id") UUID externalId,
        PaymentOrderStatus status,
        String message
) {
    static ChangedPaymentOrderMessage from(ChangedPaymentOrderEvent event) {
        return new ChangedPaymentOrderMessage(
                event.paymentOrderId(), event.externalId(), event.status(), event.message()
        );
    }
}
