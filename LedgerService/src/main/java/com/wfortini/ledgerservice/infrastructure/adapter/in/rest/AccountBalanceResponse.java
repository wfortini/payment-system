package com.wfortini.ledgerservice.infrastructure.adapter.in.rest;

import com.wfortini.ledgerservice.domain.model.AccountBalance;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceResponse(UUID accountId, String currency, BigDecimal balance) {
    static AccountBalanceResponse from(AccountBalance accountBalance) {
        return new AccountBalanceResponse(
                accountBalance.accountId(),
                accountBalance.currency().getCurrencyCode(),
                accountBalance.balance()
        );
    }
}
