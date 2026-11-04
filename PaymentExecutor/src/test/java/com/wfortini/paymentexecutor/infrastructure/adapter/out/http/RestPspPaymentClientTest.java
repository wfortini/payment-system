package com.wfortini.paymentexecutor.infrastructure.adapter.out.http;

import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withCreatedEntity;

class RestPspPaymentClientTest {

    @Test
    void shouldReturnExternalIdWhenPspAcceptsPayment() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://psp");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        UUID externalId = UUID.randomUUID();
        server.expect(requestTo("http://psp/api/v1/payments"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withCreatedEntity(URI.create("/api/v1/payments/" + externalId))
                        .body("{\"external_id\":\"%s\"}".formatted(externalId))
                        .contentType(MediaType.APPLICATION_JSON));

        var result = new RestPspPaymentClient(builder.build()).createPayment(paymentOrder());

        assertThat(result.accepted()).isTrue();
        assertThat(result.externalId()).isEqualTo(externalId);
        server.verify();
    }

    @Test
    void shouldReturnErrorMessageWhenPspRejectsPayment() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://psp");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("http://psp/api/v1/payments"))
                .andRespond(withBadRequest()
                        .body("{\"message\":\"invalid buyer_account\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        var result = new RestPspPaymentClient(builder.build()).createPayment(paymentOrder());

        assertThat(result.accepted()).isFalse();
        assertThat(result.errorMessage()).isEqualTo("invalid buyer_account");
        server.verify();
    }

    private static PaymentOrderExecution paymentOrder() {
        return new PaymentOrderExecution(
                UUID.fromString("181e536d-a140-46f8-a469-d8d9459d1b90"),
                "account-456",
                new BigDecimal("125.90"),
                Currency.getInstance("BRL"),
                UUID.fromString("b3b58ca2-326d-4f0a-bb0f-0af6bcfb7301"),
                PaymentOrderStatus.NOT_STARTED,
                false,
                false,
                null,
                null
        );
    }
}
