package com.wfortini.paymentservice.domain.model;

import java.util.Objects;
import java.util.UUID;

public record PaymentEvent(
        UUID checkoutId,
        String buyerInfo,
        String sellerInfo,
        String creditCardInfo,
        boolean paymentDone
) {
    public PaymentEvent {
        Objects.requireNonNull(checkoutId, "checkoutId must not be null");
        requireText(buyerInfo, "buyerInfo");
        requireText(sellerInfo, "sellerInfo");
        requireText(creditCardInfo, "creditCardInfo");
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
