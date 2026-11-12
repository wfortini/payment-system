package com.wfortini.paymentservice.application.port.in;

import com.wfortini.paymentservice.domain.model.PaymentOrder;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;

import java.util.Optional;
import java.util.UUID;

public interface UpdatePaymentOrderStatusUseCase {

    Optional<PaymentOrder> updateStatus(UUID paymentOrderId, PaymentOrderStatus status);
}
