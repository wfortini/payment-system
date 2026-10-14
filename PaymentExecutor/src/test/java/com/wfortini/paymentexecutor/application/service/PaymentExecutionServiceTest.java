package com.wfortini.paymentexecutor.application.service;

import com.wfortini.paymentexecutor.application.port.out.PaymentExecutionRepository;
import com.wfortini.paymentexecutor.domain.model.PaymentExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentExecutionServiceTest {

    @Test
    void shouldPersistPaymentExecutionThroughOutputPort() {
        var checkoutId = UUID.fromString("b3b58ca2-326d-4f0a-bb0f-0af6bcfb7301");
        var paymentExecution = new PaymentExecution(
                checkoutId,
                "buyer-123",
                "seller-456",
                "**** 1234",
                false,
                List.of(new PaymentOrderExecution(
                        UUID.fromString("181e536d-a140-46f8-a469-d8d9459d1b90"),
                        "account-456",
                        new BigDecimal("125.90"),
                        Currency.getInstance("BRL"),
                        checkoutId,
                        PaymentOrderStatus.NOT_STARTED,
                        false,
                        false
                )),
                Instant.parse("2026-10-13T12:00:00Z")
        );
        var persisted = new AtomicReference<PaymentExecution>();
        PaymentExecutionRepository repository = execution -> {
            persisted.set(execution);
            return execution;
        };
        var service = new PaymentExecutionService(repository);

        var result = service.process(paymentExecution);

        assertThat(result).isEqualTo(paymentExecution);
        assertThat(persisted).hasValue(paymentExecution);
    }
}
