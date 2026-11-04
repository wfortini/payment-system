package com.wfortini.paymentexecutor.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferNotificationRequest(
        @JsonProperty("payment_order_id") @NotNull UUID paymentOrderId,
        @JsonProperty("buyer_account") @NotBlank String buyerAccount,
        @NotNull BigDecimal amount,
        @NotBlank String currency,
        @NotNull PaymentOrderStatus status,
        @JsonProperty("external_id") @NotNull UUID externalId
) {
}
