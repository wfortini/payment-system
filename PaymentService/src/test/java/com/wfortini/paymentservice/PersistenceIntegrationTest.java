package com.wfortini.paymentservice;

import com.wfortini.paymentservice.application.port.in.PaymentEventUseCases;
import com.wfortini.paymentservice.application.port.in.PaymentOrderUseCases;
import com.wfortini.paymentservice.application.port.in.UpdatePaymentOrderStatusUseCase;
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

    @Autowired
    private UpdatePaymentOrderStatusUseCase updatePaymentOrderStatus;

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
                UUID.fromString("d4a944da-f975-4392-96ec-78d63adfefa4"),
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
                UUID.fromString("16f85885-6bf9-4b89-807d-67fe47d4cf2a"),
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

    @Test
    void shouldUpdatePaymentOrderStatus() {
        var event = new PaymentEvent(
                UUID.fromString("b83fcc41-c962-49c6-8b72-b4b482d0f22a"),
                "buyer",
                "seller",
                "masked-card",
                false
        );
        var order = new PaymentOrder(
                UUID.fromString("73b55af9-e5bf-48cb-90c7-8056de82b865"),
                "buyer-account",
                UUID.fromString("22fb9a4a-b6f6-4fa7-9af8-a9a176103b7f"),
                "42.00",
                Currency.getInstance("BRL"),
                event.checkoutId(),
                PaymentOrderStatus.NOT_STARTED,
                false,
                false
        );
        paymentEvents.create(event, java.util.List.of(order));

        var updated = updatePaymentOrderStatus.updateStatus(
                order.paymentOrderId(),
                PaymentOrderStatus.SUCCESS
        );

        assertThat(updated).get().extracting(PaymentOrder::status)
                .isEqualTo(PaymentOrderStatus.SUCCESS);
        assertThat(paymentOrders.findByPaymentOrderId(order.paymentOrderId()))
                .get()
                .extracting(PaymentOrder::status)
                .isEqualTo(PaymentOrderStatus.SUCCESS);
    }
}
