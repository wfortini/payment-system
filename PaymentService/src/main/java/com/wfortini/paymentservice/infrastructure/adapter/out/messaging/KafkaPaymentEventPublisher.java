package com.wfortini.paymentservice.infrastructure.adapter.out.messaging;

import com.wfortini.paymentservice.application.port.out.PaymentEventPublisher;
import com.wfortini.paymentservice.domain.model.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPaymentEventPublisher implements PaymentEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPaymentEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaPaymentEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${payment.kafka.payment-event-topic}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(PaymentEvent paymentEvent) {
        var message = PaymentEventMessage.from(paymentEvent);

        kafkaTemplate.send(topic, paymentEvent.checkoutId(), message)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        LOGGER.error(
                                "Failed to publish payment event with checkoutId={} to topic={}",
                                paymentEvent.checkoutId(),
                                topic,
                                error
                        );
                        return;
                    }
                    LOGGER.info(
                            "Published payment event with checkoutId={} to topic={} partition={} offset={}",
                            paymentEvent.checkoutId(),
                            topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                });
    }
}
