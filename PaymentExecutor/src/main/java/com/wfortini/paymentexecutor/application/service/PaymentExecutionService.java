package com.wfortini.paymentexecutor.application.service;

import com.wfortini.paymentexecutor.application.port.in.ProcessPaymentEventUseCase;
import com.wfortini.paymentexecutor.application.port.out.PaymentExecutionRepository;
import com.wfortini.paymentexecutor.domain.model.PaymentExecution;

import java.util.Objects;

public final class PaymentExecutionService implements ProcessPaymentEventUseCase {

    private final PaymentExecutionRepository repository;

    public PaymentExecutionService(PaymentExecutionRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public PaymentExecution process(PaymentExecution paymentExecution) {
        return repository.save(paymentExecution);
    }
}
