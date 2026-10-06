package com.wfortini.paymentservice.infrastructure.adapter.out.messaging;

import com.wfortini.paymentservice.domain.model.PaymentEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentEventMessageTest {

    @Test
    void shouldSerializeUsingThePaymentEventContract() {
        var paymentEvent = new PaymentEvent(
                "checkout-123",
                "buyer-123",
                "seller-456",
                "**** 1234",
                true
        );
        var serializer = new JacksonJsonSerializer<PaymentEventMessage>();

        var json = new String(
                serializer.serialize("payment-events", PaymentEventMessage.from(paymentEvent)),
                StandardCharsets.UTF_8
        );

        assertThat(json)
                .contains("\"checkout_id\":\"checkout-123\"")
                .contains("\"buyer_info\":\"buyer-123\"")
                .contains("\"seller_info\":\"seller-456\"")
                .contains("\"credit_card_info\":\"**** 1234\"")
                .contains("\"is_payment_done\":true");
    }
}
