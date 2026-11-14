package com.wfortini.paymentexecutor.infrastructure.adapter.out.kafka;

import com.wfortini.paymentexecutor.domain.model.ChangedPaymentOrderEvent;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChangedPaymentOrderMessageTest {

    @Test
    void shouldSerializeExternalIdUsingSnakeCase() {
        var event = new ChangedPaymentOrderEvent(
                UUID.fromString("181e536d-a140-46f8-a469-d8d9459d1b90"),
                UUID.fromString("381e536d-a140-46f8-a469-d8d9459d1b90"),
                PaymentOrderStatus.SUCCESS,
                null
        );
        var serializer = new JacksonJsonSerializer<ChangedPaymentOrderMessage>();

        var json = new String(
                serializer.serialize("payment-orders-event", ChangedPaymentOrderMessage.from(event)),
                StandardCharsets.UTF_8
        );

        assertThat(json)
                .contains("\"payment_order_id\":\"181e536d-a140-46f8-a469-d8d9459d1b90\"")
                .contains("\"external_id\":\"381e536d-a140-46f8-a469-d8d9459d1b90\"")
                .contains("\"status\":\"SUCCESS\"");
    }
}
