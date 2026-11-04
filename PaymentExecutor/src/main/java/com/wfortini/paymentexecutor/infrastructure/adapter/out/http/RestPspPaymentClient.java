package com.wfortini.paymentexecutor.infrastructure.adapter.out.http;

import com.wfortini.paymentexecutor.application.port.out.PspPaymentClient;
import com.wfortini.paymentexecutor.application.port.out.PspPaymentResult;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class RestPspPaymentClient implements PspPaymentClient {

    private static final String PAYMENTS_PATH = "/api/v1/payments";

    private final RestClient restClient;

    @Autowired
    public RestPspPaymentClient(@Value("${payment.psp.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    RestPspPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PspPaymentResult createPayment(PaymentOrderExecution paymentOrder) {
        try {
            var response = restClient.post()
                    .uri(PAYMENTS_PATH)
                    .body(CreatePspPaymentRequest.from(paymentOrder))
                    .retrieve()
                    .toEntity(CreatePspPaymentResponse.class);
            if (response.getStatusCode() != HttpStatus.CREATED || response.getBody() == null) {
                throw new IllegalStateException("PSP returned an invalid success response");
            }
            return PspPaymentResult.accepted(response.getBody().externalId());
        } catch (HttpClientErrorException.BadRequest exception) {
            PspErrorResponse error = exception.getResponseBodyAs(PspErrorResponse.class);
            String message = error == null || error.message() == null
                    ? "PSP rejected the payment" : error.message();
            return PspPaymentResult.rejected(message);
        }
    }
}
