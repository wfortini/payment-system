package com.wfortini.paymentexecutor.infrastructure.adapter.out.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;
import java.util.Optional;

interface SpringDataPaymentExecutionRepository extends MongoRepository<PaymentExecutionDocument, UUID> {

    Optional<PaymentExecutionDocument> findByPaymentOrdersExternalId(UUID externalId);
}
