package com.wfortini.paymentservice.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataPaymentOrderRepository extends JpaRepository<PaymentOrderJpaEntity, String> {
}
