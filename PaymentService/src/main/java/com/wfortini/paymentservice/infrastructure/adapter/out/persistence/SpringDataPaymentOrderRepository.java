package com.wfortini.paymentservice.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataPaymentOrderRepository extends JpaRepository<PaymentOrderJpaEntity, UUID> {
}
