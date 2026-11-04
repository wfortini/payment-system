package com.wfortini.paymentexecutor.application.port.out;

import com.wfortini.paymentexecutor.domain.model.PaymentExecution;

import java.util.Optional;
import java.util.UUID;

public interface PaymentExecutionRepository {

    PaymentExecution save(PaymentExecution paymentExecution);

    Optional<PaymentExecution> findByExternalId(UUID externalId);
}
