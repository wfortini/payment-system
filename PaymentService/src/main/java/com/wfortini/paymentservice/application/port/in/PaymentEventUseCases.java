package com.wfortini.paymentservice.application.port.in;

import com.wfortini.paymentservice.domain.model.PaymentEvent;

import java.util.Optional;

public interface PaymentEventUseCases {

    PaymentEvent create(PaymentEvent paymentEvent);

    Optional<PaymentEvent> findByCheckoutId(String checkoutId);
}
