package com.wfortini.paymentexecutor.application.port.in;

import com.wfortini.paymentexecutor.domain.model.PaymentExecution;

public interface ProcessPaymentEventUseCase {

    PaymentExecution process(PaymentExecution paymentExecution);
}
