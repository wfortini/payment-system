package com.wfortini.ledgerservice.infrastructure.adapter.in.rest;

import com.wfortini.ledgerservice.application.port.in.CreateLedgerEntryCommand;
import com.wfortini.ledgerservice.application.port.in.CreateLedgerEntryUseCase;
import com.wfortini.ledgerservice.application.port.in.GetAccountBalanceUseCase;
import com.wfortini.ledgerservice.application.port.in.GetLedgerEntryUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/ledger")
public class LedgerController {

    private final CreateLedgerEntryUseCase createEntry;
    private final GetLedgerEntryUseCase getEntry;
    private final GetAccountBalanceUseCase getBalance;

    public LedgerController(
            CreateLedgerEntryUseCase createEntry,
            GetLedgerEntryUseCase getEntry,
            GetAccountBalanceUseCase getBalance
    ) {
        this.createEntry = createEntry;
        this.getEntry = getEntry;
        this.getBalance = getBalance;
    }

    @PostMapping("/entries")
    @Tag(name = "Ledger Entries")
    @Operation(summary = "Criar lançamento contábil", description = "Registra um crédito ou débito para uma conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lançamento criado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<LedgerEntryResponse> create(@Valid @RequestBody CreateLedgerEntryRequest request) {
        var command = new CreateLedgerEntryCommand(
                request.accountId(),
                request.type(),
                request.amount(),
                request.currency(),
                request.description()
        );
        var entry = createEntry.create(command);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entry.id())
                .toUri();
        return ResponseEntity.created(location).body(LedgerEntryResponse.from(entry));
    }

    @GetMapping("/entries/{id}")
    @Tag(name = "Ledger Entries")
    @Operation(summary = "Consultar lançamento contábil")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lançamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Lançamento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    LedgerEntryResponse findById(
            @Parameter(description = "Identificador do lançamento", example = "d9d1ac44-c1dd-4eb9-a784-cbced8d30190")
            @PathVariable UUID id
    ) {
        return getEntry.findById(id)
                .map(LedgerEntryResponse::from)
                .orElseThrow(() -> new LedgerEntryNotFoundException(id));
    }

    @GetMapping("/accounts/{accountId}/balance")
    @Tag(name = "Account Balances")
    @Operation(summary = "Consultar saldo", description = "Calcula o saldo de uma conta para a moeda informada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo calculado"),
            @ApiResponse(responseCode = "400", description = "Conta ou moeda inválida",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    AccountBalanceResponse getBalance(
            @Parameter(description = "Identificador da conta", example = "d9d1ac44-c1dd-4eb9-a784-cbced8d30190")
            @PathVariable UUID accountId,
            @Parameter(description = "Código ISO 4217 da moeda", example = "BRL")
            @RequestParam @Pattern(regexp = "[A-Za-z]{3}") String currency
    ) {
        return AccountBalanceResponse.from(getBalance.getBalance(accountId, currency));
    }
}
