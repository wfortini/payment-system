package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import com.wfortini.paymentservice.application.port.in.PaymentEventUseCases;
import com.wfortini.paymentservice.application.port.in.PaymentOrderUseCases;
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
    private final PaymentOrderUseCases paymentOrders;

    public PaymentPersistenceController(PaymentEventUseCases paymentEvents, PaymentOrderUseCases paymentOrders) {
        this.paymentEvents = paymentEvents;
        this.paymentOrders = paymentOrders;
    }

    @PostMapping("/payment-events")
    @Tag(name = "Payment Events")
    @Operation(summary = "Registrar evento de pagamento", description = "Persiste um evento associado a um checkout.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento criado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Checkout já cadastrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<PaymentEvent> createEvent(@Valid @RequestBody CreatePaymentEventRequest request) {
        var event = paymentEvents.create(new PaymentEvent(
                request.checkoutId(), request.buyerInfo(), request.sellerInfo(),
                request.creditCardInfo(), request.paymentDone()
        ));
        return ResponseEntity.created(URI.create("/api/v1/payment-events/" + event.checkoutId())).body(event);
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

    @PostMapping("/payment-orders")
    @Tag(name = "Payment Orders")
    @Operation(summary = "Criar ordem de pagamento", description = "Persiste uma ordem vinculada a um evento existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ordem criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou checkout inexistente",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Ordem já cadastrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<PaymentOrder> createOrder(@Valid @RequestBody CreatePaymentOrderRequest request) {
        var order = paymentOrders.create(new PaymentOrder(
                request.paymentOrderId(), request.buyerAccount(), request.amount(),
                Currency.getInstance(request.currency().toUpperCase()), request.checkoutId(),
                request.paymentOrderStatus(), request.ledgerUpdated(), request.walletUpdated()
        ));
        return ResponseEntity.created(URI.create("/api/v1/payment-orders/" + order.paymentOrderId())).body(order);
    }

    @GetMapping("/payment-orders/{paymentOrderId}")
    @Tag(name = "Payment Orders")
    @Operation(summary = "Consultar ordem de pagamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ordem encontrada"),
            @ApiResponse(responseCode = "404", description = "Ordem não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    PaymentOrder findOrder(
            @Parameter(description = "Identificador da ordem", example = "order-123")
            @PathVariable String paymentOrderId
    ) {
        return paymentOrders.findByPaymentOrderId(paymentOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment order not found: " + paymentOrderId));
    }
}
