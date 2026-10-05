package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @Schema(description = "Valor do pagamento", example = "99.90", minimum = "0.01")
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @Schema(description = "Código ISO 4217 da moeda", example = "BRL", minLength = 3, maxLength = 3)
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency
) {
}
