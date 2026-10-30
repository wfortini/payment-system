package com.wfortini.pspemulator.event;

import com.wfortini.pspemulator.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CreatePaymentEventSubscribesTest {

    @Test
    void shouldPostCreatedPaymentNotification() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://notification-service");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        CreatePaymentEventSubscribes subscriber = new CreatePaymentEventSubscribes(builder.build());
        UUID paymentOrderId = UUID.randomUUID();
        UUID externalId = UUID.randomUUID();
        CreatedPayment event = new CreatedPayment(
                paymentOrderId, "account-456", new BigDecimal("125.90"),
                "BRL", PaymentStatus.SUCCESS, externalId
        );

        server.expect(once(), requestTo("http://notification-service/v1/notifications/transfer"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "payment_order_id": "%s",
                          "buyer_account": "account-456",
                          "amount": 125.90,
                          "currency": "BRL",
                          "status": "SUCCESS",
                          "external_id": "%s"
                        }
                        """.formatted(paymentOrderId, externalId)))
                .andRespond(withSuccess());

        subscriber.onCreatedPayment(event);

        server.verify();
    }
}
