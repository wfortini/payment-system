package com.wfortini.paymentservice.infrastructure.adapter.in.messaging;

import com.wfortini.paymentservice.application.port.in.UpdatePaymentOrderStatusUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ChangedPaymentOrderKafkaListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangedPaymentOrderKafkaListener.class);

    private final UpdatePaymentOrderStatusUseCase updatePaymentOrderStatus;

    public ChangedPaymentOrderKafkaListener(UpdatePaymentOrderStatusUseCase updatePaymentOrderStatus) {
        this.updatePaymentOrderStatus = updatePaymentOrderStatus;
    }

    @Transactional
    @KafkaListener(
            topics = "${payment.kafka.payment-order-event-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ChangedPaymentOrderMessage message) {
        var updatedPaymentOrder = updatePaymentOrderStatus.updateStatus(
                message.paymentOrderId(),
                message.status()
        );

        if (updatedPaymentOrder.isPresent()) {
            LOGGER.info(
                    "Payment order status updated: paymentOrderId={}, status={}",
                    message.paymentOrderId(),
                    message.status()
            );
            return;
        }

        LOGGER.warn(
                "Payment order not found for status update: paymentOrderId={}, status={}, message={}",
                message.paymentOrderId(),
                message.status(),
                message.message()
        );
    }
}
