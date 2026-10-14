package com.wfortini.paymentservice.infrastructure.adapter.out.messaging;

import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentEventMessageTest {

    @Test
    void shouldSerializeUsingThePaymentEventContract() {
        var paymentEvent = new PaymentEvent(
                UUID.fromString("b3b58ca2-326d-4f0a-bb0f-0af6bcfb7301"),
                "buyer-123",
                "seller-456",
                "**** 1234",
                true
        );
        var paymentOrder = new PaymentOrder(
                UUID.fromString("181e536d-a140-46f8-a469-d8d9459d1b90"),
                "account-456",
                "125.90",
                Currency.getInstance("BRL"),
                paymentEvent.checkoutId(),
                PaymentOrderStatus.NOT_STARTED,
                false,
                true
        );
        var serializer = new JacksonJsonSerializer<PaymentEventMessage>();

        var json = new String(
                serializer.serialize("payment-events", PaymentEventMessage.from(paymentEvent, List.of(paymentOrder))),
                StandardCharsets.UTF_8
        );

        assertThat(json)
                .contains("\"checkout_id\":\"b3b58ca2-326d-4f0a-bb0f-0af6bcfb7301\"")
                .contains("\"buyer_info\":\"buyer-123\"")
                .contains("\"seller_info\":\"seller-456\"")
                .contains("\"credit_card_info\":\"**** 1234\"")
                .contains("\"is_payment_done\":true")
                .contains("\"payment_orders\":[{")
                .contains("\"payment_order_id\":\"181e536d-a140-46f8-a469-d8d9459d1b90\"")
                .contains("\"buyer_account\":\"account-456\"")
                .contains("\"amount\":\"125.90\"")
                .contains("\"currency\":\"BRL\"")
                .contains("\"checkout_id\":\"b3b58ca2-326d-4f0a-bb0f-0af6bcfb7301\"")
                .contains("\"payment_order_status\":\"NOT_STARTED\"")
                .contains("\"ledger_updated\":false")
                .contains("\"wallet_updated\":true");
    }
}
