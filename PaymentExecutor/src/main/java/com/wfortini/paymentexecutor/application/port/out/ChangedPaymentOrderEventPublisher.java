package com.wfortini.paymentexecutor.application.port.out;

import com.wfortini.paymentexecutor.domain.model.ChangedPaymentOrderEvent;

public interface ChangedPaymentOrderEventPublisher {

    void publish(ChangedPaymentOrderEvent event);
}
