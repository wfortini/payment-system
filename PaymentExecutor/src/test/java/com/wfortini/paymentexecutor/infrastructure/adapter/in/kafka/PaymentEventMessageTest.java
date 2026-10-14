package com.wfortini.paymentexecutor.infrastructure.adapter.in.kafka;

import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentEventMessageTest {

    @Test
    void shouldMapKafkaContractToDomain() {
        var checkoutId = UUID.fromString("b3b58ca2-326d-4f0a-bb0f-0af6bcfb7301");
        var receivedAt = Instant.parse("2026-10-13T12:00:00Z");
        var message = new PaymentEventMessage(
                checkoutId,
                "buyer-123",
                "seller-456",
                "**** 1234",
                false,
                List.of(new PaymentOrderMessage(
                        UUID.fromString("181e536d-a140-46f8-a469-d8d9459d1b90"),
                        "account-456",
                        "125.90",
                        "BRL",
                        checkoutId,
                        PaymentOrderStatus.NOT_STARTED,
                        false,
                        false
                ))
        );

        var result = message.toDomain(Clock.fixed(receivedAt, ZoneOffset.UTC));

        assertThat(result.checkoutId()).isEqualTo(checkoutId);
        assertThat(result.receivedAt()).isEqualTo(receivedAt);
        assertThat(result.paymentOrders()).singleElement().satisfies(order -> {
            assertThat(order.amount()).isEqualByComparingTo("125.90");
            assertThat(order.currency().getCurrencyCode()).isEqualTo("BRL");
            assertThat(order.status()).isEqualTo(PaymentOrderStatus.NOT_STARTED);
        });
    }
}
