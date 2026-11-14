package com.wfortini.paymentexecutor.application.service;

import com.wfortini.paymentexecutor.application.port.in.ProcessPaymentEventUseCase;
import com.wfortini.paymentexecutor.application.port.in.UpdatePaymentOrderStatusUseCase;
import com.wfortini.paymentexecutor.application.port.out.ChangedPaymentOrderEventPublisher;
import com.wfortini.paymentexecutor.application.port.out.PaymentExecutionRepository;
import com.wfortini.paymentexecutor.application.port.out.PspPaymentClient;
import com.wfortini.paymentexecutor.domain.model.ChangedPaymentOrderEvent;
import com.wfortini.paymentexecutor.domain.model.PaymentExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;

import java.util.Optional;
import java.util.Objects;
import java.util.UUID;

public final class PaymentExecutionService implements ProcessPaymentEventUseCase, UpdatePaymentOrderStatusUseCase {

    private final PaymentExecutionRepository repository;
    private final PspPaymentClient pspPaymentClient;
    private final ChangedPaymentOrderEventPublisher eventPublisher;

    public PaymentExecutionService(PaymentExecutionRepository repository,
                                   PspPaymentClient pspPaymentClient,
                                   ChangedPaymentOrderEventPublisher eventPublisher) {
        this.repository = Objects.requireNonNull(repository);
        this.pspPaymentClient = Objects.requireNonNull(pspPaymentClient);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public PaymentExecution process(PaymentExecution paymentExecution) {
        PaymentExecution persisted = repository.save(paymentExecution);

        for (PaymentOrderExecution order : persisted.paymentOrders()) {
            var result = pspPaymentClient.createPayment(order);
            PaymentOrderExecution updatedOrder = result.accepted()
                    ? order.acceptedByPsp(result.externalId())
                    : order.rejectedByPsp(result.errorMessage());
            persisted = repository.save(persisted.replaceOrder(updatedOrder));
        }

        return persisted;
    }

    @Override
    public Optional<PaymentOrderExecution> update(UUID externalId,
                                                   com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus status) {
        return repository.findByExternalId(externalId).map(execution -> {
            PaymentOrderExecution order = execution.paymentOrders().stream()
                    .filter(candidate -> externalId.equals(candidate.externalId()))
                    .findFirst()
                    .orElseThrow();
            PaymentOrderExecution updatedOrder = order.withStatus(status);
            repository.save(execution.replaceOrder(updatedOrder));
            eventPublisher.publish(new ChangedPaymentOrderEvent(
                    updatedOrder.paymentOrderId(), updatedOrder.externalId(),
                    updatedOrder.status(), updatedOrder.errorMessage()
            ));
            return updatedOrder;
        });
    }
}
