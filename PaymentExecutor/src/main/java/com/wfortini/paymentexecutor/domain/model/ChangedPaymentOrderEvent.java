package com.wfortini.paymentexecutor.domain.model;

import java.util.UUID;

public record ChangedPaymentOrderEvent(
        UUID paymentOrderId,
        PaymentOrderStatus status,
        String message
) {
}
