package com.wfortini.paymentservice.application.port.out;

import com.wfortini.paymentservice.domain.model.PaymentOrder;

import java.util.UUID;

public interface PaymentProcessedEventPublisher {

    void publish(PaymentOrder paymentOrder, UUID externalId);
}
