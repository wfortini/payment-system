package com.wfortini.paymentexecutor.infrastructure.adapter.in.rest;

import com.wfortini.paymentexecutor.application.port.in.UpdatePaymentOrderStatusUseCase;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentNotificationControllerTest {

    @Test
    void shouldForwardNotificationToUseCase() throws Exception {
        AtomicReference<UUID> capturedExternalId = new AtomicReference<>();
        AtomicReference<PaymentOrderStatus> capturedStatus = new AtomicReference<>();
        UpdatePaymentOrderStatusUseCase useCase = (externalId, status) -> {
            capturedExternalId.set(externalId);
            capturedStatus.set(status);
            return Optional.empty();
        };
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new PaymentNotificationController(useCase))
                .build();
        UUID externalId = UUID.randomUUID();

        mockMvc.perform(post("/v1/notifications/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "payment_order_id": "181e536d-a140-46f8-a469-d8d9459d1b90",
                                  "buyer_account": "account-456",
                                  "amount": "125.90",
                                  "currency": "BRL",
                                  "status": "SUCCESS",
                                  "external_id": "%s"
                                }
                                """.formatted(externalId)))
                .andExpect(status().isNotFound());

        assertThat(capturedExternalId).hasValue(externalId);
        assertThat(capturedStatus).hasValue(PaymentOrderStatus.SUCCESS);
    }
}
