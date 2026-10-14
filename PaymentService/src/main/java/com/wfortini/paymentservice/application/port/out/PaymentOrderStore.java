package com.wfortini.paymentservice.application.port.out;

import com.wfortini.paymentservice.domain.model.PaymentOrder;

import java.util.Optional;
import java.util.UUID;

public interface PaymentOrderStore {

    PaymentOrder save(PaymentOrder paymentOrder);

    Optional<PaymentOrder> findByPaymentOrderId(UUID paymentOrderId);
}
