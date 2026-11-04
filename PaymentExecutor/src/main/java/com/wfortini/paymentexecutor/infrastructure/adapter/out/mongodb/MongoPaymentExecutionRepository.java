package com.wfortini.paymentexecutor.infrastructure.adapter.out.mongodb;

import com.wfortini.paymentexecutor.application.port.out.PaymentExecutionRepository;
import com.wfortini.paymentexecutor.domain.model.PaymentExecution;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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

    @Override
    public Optional<PaymentExecution> findByExternalId(UUID externalId) {
        return repository.findByPaymentOrdersExternalId(externalId)
                .map(PaymentExecutionDocument::toDomain);
    }
}
