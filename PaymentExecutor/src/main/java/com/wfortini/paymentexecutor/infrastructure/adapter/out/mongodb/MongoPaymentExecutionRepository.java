package com.wfortini.paymentexecutor.infrastructure.adapter.out.mongodb;

import com.wfortini.paymentexecutor.application.port.out.PaymentExecutionRepository;
import com.wfortini.paymentexecutor.domain.model.PaymentExecution;
import org.springframework.stereotype.Repository;

@Repository
public class MongoPaymentExecutionRepository implements PaymentExecutionRepository {

    private final SpringDataPaymentExecutionRepository repository;

    public MongoPaymentExecutionRepository(SpringDataPaymentExecutionRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaymentExecution save(PaymentExecution paymentExecution) {
        return repository.save(PaymentExecutionDocument.from(paymentExecution)).toDomain();
    }
}
