package com.wfortini.paymentexecutor.infrastructure.adapter.out.mongodb;

import com.wfortini.paymentexecutor.domain.model.PaymentExecution;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "payment-executors")
@CompoundIndex(name = "payment_order_external_id_idx", def = "{'paymentOrders.externalId': 1}")
public record PaymentExecutionDocument(
        @Id UUID checkoutId,
        String buyerInfo,
        String sellerInfo,
        String creditCardInfo,
        boolean paymentDone,
        List<PaymentOrderDocument> paymentOrders,
        Instant receivedAt
) {

    static PaymentExecutionDocument from(PaymentExecution paymentExecution) {
        return new PaymentExecutionDocument(
                paymentExecution.checkoutId(),
                paymentExecution.buyerInfo(),
                paymentExecution.sellerInfo(),
                paymentExecution.creditCardInfo(),
                paymentExecution.paymentDone(),
                paymentExecution.paymentOrders().stream().map(PaymentOrderDocument::from).toList(),
                paymentExecution.receivedAt()
        );
    }

    PaymentExecution toDomain() {
        return new PaymentExecution(
                checkoutId,
                buyerInfo,
                sellerInfo,
                creditCardInfo,
                paymentDone,
                paymentOrders.stream().map(PaymentOrderDocument::toDomain).toList(),
                receivedAt
        );
    }
}
