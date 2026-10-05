package com.wfortini.paymentservice.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_event")
class PaymentEventJpaEntity {

    @Id
    @Column(name = "checkout_id", nullable = false)
    String checkoutId;

    @Column(name = "buyer_info", nullable = false, length = 2000)
    String buyerInfo;

    @Column(name = "seller_info", nullable = false, length = 2000)
    String sellerInfo;

    @Column(name = "credit_card_info", nullable = false, length = 2000)
    String creditCardInfo;

    @Column(name = "is_payment_done", nullable = false)
    boolean paymentDone;

    protected PaymentEventJpaEntity() {
    }

    PaymentEventJpaEntity(
            String checkoutId,
            String buyerInfo,
            String sellerInfo,
            String creditCardInfo,
            boolean paymentDone
    ) {
        this.checkoutId = checkoutId;
        this.buyerInfo = buyerInfo;
        this.sellerInfo = sellerInfo;
        this.creditCardInfo = creditCardInfo;
        this.paymentDone = paymentDone;
    }
}
