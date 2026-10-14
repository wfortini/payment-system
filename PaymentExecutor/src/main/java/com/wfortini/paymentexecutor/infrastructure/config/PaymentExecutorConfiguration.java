package com.wfortini.paymentexecutor.infrastructure.config;

import com.wfortini.paymentexecutor.application.port.in.ProcessPaymentEventUseCase;
import com.wfortini.paymentexecutor.application.port.out.PaymentExecutionRepository;
import com.wfortini.paymentexecutor.application.service.PaymentExecutionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class PaymentExecutorConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    ProcessPaymentEventUseCase processPaymentEventUseCase(PaymentExecutionRepository repository) {
        return new PaymentExecutionService(repository);
    }
}
