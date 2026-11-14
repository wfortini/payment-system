package com.wfortini.paymentservice.infrastructure.adapter.in.messaging;

import com.wfortini.paymentservice.application.port.in.UpdatePaymentOrderStatusUseCase;
import com.wfortini.paymentservice.application.port.out.PaymentProcessedEventPublisher;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChangedPaymentOrderKafkaListenerTest {

    @Test
    void shouldUpdateStatusAndPublishSuccessfulPayment() {
        var useCase = new CapturingUpdatePaymentOrderStatusUseCase(true);
        var publisher = new CapturingPaymentProcessedEventPublisher();
        var listener = new ChangedPaymentOrderKafkaListener(useCase, publisher);
        var paymentOrderId = UUID.fromString("181e536d-a140-46f8-a469-d8d9459d1b90");
        var externalId = UUID.fromString("381e536d-a140-46f8-a469-d8d9459d1b90");

        listener.consume(new ChangedPaymentOrderMessage(
                paymentOrderId,
                externalId,
                PaymentOrderStatus.SUCCESS,
                null
        ));

        assertThat(useCase.paymentOrderId).isEqualTo(paymentOrderId);
        assertThat(useCase.status).isEqualTo(PaymentOrderStatus.SUCCESS);
        assertThat(publisher.paymentOrder).isNotNull();
        assertThat(publisher.paymentOrder.paymentOrderId()).isEqualTo(paymentOrderId);
        assertThat(publisher.externalId).isEqualTo(externalId);
    }

    @Test
    void shouldNotPublishWhenStatusIsNotSuccessful() {
        var useCase = new CapturingUpdatePaymentOrderStatusUseCase(true);
        var publisher = new CapturingPaymentProcessedEventPublisher();
        var listener = new ChangedPaymentOrderKafkaListener(useCase, publisher);

        listener.consume(new ChangedPaymentOrderMessage(
                UUID.fromString("181e536d-a140-46f8-a469-d8d9459d1b90"),
                UUID.fromString("381e536d-a140-46f8-a469-d8d9459d1b90"),
                PaymentOrderStatus.FAILED,
                "payment failed"
        ));

        assertThat(publisher.paymentOrder).isNull();
    }

    private static final class CapturingUpdatePaymentOrderStatusUseCase
            implements UpdatePaymentOrderStatusUseCase {

        private UUID paymentOrderId;
        private PaymentOrderStatus status;
        private final boolean orderExists;

        private CapturingUpdatePaymentOrderStatusUseCase(boolean orderExists) {
            this.orderExists = orderExists;
        }

        @Override
        public Optional<PaymentOrder> updateStatus(UUID paymentOrderId, PaymentOrderStatus status) {
            this.paymentOrderId = paymentOrderId;
            this.status = status;
            if (!orderExists) {
                return Optional.empty();
            }
            return Optional.of(new PaymentOrder(
                    paymentOrderId,
                    "a7f036ab-247f-4c10-984d-45f4e589142e",
                    UUID.fromString("592f47db-027d-4d56-82e3-343ecf9e8bca"),
                    "10.00",
                    Currency.getInstance("BRL"),
                    UUID.fromString("b3b58ca2-326d-4f0a-bb0f-0af6bcfb7301"),
                    status,
                    false,
                    false
            ));
        }
    }

    private static final class CapturingPaymentProcessedEventPublisher
            implements PaymentProcessedEventPublisher {

        private PaymentOrder paymentOrder;
        private UUID externalId;

        @Override
        public void publish(PaymentOrder paymentOrder, UUID externalId) {
            this.paymentOrder = paymentOrder;
            this.externalId = externalId;
        }
    }
}
