package com.wfortini.paymentservice.infrastructure.adapter.out.persistence;

import com.wfortini.paymentservice.application.port.out.PaymentEventStore;
import com.wfortini.paymentservice.domain.model.PaymentEvent;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaPaymentEventStore implements PaymentEventStore {

    private final SpringDataPaymentEventRepository repository;

    JpaPaymentEventStore(SpringDataPaymentEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaymentEvent save(PaymentEvent event) {
        return toDomain(repository.save(new PaymentEventJpaEntity(
                event.checkoutId(), event.buyerInfo(), event.sellerInfo(),
                event.creditCardInfo(), event.paymentDone()
        )));
    }

    @Override
    public Optional<PaymentEvent> findByCheckoutId(String checkoutId) {
        return repository.findById(checkoutId).map(this::toDomain);
    }

    private PaymentEvent toDomain(PaymentEventJpaEntity entity) {
        return new PaymentEvent(
                entity.checkoutId, entity.buyerInfo, entity.sellerInfo,
                entity.creditCardInfo, entity.paymentDone
        );
    }
}
