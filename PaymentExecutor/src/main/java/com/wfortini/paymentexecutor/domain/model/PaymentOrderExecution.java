package com.wfortini.paymentexecutor.domain.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public record PaymentOrderExecution(
        UUID paymentOrderId,
        String buyerAccount,
        BigDecimal amount,
        Currency currency,
        UUID checkoutId,
        PaymentOrderStatus status,
        boolean ledgerUpdated,
        boolean walletUpdated
) {
    public PaymentOrderExecution {
        Objects.requireNonNull(paymentOrderId, "paymentOrderId must not be null");
        requireText(buyerAccount, "buyerAccount");
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        Objects.requireNonNull(currency, "currency must not be null");
        Objects.requireNonNull(checkoutId, "checkoutId must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
