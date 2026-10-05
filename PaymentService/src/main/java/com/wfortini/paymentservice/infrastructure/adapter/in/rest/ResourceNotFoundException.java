package com.wfortini.paymentservice.infrastructure.adapter.in.rest;

final class ResourceNotFoundException extends RuntimeException {

    ResourceNotFoundException(String message) {
        super(message);
    }
}
