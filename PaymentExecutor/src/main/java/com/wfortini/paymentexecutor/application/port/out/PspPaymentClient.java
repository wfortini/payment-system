package com.wfortini.paymentexecutor.application.port.out;

import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;

public interface PspPaymentClient {

    PspPaymentResult createPayment(PaymentOrderExecution paymentOrder);
}
