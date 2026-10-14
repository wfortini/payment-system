package com.wfortini.paymentexecutor.infrastructure.adapter.in.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public record PaymentOrderMessage(
        @JsonProperty("payment_order_id") UUID paymentOrderId,
        @JsonProperty("buyer_account") String buyerAccount,
        @JsonProperty("amount") String amount,
        @JsonProperty("currency") String currency,
        @JsonProperty("checkout_id") UUID checkoutId,
        @JsonProperty("payment_order_status") PaymentOrderStatus paymentOrderStatus,
        @JsonProperty("ledger_updated") boolean ledgerUpdated,
        @JsonProperty("wallet_updated") boolean walletUpdated
) {

    PaymentOrderExecution toDomain() {
        return new PaymentOrderExecution(
                paymentOrderId,
                buyerAccount,
                new BigDecimal(amount),
                Currency.getInstance(currency.toUpperCase()),
                checkoutId,
                paymentOrderStatus,
                ledgerUpdated,
                walletUpdated
        );
    }
}
