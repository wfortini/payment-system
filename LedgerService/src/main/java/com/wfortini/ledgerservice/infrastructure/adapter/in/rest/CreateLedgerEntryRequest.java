package com.wfortini.ledgerservice.infrastructure.adapter.in.rest;

import com.wfortini.ledgerservice.domain.model.EntryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados necessários para criar um lançamento contábil")
public record CreateLedgerEntryRequest(
        @Schema(description = "Identificador da conta", example = "d9d1ac44-c1dd-4eb9-a784-cbced8d30190")
        @NotNull UUID accountId,
        @Schema(description = "Tipo do lançamento", example = "CREDIT") @NotNull EntryType type,
        @Schema(description = "Valor positivo do lançamento", example = "100.00", minimum = "0.01")
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @Schema(description = "Código ISO 4217 da moeda", example = "BRL")
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency,
        @Schema(description = "Descrição do lançamento", example = "Initial deposit")
        @NotBlank @Size(max = 200) String description
) {
}
