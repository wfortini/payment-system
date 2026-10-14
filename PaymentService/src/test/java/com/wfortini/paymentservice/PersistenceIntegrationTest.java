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
import java.util.UUID;

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
                UUID.fromString("f76b6fe5-3588-487b-a78e-c4996f85349e"),
                "buyer",
                "seller",
                "masked-card",
                false
        );
        var order = new PaymentOrder(
                UUID.fromString("181e536d-a140-46f8-a469-d8d9459d1b90"),
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
        verify(paymentEventPublisher).publish(event, java.util.List.of(order));
    }

    @Test
    void shouldRollbackEventWhenAnOrderCannotBePersisted() {
        var event = new PaymentEvent(
                UUID.fromString("56b5f437-7bc6-484f-bab7-070ad00cd2cd"),
                "buyer",
                "seller",
                "masked-card",
                false
        );
        var invalidOrder = new PaymentOrder(
                UUID.fromString("434d94ba-5e97-4c04-bd62-a23095b3197a"),
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
