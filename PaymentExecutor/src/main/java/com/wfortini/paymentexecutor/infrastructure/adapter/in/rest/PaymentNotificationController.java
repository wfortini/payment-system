package com.wfortini.paymentexecutor.infrastructure.adapter.in.rest;

import com.wfortini.paymentexecutor.application.port.in.UpdatePaymentOrderStatusUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notifications/transfer")
public class PaymentNotificationController {

    private final UpdatePaymentOrderStatusUseCase updatePaymentOrderStatus;

    public PaymentNotificationController(UpdatePaymentOrderStatusUseCase updatePaymentOrderStatus) {
        this.updatePaymentOrderStatus = updatePaymentOrderStatus;
    }

    @PostMapping
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferNotificationRequest request) {
        return updatePaymentOrderStatus.update(request.externalId(), request.status())
                .map(ignored -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
