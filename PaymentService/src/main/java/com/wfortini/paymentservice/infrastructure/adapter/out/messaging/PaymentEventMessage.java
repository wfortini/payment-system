package com.wfortini.paymentservice.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.paymentservice.domain.model.PaymentEvent;

public record PaymentEventMessage(
        @JsonProperty("checkout_id") String checkoutId,
        @JsonProperty("buyer_info") String buyerInfo,
        @JsonProperty("seller_info") String sellerInfo,
        @JsonProperty("credit_card_info") String creditCardInfo,
        @JsonProperty("is_payment_done") boolean paymentDone
) {

    public static PaymentEventMessage from(PaymentEvent paymentEvent) {
        return new PaymentEventMessage(
                paymentEvent.checkoutId(),
                paymentEvent.buyerInfo(),
                paymentEvent.sellerInfo(),
                paymentEvent.creditCardInfo(),
                paymentEvent.paymentDone()
        );
    }
}
