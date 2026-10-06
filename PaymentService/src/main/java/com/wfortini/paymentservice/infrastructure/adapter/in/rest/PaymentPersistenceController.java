package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import com.wfortini.paymentservice.application.port.in.PaymentEventUseCases;
import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Currency;

@RestController
@RequestMapping("/api/v1")
public class PaymentPersistenceController {

    private final PaymentEventUseCases paymentEvents;

    public PaymentPersistenceController(PaymentEventUseCases paymentEvents) {
        this.paymentEvents = paymentEvents;
    }

    @PostMapping("/payment-events")
    @Tag(name = "Payment Events")
    @Operation(
            summary = "Registrar evento e ordens de pagamento",
            description = "Persiste o evento e todas as suas ordens de pagamento na mesma transação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento e ordens criados"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Checkout já cadastrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<PaymentEvent> createEvent(@Valid @RequestBody CreatePaymentEventRequest request) {
        var event = new PaymentEvent(
                request.checkoutId(), request.buyerInfo(), request.sellerInfo(),
                request.creditCardInfo(), request.paymentDone()
        );
        var orders = request.paymentOrders().stream()
                .map(this::toPaymentOrder)
                .toList();
        var savedEvent = paymentEvents.create(event, orders);
        return ResponseEntity.created(URI.create("/api/v1/payment-events/" + savedEvent.checkoutId())).body(savedEvent);
    }

    @GetMapping("/payment-events/{checkoutId}")
    @Tag(name = "Payment Events")
    @Operation(summary = "Consultar evento de pagamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    PaymentEvent findEvent(
            @Parameter(description = "Identificador do checkout", example = "checkout-123")
            @PathVariable String checkoutId
    ) {
        return paymentEvents.findByCheckoutId(checkoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment event not found: " + checkoutId));
    }

    private PaymentOrder toPaymentOrder(CreatePaymentOrderRequest request) {
        return new PaymentOrder(
                request.paymentOrderId(), request.buyerAccount(), request.amount(),
                Currency.getInstance(request.currency().toUpperCase()), request.checkoutId(),
                request.paymentOrderStatus(), request.ledgerUpdated(), request.walletUpdated()
        );
    }
}
