package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Dados necessários para registrar um evento de pagamento")
public record CreatePaymentEventRequest(
        @Schema(description = "Informações do comprador", example = "buyer-456")
        @NotBlank @Size(max = 2000) String buyerInfo,
        @Schema(description = "Informações do vendedor", example = "seller-789")
        @NotBlank @Size(max = 2000) String sellerInfo,
        @JsonProperty("seller_account")
        @Schema(
                description = "Identificador UUID da conta do vendedor",
                example = "592f47db-027d-4d56-82e3-343ecf9e8bca"
        )
        @NotBlank
        @Pattern(
                regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
                message = "seller_account must be a valid UUID"
        )
        String sellerAccount,
        @Schema(description = "Token ou dados mascarados do cartão", example = "****1111")
        @NotBlank @Size(max = 2000) String creditCardInfo,
        @Schema(description = "Indica se o pagamento foi concluído", example = "false") boolean paymentDone,
        @ArraySchema(
                arraySchema = @Schema(description = "Ordens que serão persistidas junto com o evento"),
                schema = @Schema(implementation = CreatePaymentOrderRequest.class),
                minItems = 1
        )
        @NotEmpty List<@Valid CreatePaymentOrderRequest> paymentOrders
) {
}
