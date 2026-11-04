package com.wfortini.paymentexecutor.infrastructure.adapter.out.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

record CreatePspPaymentResponse(
        @JsonProperty("external_id") UUID externalId
) {
}
