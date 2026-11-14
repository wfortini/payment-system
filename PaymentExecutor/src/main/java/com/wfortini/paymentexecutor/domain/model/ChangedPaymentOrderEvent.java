package com.wfortini.paymentexecutor.domain.model;

import java.util.UUID;

public record ChangedPaymentOrderEvent(
        UUID paymentOrderId,
        UUID externalId,
        PaymentOrderStatus status,
        String message
) {
}
