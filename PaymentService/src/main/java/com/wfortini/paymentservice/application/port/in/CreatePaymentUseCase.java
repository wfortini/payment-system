package com.wfortini.paymentservice.application.port.in;

import com.wfortini.paymentservice.domain.model.Payment;

public interface CreatePaymentUseCase {

    Payment create(CreatePaymentCommand command);
}
