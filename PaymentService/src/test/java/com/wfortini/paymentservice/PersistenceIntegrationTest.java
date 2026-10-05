package com.wfortini.paymentservice;

import com.wfortini.paymentservice.application.port.in.PaymentEventUseCases;
import com.wfortini.paymentservice.application.port.in.PaymentOrderUseCases;
import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PersistenceIntegrationTest {

    @Autowired
    private PaymentEventUseCases paymentEvents;

    @Autowired
    private PaymentOrderUseCases paymentOrders;

    @Test
    void shouldPersistPaymentEventAndOrder() {
        var event = paymentEvents.create(new PaymentEvent(
                "checkout-integration-test",
                "buyer",
                "seller",
                "masked-card",
                false
        ));
        var order = paymentOrders.create(new PaymentOrder(
                "order-integration-test",
                "buyer-account",
                "125.90",
                Currency.getInstance("BRL"),
                event.checkoutId(),
                PaymentOrderStatus.NOT_STARTED,
                false,
                false
        ));

        assertThat(paymentEvents.findByCheckoutId(event.checkoutId())).contains(event);
        assertThat(paymentOrders.findByPaymentOrderId(order.paymentOrderId())).contains(order);
    }
}
