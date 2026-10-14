package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import com.wfortini.paymentservice.application.port.in.PaymentEventUseCases;
import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentPersistenceControllerTest {

    @Test
    void shouldGenerateIdsAndPropagateCheckoutIdToOrders() {
        var useCases = mock(PaymentEventUseCases.class);
        var capturedOrders = new java.util.concurrent.atomic.AtomicReference<List<PaymentOrder>>();
        when(useCases.create(any(PaymentEvent.class), any())).thenAnswer(invocation -> {
            capturedOrders.set(invocation.getArgument(1));
            return invocation.getArgument(0);
        });
        var controller = new PaymentPersistenceController(useCases);
        var request = new CreatePaymentEventRequest(
                "buyer-456",
                "seller-789",
                "****1111",
                false,
                List.of(new CreatePaymentOrderRequest(
                        "account-456",
                        "125.90",
                        "BRL",
                        PaymentOrderStatus.NOT_STARTED,
                        false,
                        false
                ))
        );

        var response = controller.createEvent(request);

        var event = response.getBody();
        assertThat(event).isNotNull();
        assertThat(event.checkoutId()).isNotNull();
        assertThat(capturedOrders.get()).singleElement().satisfies(order -> {
            assertThat(order.paymentOrderId()).isNotNull();
            assertThat(order.checkoutId()).isEqualTo(event.checkoutId());
        });
    }
}
