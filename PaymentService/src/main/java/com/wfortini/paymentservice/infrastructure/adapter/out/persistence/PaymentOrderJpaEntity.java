package com.wfortini.paymentservice.infrastructure.adapter.out.persistence;

import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "payment_order")
class PaymentOrderJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "payment_order_id", nullable = false)
    UUID paymentOrderId;

    @Column(name = "buyer_account", nullable = false)
    String buyerAccount;

    @Column(name = "amount", nullable = false)
    String amount;

    @Column(name = "currency", nullable = false, length = 3)
    String currency;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "checkout_id", nullable = false)
    UUID checkoutId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_order_status", nullable = false, length = 32)
    PaymentOrderStatus status;

    @Column(name = "ledger_updated", nullable = false)
    boolean ledgerUpdated;

    @Column(name = "wallet_updated", nullable = false)
    boolean walletUpdated;

    protected PaymentOrderJpaEntity() {
    }

    PaymentOrderJpaEntity(
            UUID paymentOrderId,
            String buyerAccount,
            String amount,
            String currency,
            UUID checkoutId,
            PaymentOrderStatus status,
            boolean ledgerUpdated,
            boolean walletUpdated
    ) {
        this.paymentOrderId = paymentOrderId;
        this.buyerAccount = buyerAccount;
        this.amount = amount;
        this.currency = currency;
        this.checkoutId = checkoutId;
        this.status = status;
        this.ledgerUpdated = ledgerUpdated;
        this.walletUpdated = walletUpdated;
    }
}
