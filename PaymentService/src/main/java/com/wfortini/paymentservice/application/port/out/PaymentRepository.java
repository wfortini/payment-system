package com.wfortini.paymentservice.application.port.out;

import com.wfortini.paymentservice.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);
}
