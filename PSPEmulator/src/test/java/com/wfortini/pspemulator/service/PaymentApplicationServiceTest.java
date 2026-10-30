package com.wfortini.pspemulator.service;

import com.wfortini.pspemulator.controller.CreatePaymentRequest;
import com.wfortini.pspemulator.event.CreatedPayment;
import com.wfortini.pspemulator.model.Payment;
import com.wfortini.pspemulator.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentApplicationServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentApplicationService service;

    @Test
    void shouldPersistPaymentAndPublishEvent() {
        UUID paymentOrderId = UUID.randomUUID();
        CreatePaymentRequest request = new CreatePaymentRequest(
                paymentOrderId, "account-456", new BigDecimal("125.90"), "BRL"
        );

        var response = service.createPayment(request);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment payment = paymentCaptor.getValue();
        assertThat(payment.getExternalId()).isEqualTo(response.externalId());
        assertThat(payment.getPaymentOrderId()).isEqualTo(paymentOrderId);

        ArgumentCaptor<CreatedPayment> eventCaptor = ArgumentCaptor.forClass(CreatedPayment.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().externalId()).isEqualTo(response.externalId());
        assertThat(eventCaptor.getValue().status().name()).isEqualTo("SUCCESS");
    }
}
