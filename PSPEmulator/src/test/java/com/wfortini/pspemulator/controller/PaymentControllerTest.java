package com.wfortini.pspemulator.controller;

import com.wfortini.pspemulator.service.PaymentApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest {

    private MockMvc mockMvc;
    private PaymentApplicationService paymentApplicationService;

    @BeforeEach
    void setUp() {
        paymentApplicationService = mock(PaymentApplicationService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PaymentController(paymentApplicationService))
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    void shouldCreatePayment() throws Exception {
        UUID externalId = UUID.randomUUID();
        when(paymentApplicationService.createPayment(any()))
                .thenReturn(new CreatePaymentResponse(externalId));

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "payment_order_id": "181e536d-a140-46f8-a469-d8d9459d1b90",
                                  "buyer_account": "account-456",
                                  "amount": "125.90",
                                  "currency": "BRL"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.external_id").value(externalId.toString()));
    }

    @Test
    void shouldRejectBlankBuyerAccount() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "payment_order_id": "181e536d-a140-46f8-a469-d8d9459d1b90",
                                  "buyer_account": "",
                                  "amount": "125.90",
                                  "currency": "BRL"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invalid buyer_account"));
    }
}
