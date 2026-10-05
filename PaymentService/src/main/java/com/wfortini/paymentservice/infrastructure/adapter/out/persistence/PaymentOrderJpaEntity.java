package com.wfortini.paymentservice.infrastructure.adapter.out.persistence;

import com.wfortini.paymentservice.domain.model.PaymentOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_order")
class PaymentOrderJpaEntity {

    @Id
    @Column(name = "payment_order_id", nullable = false)
    String paymentOrderId;

    @Column(name = "buyer_account", nullable = false)
    String buyerAccount;

    @Column(name = "amount", nullable = false)
    String amount;

    @Column(name = "currency", nullable = false, length = 3)
    String currency;

    @Column(name = "checkout_id", nullable = false)
    String checkoutId;

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
            String paymentOrderId,
            String buyerAccount,
            String amount,
            String currency,
            String checkoutId,
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
