package com.wfortini.paymentservice.domain.model;

import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public record PaymentOrder(
        UUID paymentOrderId,
        String buyerAccount,
        UUID sellerAccount,
        String amount,
        Currency currency,
        UUID checkoutId,
        PaymentOrderStatus status,
        boolean ledgerUpdated,
        boolean walletUpdated
) {
    public PaymentOrder {
        Objects.requireNonNull(paymentOrderId, "paymentOrderId must not be null");
        requireText(buyerAccount, "buyerAccount");
        Objects.requireNonNull(sellerAccount, "sellerAccount must not be null");
        requireText(amount, "amount");
        Objects.requireNonNull(currency, "currency must not be null");
        Objects.requireNonNull(checkoutId, "checkoutId must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    public PaymentOrder withStatus(PaymentOrderStatus newStatus) {
        return new PaymentOrder(
                paymentOrderId,
                buyerAccount,
                sellerAccount,
                amount,
                currency,
                checkoutId,
                newStatus,
                ledgerUpdated,
                walletUpdated
        );
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
