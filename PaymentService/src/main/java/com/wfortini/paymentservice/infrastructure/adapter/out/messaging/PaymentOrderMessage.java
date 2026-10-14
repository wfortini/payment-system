package com.wfortini.paymentservice.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.paymentservice.domain.model.PaymentOrder;
import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;

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

    public static PaymentOrderMessage from(PaymentOrder paymentOrder) {
        return new PaymentOrderMessage(
                paymentOrder.paymentOrderId(),
                paymentOrder.buyerAccount(),
                paymentOrder.amount(),
                paymentOrder.currency().getCurrencyCode(),
                paymentOrder.checkoutId(),
                paymentOrder.status(),
                paymentOrder.ledgerUpdated(),
                paymentOrder.walletUpdated()
        );
    }
}
