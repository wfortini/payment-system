package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para registrar um evento de pagamento")
public record CreatePaymentEventRequest(
        @Schema(description = "Identificador único do checkout", example = "checkout-123")
        @NotBlank @Size(max = 255) String checkoutId,
        @Schema(description = "Informações do comprador", example = "buyer-456")
        @NotBlank @Size(max = 2000) String buyerInfo,
        @Schema(description = "Informações do vendedor", example = "seller-789")
        @NotBlank @Size(max = 2000) String sellerInfo,
        @Schema(description = "Token ou dados mascarados do cartão", example = "****1111")
        @NotBlank @Size(max = 2000) String creditCardInfo,
        @Schema(description = "Indica se o pagamento foi concluído", example = "false") boolean paymentDone
) {
}
