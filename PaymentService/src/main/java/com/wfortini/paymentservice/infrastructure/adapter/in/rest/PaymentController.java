package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

import com.wfortini.paymentservice.application.port.in.CreatePaymentCommand;
import com.wfortini.paymentservice.application.port.in.CreatePaymentUseCase;
import com.wfortini.paymentservice.application.port.in.GetPaymentUseCase;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;

    public PaymentController(CreatePaymentUseCase createPaymentUseCase, GetPaymentUseCase getPaymentUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.getPaymentUseCase = getPaymentUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar pagamento", description = "Cria um pagamento em memória com status inicial CREATED.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento criado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        var payment = createPaymentUseCase.create(new CreatePaymentCommand(request.amount(), request.currency()));
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(payment.id())
                .toUri();

        return ResponseEntity.created(location).body(PaymentResponse.from(payment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar pagamento", description = "Retorna um pagamento pelo identificador UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    PaymentResponse findById(
            @Parameter(description = "Identificador do pagamento", example = "d9d1ac44-c1dd-4eb9-a784-cbced8d30190")
            @PathVariable UUID id
    ) {
        return getPaymentUseCase.findById(id)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }
}
