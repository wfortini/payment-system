package com.wfortini.paymentservice.application.service;

import com.wfortini.paymentservice.application.port.in.CreatePaymentCommand;
import com.wfortini.paymentservice.application.port.in.CreatePaymentUseCase;
import com.wfortini.paymentservice.application.port.in.GetPaymentUseCase;
import com.wfortini.paymentservice.application.port.out.PaymentRepository;
import com.wfortini.paymentservice.domain.model.Payment;
import com.wfortini.paymentservice.domain.model.PaymentStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PaymentApplicationService implements CreatePaymentUseCase, GetPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final Clock clock;

    public PaymentApplicationService(PaymentRepository paymentRepository, Clock clock) {
        this.paymentRepository = Objects.requireNonNull(paymentRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public Payment create(CreatePaymentCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        var currency = Currency.getInstance(command.currency().toUpperCase(Locale.ROOT));
        var payment = new Payment(
                UUID.randomUUID(),
                command.amount(),
                currency,
                PaymentStatus.CREATED,
                Instant.now(clock)
        );

        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return paymentRepository.findById(id);
    }
}
