package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ordem de pagamento persistida junto com o evento")
public record CreatePaymentOrderRequest(
        @Schema(description = "Identificador único da ordem", example = "order-123")
        @NotBlank @Size(max = 255) String paymentOrderId,
        @Schema(description = "Conta do comprador", example = "account-456")
        @NotBlank @Size(max = 255) String buyerAccount,
        @Schema(description = "Valor conforme recebido pelo domínio", example = "125.90")
        @NotBlank @Size(max = 255) String amount,
        @Schema(description = "Código ISO 4217 da moeda", example = "BRL")
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency,
        @Schema(description = "Checkout do evento pai; deve ser igual ao checkoutId do evento", example = "checkout-123")
        @NotBlank @Size(max = 255) String checkoutId,
        @Schema(description = "Estado atual da ordem", example = "NOT_STARTED")
        @NotNull PaymentOrderStatus paymentOrderStatus,
        @Schema(description = "Indica atualização do ledger", example = "false") boolean ledgerUpdated,
        @Schema(description = "Indica atualização da wallet", example = "false") boolean walletUpdated
) {
}
