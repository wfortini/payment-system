package com.wfortini.paymentservice.infrastructure.adapter.in.messaging;

import com.wfortini.paymentservice.application.port.in.UpdatePaymentOrderStatusUseCase;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChangedPaymentOrderKafkaListenerTest {

    @Test
    void shouldDelegateStatusUpdateFromConsumedMessage() {
        var useCase = new CapturingUpdatePaymentOrderStatusUseCase();
        var listener = new ChangedPaymentOrderKafkaListener(useCase);
        var paymentOrderId = UUID.fromString("181e536d-a140-46f8-a469-d8d9459d1b90");

        listener.consume(new ChangedPaymentOrderMessage(
                paymentOrderId,
                PaymentOrderStatus.SUCCESS,
                null
        ));

        assertThat(useCase.paymentOrderId).isEqualTo(paymentOrderId);
        assertThat(useCase.status).isEqualTo(PaymentOrderStatus.SUCCESS);
    }

    private static final class CapturingUpdatePaymentOrderStatusUseCase
            implements UpdatePaymentOrderStatusUseCase {

        private UUID paymentOrderId;
        private PaymentOrderStatus status;

        @Override
        public Optional<PaymentOrder> updateStatus(UUID paymentOrderId, PaymentOrderStatus status) {
            this.paymentOrderId = paymentOrderId;
            this.status = status;
            return Optional.empty();
        }
    }
}
