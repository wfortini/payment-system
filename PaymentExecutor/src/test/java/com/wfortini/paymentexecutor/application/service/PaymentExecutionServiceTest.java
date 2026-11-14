package com.wfortini.paymentexecutor.application.service;

import com.wfortini.paymentexecutor.application.port.out.PaymentExecutionRepository;
import com.wfortini.paymentexecutor.application.port.out.PspPaymentClient;
import com.wfortini.paymentexecutor.application.port.out.PspPaymentResult;
import com.wfortini.paymentexecutor.domain.model.ChangedPaymentOrderEvent;
import com.wfortini.paymentexecutor.domain.model.PaymentExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentExecutionServiceTest {

    @Test
    void shouldPersistThenSendOrderToPspAndMarkItAsExecuting() {
        PaymentExecution paymentExecution = paymentExecution();
        UUID externalId = UUID.randomUUID();
        InMemoryRepository repository = new InMemoryRepository();
        PspPaymentClient pspClient = ignored -> PspPaymentResult.accepted(externalId);
        List<ChangedPaymentOrderEvent> events = new ArrayList<>();
        var service = new PaymentExecutionService(repository, pspClient, events::add);

        PaymentExecution result = service.process(paymentExecution);

        assertThat(repository.saveCount).isEqualTo(2);
        assertThat(result.paymentOrders()).singleElement().satisfies(order -> {
            assertThat(order.status()).isEqualTo(PaymentOrderStatus.EXECUTING);
            assertThat(order.externalId()).isEqualTo(externalId);
            assertThat(order.errorMessage()).isNull();
        });
    }

    @Test
    void shouldStorePspErrorMessageOnBadRequest() {
        InMemoryRepository repository = new InMemoryRepository();
        PspPaymentClient pspClient = ignored -> PspPaymentResult.rejected("invalid buyer_account");
        var service = new PaymentExecutionService(repository, pspClient, ignored -> { });

        PaymentExecution result = service.process(paymentExecution());

        assertThat(result.paymentOrders()).singleElement().satisfies(order -> {
            assertThat(order.status()).isEqualTo(PaymentOrderStatus.NOT_STARTED);
            assertThat(order.externalId()).isNull();
            assertThat(order.errorMessage()).isEqualTo("invalid buyer_account");
        });
    }

    @Test
    void shouldUpdateOrderByExternalIdAndPublishChangedEvent() {
        UUID externalId = UUID.randomUUID();
        PaymentExecution original = paymentExecution();
        PaymentExecution acceptedExecution = original.replaceOrder(
                original.paymentOrders().getFirst().acceptedByPsp(externalId)
        );
        InMemoryRepository repository = new InMemoryRepository();
        repository.current = acceptedExecution;
        List<ChangedPaymentOrderEvent> events = new ArrayList<>();
        var service = new PaymentExecutionService(
                repository, ignored -> PspPaymentResult.accepted(externalId), events::add
        );

        Optional<PaymentOrderExecution> updated = service.update(externalId, PaymentOrderStatus.SUCCESS);

        assertThat(updated).isPresent().get().extracting(PaymentOrderExecution::status)
                .isEqualTo(PaymentOrderStatus.SUCCESS);
        assertThat(events).singleElement().satisfies(event -> {
            assertThat(event.paymentOrderId()).isEqualTo(original.paymentOrders().getFirst().paymentOrderId());
            assertThat(event.externalId()).isEqualTo(externalId);
            assertThat(event.status()).isEqualTo(PaymentOrderStatus.SUCCESS);
            assertThat(event.message()).isNull();
        });
    }

    private static PaymentExecution paymentExecution() {
        UUID checkoutId = UUID.fromString("b3b58ca2-326d-4f0a-bb0f-0af6bcfb7301");
        return new PaymentExecution(
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
                        false,
                        null,
                        null
                )),
                Instant.parse("2026-10-13T12:00:00Z")
        );
    }

    private static final class InMemoryRepository implements PaymentExecutionRepository {
        private PaymentExecution current;
        private int saveCount;

        @Override
        public PaymentExecution save(PaymentExecution paymentExecution) {
            current = paymentExecution;
            saveCount++;
            return paymentExecution;
        }

        @Override
        public Optional<PaymentExecution> findByExternalId(UUID externalId) {
            if (current == null) {
                return Optional.empty();
            }
            return current.paymentOrders().stream().anyMatch(order -> externalId.equals(order.externalId()))
                    ? Optional.of(current) : Optional.empty();
        }
    }
}
