package com.wfortini.paymentservice.application.service;

import com.wfortini.paymentservice.application.port.in.CreatePaymentCommand;
import com.wfortini.paymentservice.application.port.out.PaymentRepository;
import com.wfortini.paymentservice.domain.model.Payment;
import com.wfortini.paymentservice.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentApplicationServiceTest {

    @Test
    void shouldCreateAndRetrievePayment() {
        var repository = new TestPaymentRepository();
        var instant = Instant.parse("2026-01-01T12:00:00Z");
        var service = new PaymentApplicationService(repository, Clock.fixed(instant, ZoneOffset.UTC));

        var created = service.create(new CreatePaymentCommand(new BigDecimal("99.90"), "brl"));

        assertThat(created.amount()).isEqualByComparingTo("99.90");
        assertThat(created.currency().getCurrencyCode()).isEqualTo("BRL");
        assertThat(created.status()).isEqualTo(PaymentStatus.CREATED);
        assertThat(created.createdAt()).isEqualTo(instant);
        assertThat(service.findById(created.id())).contains(created);
    }

    private static final class TestPaymentRepository implements PaymentRepository {
        private final HashMap<UUID, Payment> payments = new HashMap<>();

        @Override
        public Payment save(Payment payment) {
            payments.put(payment.id(), payment);
            return payment;
        }

        @Override
        public Optional<Payment> findById(UUID id) {
            return Optional.ofNullable(payments.get(id));
        }
    }
}
