package com.wfortini.paymentservice;

import com.wfortini.paymentservice.application.port.in.PaymentEventUseCases;
import com.wfortini.paymentservice.application.port.in.PaymentOrderUseCases;
import com.wfortini.paymentservice.application.port.out.PaymentEventPublisher;
import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
class PersistenceIntegrationTest {

    @Autowired
    private PaymentEventUseCases paymentEvents;

    @Autowired
    private PaymentOrderUseCases paymentOrders;

    @MockitoBean
    private PaymentEventPublisher paymentEventPublisher;

    @Test
    void shouldPersistPaymentEventAndOrder() {
        var eventToCreate = new PaymentEvent(
                "checkout-integration-test",
                "buyer",
                "seller",
                "masked-card",
                false
        );
        var order = new PaymentOrder(
                "order-integration-test",
                "buyer-account",
                "125.90",
                Currency.getInstance("BRL"),
                eventToCreate.checkoutId(),
                PaymentOrderStatus.NOT_STARTED,
                false,
                false
        );

        var event = paymentEvents.create(eventToCreate, java.util.List.of(order));

        assertThat(paymentEvents.findByCheckoutId(event.checkoutId())).contains(event);
        assertThat(paymentOrders.findByPaymentOrderId(order.paymentOrderId())).contains(order);
        verify(paymentEventPublisher).publish(event);
    }

    @Test
    void shouldRollbackEventWhenAnOrderCannotBePersisted() {
        var event = new PaymentEvent(
                "checkout-rollback-test",
                "buyer",
                "seller",
                "masked-card",
                false
        );
        var invalidOrder = new PaymentOrder(
                "order-rollback-test",
                "buyer-account",
                "1".repeat(256),
                Currency.getInstance("BRL"),
                event.checkoutId(),
                PaymentOrderStatus.NOT_STARTED,
                false,
                false
        );

        assertThatThrownBy(() -> paymentEvents.create(event, java.util.List.of(invalidOrder)))
                .isInstanceOf(RuntimeException.class);

        assertThat(paymentEvents.findByCheckoutId(event.checkoutId())).isEmpty();
        assertThat(paymentOrders.findByPaymentOrderId(invalidOrder.paymentOrderId())).isEmpty();
        verifyNoMoreInteractions(paymentEventPublisher);
    }
}
