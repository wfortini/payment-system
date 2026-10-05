package com.wfortini.paymentservice.infrastructure.adapter.out.persistence;

import com.wfortini.paymentservice.application.port.out.PaymentOrderStore;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import org.springframework.stereotype.Repository;

import java.util.Currency;
import java.util.Optional;

@Repository
public class JpaPaymentOrderStore implements PaymentOrderStore {

    private final SpringDataPaymentOrderRepository repository;

    JpaPaymentOrderStore(SpringDataPaymentOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaymentOrder save(PaymentOrder order) {
        return toDomain(repository.save(new PaymentOrderJpaEntity(
                order.paymentOrderId(), order.buyerAccount(), order.amount(),
                order.currency().getCurrencyCode(), order.checkoutId(), order.status(),
                order.ledgerUpdated(), order.walletUpdated()
        )));
    }

    @Override
    public Optional<PaymentOrder> findByPaymentOrderId(String paymentOrderId) {
        return repository.findById(paymentOrderId).map(this::toDomain);
    }

    private PaymentOrder toDomain(PaymentOrderJpaEntity entity) {
        return new PaymentOrder(
                entity.paymentOrderId, entity.buyerAccount, entity.amount,
                Currency.getInstance(entity.currency), entity.checkoutId, entity.status,
                entity.ledgerUpdated, entity.walletUpdated
        );
    }
}
