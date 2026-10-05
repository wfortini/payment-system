package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import com.wfortini.paymentservice.domain.model.Payment;
import com.wfortini.paymentservice.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant createdAt
) {
    static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.id(),
                payment.amount(),
                payment.currency().getCurrencyCode(),
                payment.status(),
                payment.createdAt()
        );
    }
}
