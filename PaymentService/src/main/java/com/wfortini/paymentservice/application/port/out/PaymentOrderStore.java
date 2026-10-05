package com.wfortini.paymentservice.application.port.out;

import com.wfortini.paymentservice.domain.model.PaymentOrder;

import java.util.Optional;

public interface PaymentOrderStore {

    PaymentOrder save(PaymentOrder paymentOrder);

    Optional<PaymentOrder> findByPaymentOrderId(String paymentOrderId);
}
