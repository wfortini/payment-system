package com.wfortini.paymentservice.application.port.out;

import com.wfortini.paymentservice.domain.model.PaymentEvent;

public interface PaymentEventPublisher {

    void publish(PaymentEvent paymentEvent);
}
