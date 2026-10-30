package com.wfortini.pspemulator.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestClient;

@Component
public class CreatePaymentEventSubscribes {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePaymentEventSubscribes.class);
    private static final String NOTIFICATION_PATH = "/v1/notifications/transfer";

    private final RestClient notificationRestClient;

    public CreatePaymentEventSubscribes(RestClient notificationRestClient) {
        this.notificationRestClient = notificationRestClient;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreatedPayment(CreatedPayment event) {
        notificationRestClient.post()
                .uri(NOTIFICATION_PATH)
                .body(event)
                .retrieve()
                .toBodilessEntity();
        LOGGER.info("Payment notification sent for externalId={}", event.externalId());
    }
}
