package com.wfortini.ledgerservice.infrastructure.adapter.in.kafka;

import com.wfortini.ledgerservice.application.port.in.ProcessPaymentUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentProcessedKafkaListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProcessedKafkaListener.class);

    private final ProcessPaymentUseCase processPayment;

    public PaymentProcessedKafkaListener(ProcessPaymentUseCase processPayment) {
        this.processPayment = processPayment;
    }

    @Transactional
    @KafkaListener(
            topics = "${ledger.kafka.payment-processed-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(PaymentProcessedEvent event) {
        var transaction = processPayment.process(event.toCommand());
        LOGGER.info(
                "Payment ledger transaction processed: paymentOrderId={}, transactionId={}, status={}",
                transaction.paymentOrderId(),
                transaction.transactionId(),
                transaction.status()
        );
    }
}
