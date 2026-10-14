package com.wfortini.paymentexecutor.infrastructure.adapter.out.mongodb;

import com.wfortini.paymentexecutor.domain.model.PaymentOrderExecution;
import com.wfortini.paymentexecutor.domain.model.PaymentOrderStatus;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentOrderDocument(
        UUID paymentOrderId,
        String buyerAccount,
        @Field(targetType = FieldType.DECIMAL128) BigDecimal amount,
        String currency,
        UUID checkoutId,
        PaymentOrderStatus paymentOrderStatus,
        boolean ledgerUpdated,
        boolean walletUpdated
) {

    static PaymentOrderDocument from(PaymentOrderExecution order) {
        return new PaymentOrderDocument(
                order.paymentOrderId(),
                order.buyerAccount(),
                order.amount(),
                order.currency().getCurrencyCode(),
                order.checkoutId(),
                order.status(),
                order.ledgerUpdated(),
                order.walletUpdated()
        );
    }

    PaymentOrderExecution toDomain() {
        return new PaymentOrderExecution(
                paymentOrderId,
                buyerAccount,
                amount,
                java.util.Currency.getInstance(currency),
                checkoutId,
                paymentOrderStatus,
                ledgerUpdated,
                walletUpdated
        );
    }
}
