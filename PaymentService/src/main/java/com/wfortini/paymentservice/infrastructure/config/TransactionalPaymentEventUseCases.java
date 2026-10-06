package com.wfortini.paymentservice.infrastructure.config;

import com.wfortini.paymentservice.application.port.in.PaymentEventUseCases;
import com.wfortini.paymentservice.application.port.out.PaymentEventPublisher;
import com.wfortini.paymentservice.application.service.PaymentPersistenceService;
import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;

public class TransactionalPaymentEventUseCases implements PaymentEventUseCases {

    private final PaymentPersistenceService delegate;
    private final PaymentEventPublisher paymentEventPublisher;

    public TransactionalPaymentEventUseCases(
            PaymentPersistenceService delegate,
            PaymentEventPublisher paymentEventPublisher
    ) {
        this.delegate = delegate;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @Override
    @Transactional
    public PaymentEvent create(PaymentEvent paymentEvent, List<PaymentOrder> paymentOrders) {
        var savedPaymentEvent = delegate.create(paymentEvent, paymentOrders);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                paymentEventPublisher.publish(savedPaymentEvent);
            }
        });
        return savedPaymentEvent;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentEvent> findByCheckoutId(String checkoutId) {
        return delegate.findByCheckoutId(checkoutId);
    }
}
