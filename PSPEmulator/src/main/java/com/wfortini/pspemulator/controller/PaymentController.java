package com.wfortini.pspemulator.controller;

import com.wfortini.pspemulator.service.PaymentApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePaymentResponse create(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentApplicationService.createPayment(request);
    }
}
