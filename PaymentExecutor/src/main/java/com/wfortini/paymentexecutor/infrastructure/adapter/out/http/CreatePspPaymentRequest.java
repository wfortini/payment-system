package com.wfortini.paymentexecutor.infrastructure.adapter.out.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;

import java.math.BigDecimal;
import java.util.UUID;

record CreatePspPaymentRequest(
        @JsonProperty("payment_order_id") UUID paymentOrderId,
        @JsonProperty("buyer_account") String buyerAccount,
        BigDecimal amount,
        String currency
) {
    static CreatePspPaymentRequest from(PaymentOrderExecution order) {
        return new CreatePspPaymentRequest(
                order.paymentOrderId(),
                order.buyerAccount(),
                order.amount(),
                order.currency().getCurrencyCode()
        );
    }
}
