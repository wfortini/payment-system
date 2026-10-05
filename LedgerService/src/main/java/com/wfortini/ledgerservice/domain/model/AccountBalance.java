package com.wfortini.ledgerservice.domain.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public record AccountBalance(UUID accountId, Currency currency, BigDecimal balance) {
    public AccountBalance {
        Objects.requireNonNull(accountId, "accountId must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        Objects.requireNonNull(balance, "balance must not be null");
    }
}
