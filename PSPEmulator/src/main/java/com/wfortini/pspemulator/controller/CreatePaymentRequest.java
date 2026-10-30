package com.wfortini.pspemulator.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        @JsonProperty("payment_order_id")
        @NotNull(message = "invalid payment_order_id")
        UUID paymentOrderId,

        @JsonProperty("buyer_account")
        @NotBlank(message = "invalid buyer_account")
        String buyerAccount,

        @NotNull(message = "invalid amount")
        @DecimalMin(value = "0.01", message = "invalid amount")
        BigDecimal amount,

        @NotBlank(message = "invalid currency")
        String currency
) {
}
