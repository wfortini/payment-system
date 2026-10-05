package com.wfortini.paymentservice.application.port.in;

import com.wfortini.paymentservice.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface GetPaymentUseCase {

    Optional<Payment> findById(UUID id);
}
