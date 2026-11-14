package com.wfortini.paymentservice.infrastructure.adapter.in.messaging;

import com.wfortini.paymentservice.application.port.in.UpdatePaymentOrderStatusUseCase;
import com.wfortini.paymentservice.application.port.out.PaymentProcessedEventPublisher;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ChangedPaymentOrderKafkaListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangedPaymentOrderKafkaListener.class);

    private final UpdatePaymentOrderStatusUseCase updatePaymentOrderStatus;
    private final PaymentProcessedEventPublisher paymentProcessedEventPublisher;

    public ChangedPaymentOrderKafkaListener(
            UpdatePaymentOrderStatusUseCase updatePaymentOrderStatus,
            PaymentProcessedEventPublisher paymentProcessedEventPublisher
    ) {
        this.updatePaymentOrderStatus = updatePaymentOrderStatus;
        this.paymentProcessedEventPublisher = paymentProcessedEventPublisher;
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
            if (message.status() == PaymentOrderStatus.SUCCESS) {
                paymentProcessedEventPublisher.publish(updatedPaymentOrder.get(), message.externalId());
            }
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
