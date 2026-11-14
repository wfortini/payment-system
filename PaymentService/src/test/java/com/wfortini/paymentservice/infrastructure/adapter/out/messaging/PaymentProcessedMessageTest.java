package com.wfortini.paymentservice.infrastructure.adapter.out.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentProcessedMessageTest {

    @Test
    void shouldSerializeUsingLedgerContract() {
        var message = new PaymentProcessedMessage(
                UUID.fromString("781e536d-a140-46f8-a469-d8d9459d1b90"),
                "SUCCESS",
                UUID.fromString("592f47db-027d-4d56-82e3-343ecf9e8bca"),
                UUID.fromString("a7f036ab-247f-4c10-984d-45f4e589142e"),
                new BigDecimal("10.00"),
                "payment sent with success",
                "PAYMENT",
                "BRL",
                UUID.fromString("381e536d-a140-46f8-a469-d8d9459d1b90")
        );

        var json = new String(
                new JacksonJsonSerializer<PaymentProcessedMessage>()
                        .serialize("payment_processed", message),
                StandardCharsets.UTF_8
        );

        assertThat(json)
                .contains("\"payment_order_id\":\"781e536d-a140-46f8-a469-d8d9459d1b90\"")
                .contains("\"status\":\"SUCCESS\"")
                .contains("\"seller_account\":\"592f47db-027d-4d56-82e3-343ecf9e8bca\"")
                .contains("\"buyer_account\":\"a7f036ab-247f-4c10-984d-45f4e589142e\"")
                .contains("\"amount\":10.00")
                .contains("\"transaction_type\":\"PAYMENT\"")
                .contains("\"currency\":\"BRL\"")
                .contains("\"external_id\":\"381e536d-a140-46f8-a469-d8d9459d1b90\"");
    }
}
