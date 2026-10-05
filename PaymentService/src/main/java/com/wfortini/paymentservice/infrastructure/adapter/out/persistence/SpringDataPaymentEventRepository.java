package com.wfortini.paymentservice.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataPaymentEventRepository extends JpaRepository<PaymentEventJpaEntity, String> {
}
