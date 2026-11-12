package com.wfortini.ledgerservice;

import com.wfortini.ledgerservice.application.port.in.GetAccountBalanceUseCase;
import com.wfortini.ledgerservice.application.port.in.ProcessPaymentCommand;
import com.wfortini.ledgerservice.application.port.in.ProcessPaymentUseCase;
import com.wfortini.ledgerservice.domain.model.LedgerTransactionStatus;
import com.wfortini.ledgerservice.domain.model.PaymentProcessedStatus;
import com.wfortini.ledgerservice.domain.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LedgerPersistenceIntegrationTest {

    @Autowired
    private ProcessPaymentUseCase processPayment;

    @Autowired
    private GetAccountBalanceUseCase getBalance;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void shouldPersistOneTransactionAndTwoBalancedEntriesOnlyOnce() {
        var buyer = UUID.fromString("a7f036ab-247f-4c10-984d-45f4e589142e");
        var seller = UUID.fromString("592f47db-027d-4d56-82e3-343ecf9e8bca");
        var command = new ProcessPaymentCommand(
                UUID.fromString("781e536d-a140-46f8-a469-d8d9459d1b90"),
                PaymentProcessedStatus.SUCCESS,
                seller,
                buyer,
                new BigDecimal("10.00"),
                "payment sent with success",
                TransactionType.PAYMENT,
                "BRL",
                UUID.fromString("381e536d-a140-46f8-a469-d8d9459d1b90")
        );

        var first = processPayment.process(command);
        var duplicate = processPayment.process(command);

        assertThat(first.status()).isEqualTo(LedgerTransactionStatus.POSTED);
        assertThat(duplicate.transactionId()).isEqualTo(first.transactionId());
        assertThat(first.entries()).hasSize(2);
        assertThat(getBalance.getBalance(buyer, "BRL").balance()).isEqualByComparingTo("-10.00");
        assertThat(getBalance.getBalance(seller, "BRL").balance()).isEqualByComparingTo("10.00");
        assertThat(count("ledger_transaction")).isEqualTo(1);
        assertThat(count("ledger_entry")).isEqualTo(2);
    }

    private long count(String table) {
        return jdbcClient.sql("SELECT COUNT(*) FROM " + table).query(Long.class).single();
    }
}
