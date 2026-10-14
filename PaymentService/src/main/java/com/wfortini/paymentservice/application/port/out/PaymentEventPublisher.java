package com.wfortini.paymentservice.application.port.out;

import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;

import java.util.List;

public interface PaymentEventPublisher {

    void publish(PaymentEvent paymentEvent, List<PaymentOrder> paymentOrders);
}
