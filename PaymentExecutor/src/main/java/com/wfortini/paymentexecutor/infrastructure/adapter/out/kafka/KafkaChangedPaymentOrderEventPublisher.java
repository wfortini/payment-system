package com.wfortini.paymentexecutor.infrastructure.adapter.out.kafka;

import com.wfortini.paymentexecutor.application.port.out.ChangedPaymentOrderEventPublisher;
import com.wfortini.paymentexecutor.domain.model.ChangedPaymentOrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaChangedPaymentOrderEventPublisher implements ChangedPaymentOrderEventPublisher {

    private final KafkaTemplate<String, ChangedPaymentOrderMessage> kafkaTemplate;
    private final String topic;

    public KafkaChangedPaymentOrderEventPublisher(
            KafkaTemplate<String, ChangedPaymentOrderMessage> kafkaTemplate,
            @Value("${payment.kafka.payment-order-event-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(ChangedPaymentOrderEvent event) {
        kafkaTemplate.send(
                topic,
                event.paymentOrderId().toString(),
                ChangedPaymentOrderMessage.from(event)
        );
    }
}
