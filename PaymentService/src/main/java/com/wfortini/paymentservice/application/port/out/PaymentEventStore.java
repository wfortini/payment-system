package com.wfortini.paymentservice.application.port.out;

import com.wfortini.paymentservice.domain.model.PaymentEvent;

import java.util.Optional;
import java.util.UUID;

public interface PaymentEventStore {

    PaymentEvent save(PaymentEvent paymentEvent);

    Optional<PaymentEvent> findByCheckoutId(UUID checkoutId);
}
