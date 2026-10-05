package com.wfortini.paymentservice.domain.model;

import java.util.Currency;
import java.util.Objects;

public record PaymentOrder(
        String paymentOrderId,
        String buyerAccount,
        String amount,
        Currency currency,
        String checkoutId,
        PaymentOrderStatus status,
        boolean ledgerUpdated,
        boolean walletUpdated
) {
    public PaymentOrder {
        requireText(paymentOrderId, "paymentOrderId");
        requireText(buyerAccount, "buyerAccount");
        requireText(amount, "amount");
        Objects.requireNonNull(currency, "currency must not be null");
        requireText(checkoutId, "checkoutId");
        Objects.requireNonNull(status, "status must not be null");
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
