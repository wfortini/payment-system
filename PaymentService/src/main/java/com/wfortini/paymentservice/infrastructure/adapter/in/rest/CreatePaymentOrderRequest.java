package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ordem de pagamento persistida junto com o evento")
public record CreatePaymentOrderRequest(
        @Schema(
                description = "Identificador UUID da conta do comprador",
                example = "a7f036ab-247f-4c10-984d-45f4e589142e"
        )
        @NotBlank
        @Pattern(
                regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
                message = "buyerAccount must be a valid UUID"
        )
        String buyerAccount,
        @Schema(description = "Valor conforme recebido pelo domínio", example = "125.90")
        @NotBlank @Size(max = 255) String amount,
        @Schema(description = "Código ISO 4217 da moeda", example = "BRL")
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency,
        @Schema(description = "Estado atual da ordem", example = "NOT_STARTED")
        @NotNull PaymentOrderStatus paymentOrderStatus,
        @Schema(description = "Indica atualização do ledger", example = "false") boolean ledgerUpdated,
        @Schema(description = "Indica atualização da wallet", example = "false") boolean walletUpdated
) {
}
