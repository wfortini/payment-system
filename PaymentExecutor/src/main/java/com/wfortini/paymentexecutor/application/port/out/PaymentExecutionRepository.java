package com.wfortini.paymentexecutor.application.port.out;

import com.wfortini.paymentexecutor.domain.model.PaymentExecution;

public interface PaymentExecutionRepository {

    PaymentExecution save(PaymentExecution paymentExecution);
}
