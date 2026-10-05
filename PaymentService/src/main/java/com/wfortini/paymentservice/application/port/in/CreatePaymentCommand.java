package com.wfortini.paymentservice.application.port.in;

import java.math.BigDecimal;

public record CreatePaymentCommand(BigDecimal amount, String currency) {
}
