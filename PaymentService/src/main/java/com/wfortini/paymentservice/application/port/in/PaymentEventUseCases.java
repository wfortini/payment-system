package com.wfortini.paymentservice.application.port.in;

import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;

import java.util.List;
import java.util.Optional;

public interface PaymentEventUseCases {

    PaymentEvent create(PaymentEvent paymentEvent, List<PaymentOrder> paymentOrders);

    Optional<PaymentEvent> findByCheckoutId(String checkoutId);
}
