package com.wfortini.paymentservice.infrastructure.adapter.out.messaging;

import com.wfortini.paymentservice.application.port.out.PaymentProcessedEventPublisher;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class KafkaPaymentProcessedEventPublisher implements PaymentProcessedEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPaymentProcessedEventPublisher.class);
    private static final String SUCCESS = "SUCCESS";
    private static final String PAYMENT = "PAYMENT";
    private static final String DESCRIPTION = "payment sent with success";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaPaymentProcessedEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${payment.kafka.payment-processed-topic}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(PaymentOrder paymentOrder, UUID externalId) {
        var message = new PaymentProcessedMessage(
                paymentOrder.paymentOrderId(),
                SUCCESS,
                paymentOrder.sellerAccount(),
                toUuid(paymentOrder.buyerAccount()),
                new BigDecimal(paymentOrder.amount()),
                DESCRIPTION,
                PAYMENT,
                paymentOrder.currency().getCurrencyCode(),
                externalId
        );

        var result = kafkaTemplate.send(
                topic,
                paymentOrder.paymentOrderId().toString(),
                message
        ).join();
        LOGGER.info(
                "Published processed payment: paymentOrderId={}, externalId={}, topic={}, partition={}, offset={}",
                paymentOrder.paymentOrderId(),
                externalId,
                topic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
        );
    }

    private UUID toUuid(String buyerAccount) {
        try {
            return UUID.fromString(buyerAccount);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("buyerAccount must be a valid UUID to publish to the ledger", exception);
        }
    }
}
