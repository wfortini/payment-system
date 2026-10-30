package com.wfortini.pspemulator.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreatePaymentResponse(
        @JsonProperty("external_id") UUID externalId
) {
}
