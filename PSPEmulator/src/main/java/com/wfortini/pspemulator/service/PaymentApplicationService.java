package com.wfortini.pspemulator.service;

import com.wfortini.pspemulator.controller.CreatePaymentRequest;
import com.wfortini.pspemulator.controller.CreatePaymentResponse;
import com.wfortini.pspemulator.event.CreatedPayment;
import com.wfortini.pspemulator.model.Payment;
import com.wfortini.pspemulator.model.PaymentStatus;
import com.wfortini.pspemulator.repository.PaymentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentApplicationService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentApplicationService(PaymentRepository paymentRepository,
                                     ApplicationEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        UUID externalId = UUID.randomUUID();
        Payment payment = new Payment(
                externalId,
                request.paymentOrderId(),
                request.buyerAccount(),
                request.amount(),
                request.currency(),
                PaymentStatus.SUCCESS
        );

        paymentRepository.save(payment);
        eventPublisher.publishEvent(new CreatedPayment(
                payment.getPaymentOrderId(),
                payment.getBuyerAccount(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getExternalId()
        ));

        return new CreatePaymentResponse(externalId);
    }
}
