package com.wfortini.paymentexecutor.application.port.in;

import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;

import java.util.Optional;
import java.util.UUID;

public interface UpdatePaymentOrderStatusUseCase {

    Optional<PaymentOrderExecution> update(UUID externalId, PaymentOrderStatus status);
}
