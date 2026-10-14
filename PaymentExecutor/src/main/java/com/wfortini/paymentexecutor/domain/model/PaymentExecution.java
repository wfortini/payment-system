package com.wfortini.paymentexecutor.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PaymentExecution(
        UUID checkoutId,
        String buyerInfo,
        String sellerInfo,
        String creditCardInfo,
        boolean paymentDone,
        List<PaymentOrderExecution> paymentOrders,
        Instant receivedAt
) {
    public PaymentExecution {
        Objects.requireNonNull(checkoutId, "checkoutId must not be null");
        requireText(buyerInfo, "buyerInfo");
        requireText(sellerInfo, "sellerInfo");
        requireText(creditCardInfo, "creditCardInfo");
        paymentOrders = List.copyOf(Objects.requireNonNull(paymentOrders, "paymentOrders must not be null"));
        if (paymentOrders.isEmpty()) {
            throw new IllegalArgumentException("paymentOrders must not be empty");
        }
        if (paymentOrders.stream().anyMatch(order -> !checkoutId.equals(order.checkoutId()))) {
            throw new IllegalArgumentException("all payment orders must belong to the event checkoutId");
        }
        Objects.requireNonNull(receivedAt, "receivedAt must not be null");
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
