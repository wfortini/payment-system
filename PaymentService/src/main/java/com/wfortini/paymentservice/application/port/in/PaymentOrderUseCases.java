package com.wfortini.paymentservice.application.port.in;

import com.wfortini.paymentservice.domain.model.PaymentOrder;

import java.util.Optional;
import java.util.UUID;

public interface PaymentOrderUseCases {

    PaymentOrder create(PaymentOrder paymentOrder);

    Optional<PaymentOrder> findByPaymentOrderId(UUID paymentOrderId);
}
