package com.wfortini.paymentservice.application.service;

import com.wfortini.paymentservice.application.port.in.PaymentEventUseCases;
import com.wfortini.paymentservice.application.port.in.PaymentOrderUseCases;
import com.wfortini.paymentservice.application.port.out.PaymentEventStore;
import com.wfortini.paymentservice.application.port.out.PaymentOrderStore;
import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class PaymentPersistenceService implements PaymentEventUseCases, PaymentOrderUseCases {

    private final PaymentEventStore paymentEventStore;
    private final PaymentOrderStore paymentOrderStore;

    public PaymentPersistenceService(
            PaymentEventStore paymentEventStore,
            PaymentOrderStore paymentOrderStore
    ) {
        this.paymentEventStore = Objects.requireNonNull(paymentEventStore);
        this.paymentOrderStore = Objects.requireNonNull(paymentOrderStore);
    }

    @Override
    public PaymentEvent create(PaymentEvent paymentEvent, List<PaymentOrder> paymentOrders) {
        Objects.requireNonNull(paymentOrders, "paymentOrders must not be null");
        if (paymentEventStore.findByCheckoutId(paymentEvent.checkoutId()).isPresent()) {
            throw new IllegalStateException("Payment event already exists: " + paymentEvent.checkoutId());
        }

        var orderIds = new HashSet<String>();
        for (var paymentOrder : paymentOrders) {
            if (!paymentEvent.checkoutId().equals(paymentOrder.checkoutId())) {
                throw new IllegalArgumentException(
                        "Payment order checkoutId must match payment event checkoutId: "
                                + paymentOrder.paymentOrderId()
                );
            }
            if (!orderIds.add(paymentOrder.paymentOrderId())) {
                throw new IllegalStateException(
                        "Duplicated payment order in request: " + paymentOrder.paymentOrderId()
                );
            }
            if (paymentOrderStore.findByPaymentOrderId(paymentOrder.paymentOrderId()).isPresent()) {
                throw new IllegalStateException(
                        "Payment order already exists: " + paymentOrder.paymentOrderId()
                );
            }
        }

        var savedPaymentEvent = paymentEventStore.save(paymentEvent);
        paymentOrders.forEach(paymentOrderStore::save);
        return savedPaymentEvent;
    }

    @Override
    public Optional<PaymentEvent> findByCheckoutId(String checkoutId) {
        return paymentEventStore.findByCheckoutId(checkoutId);
    }

    @Override
    public PaymentOrder create(PaymentOrder paymentOrder) {
        if (paymentEventStore.findByCheckoutId(paymentOrder.checkoutId()).isEmpty()) {
            throw new IllegalArgumentException("Payment event does not exist: " + paymentOrder.checkoutId());
        }
        if (paymentOrderStore.findByPaymentOrderId(paymentOrder.paymentOrderId()).isPresent()) {
            throw new IllegalStateException("Payment order already exists: " + paymentOrder.paymentOrderId());
        }
        return paymentOrderStore.save(paymentOrder);
    }

    @Override
    public Optional<PaymentOrder> findByPaymentOrderId(String paymentOrderId) {
        return paymentOrderStore.findByPaymentOrderId(paymentOrderId);
    }
}
