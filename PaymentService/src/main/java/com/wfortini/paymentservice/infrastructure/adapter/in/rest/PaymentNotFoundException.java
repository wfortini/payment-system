package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import java.util.UUID;

final class PaymentNotFoundException extends RuntimeException {

    PaymentNotFoundException(UUID id) {
        super("Payment not found: " + id);
    }
}
