package com.wfortini.paymentexecutor.infrastructure.adapter.in.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.paymentexecutor.domain.model.PaymentExecution;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

public record PaymentEventMessage(
        @JsonProperty("checkout_id") UUID checkoutId,
        @JsonProperty("buyer_info") String buyerInfo,
        @JsonProperty("seller_info") String sellerInfo,
        @JsonProperty("credit_card_info") String creditCardInfo,
        @JsonProperty("is_payment_done") boolean paymentDone,
        @JsonProperty("payment_orders") List<PaymentOrderMessage> paymentOrders
) {

    PaymentExecution toDomain(Clock clock) {
        return new PaymentExecution(
                checkoutId,
                buyerInfo,
                sellerInfo,
                creditCardInfo,
                paymentDone,
                paymentOrders.stream().map(PaymentOrderMessage::toDomain).toList(),
                clock.instant()
        );
    }
}
