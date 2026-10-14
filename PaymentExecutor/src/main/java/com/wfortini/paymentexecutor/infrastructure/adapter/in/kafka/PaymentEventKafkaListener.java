package com.wfortini.paymentexecutor.infrastructure.adapter.in.kafka;

import com.wfortini.paymentexecutor.application.port.in.ProcessPaymentEventUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class PaymentEventKafkaListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventKafkaListener.class);

    private final ProcessPaymentEventUseCase processPaymentEvent;
    private final Clock clock;

    public PaymentEventKafkaListener(ProcessPaymentEventUseCase processPaymentEvent, Clock clock) {
        this.processPaymentEvent = processPaymentEvent;
        this.clock = clock;
    }

    @KafkaListener(topics = "${payment.kafka.payment-event-topic}",
            groupId = "payment-executor" )
    public void consume(PaymentEventMessage message) {
        var paymentExecution = processPaymentEvent.process(message.toDomain(clock));
        LOGGER.info(
                "Payment event persisted for checkoutId={} with {} orders",
                paymentExecution.checkoutId(),
                paymentExecution.paymentOrders().size()
        );
    }
}
