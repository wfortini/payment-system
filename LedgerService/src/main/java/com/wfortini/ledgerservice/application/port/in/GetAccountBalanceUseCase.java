package com.wfortini.ledgerservice.application.port.in;

import com.wfortini.ledgerservice.domain.model.AccountBalance;

import java.util.UUID;

public interface GetAccountBalanceUseCase {

    AccountBalance getBalance(UUID accountId, String currency);
}
