package com.wfortini.paymentservice.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wfortini.paymentservice.domain.model.PaymentEvent;
import com.wfortini.paymentservice.domain.model.PaymentOrder;

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

    public static PaymentEventMessage from(PaymentEvent paymentEvent, List<PaymentOrder> paymentOrders) {
        return new PaymentEventMessage(
                paymentEvent.checkoutId(),
                paymentEvent.buyerInfo(),
                paymentEvent.sellerInfo(),
                paymentEvent.creditCardInfo(),
                paymentEvent.paymentDone(),
                paymentOrders.stream().map(PaymentOrderMessage::from).toList()
        );
    }
}
