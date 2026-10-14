package com.wfortini.paymentexecutor.infrastructure.adapter.out.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

interface SpringDataPaymentExecutionRepository extends MongoRepository<PaymentExecutionDocument, UUID> {
}
