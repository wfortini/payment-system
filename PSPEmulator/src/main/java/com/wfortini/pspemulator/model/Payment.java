package com.wfortini.pspemulator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "external_id", nullable = false, updatable = false)
    private UUID externalId;

    @Column(name = "payment_order_id", nullable = false, unique = true, updatable = false)
    private UUID paymentOrderId;

    @Column(name = "buyer_account", nullable = false)
    private String buyerAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    protected Payment() {
    }

    public Payment(UUID externalId, UUID paymentOrderId, String buyerAccount,
                   BigDecimal amount, String currency, PaymentStatus status) {
        this.externalId = externalId;
        this.paymentOrderId = paymentOrderId;
        this.buyerAccount = buyerAccount;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public UUID getPaymentOrderId() {
        return paymentOrderId;
    }

    public String getBuyerAccount() {
        return buyerAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
