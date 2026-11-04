package com.wfortini.paymentexecutor.application.port.out;

import java.util.UUID;

public record PspPaymentResult(UUID externalId, String errorMessage) {

    public static PspPaymentResult accepted(UUID externalId) {
        return new PspPaymentResult(externalId, null);
    }

    public static PspPaymentResult rejected(String errorMessage) {
        return new PspPaymentResult(null, errorMessage);
    }

    public boolean accepted() {
        return externalId != null;
    }
}
