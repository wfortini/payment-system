package com.wfortini.paymentexecutor.infrastructure.config;

import com.wfortini.paymentexecutor.application.port.out.ChangedPaymentOrderEventPublisher;
import com.wfortini.paymentexecutor.application.port.out.PaymentExecutionRepository;
import com.wfortini.paymentexecutor.application.port.out.PspPaymentClient;
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
    PaymentExecutionService paymentExecutionService(PaymentExecutionRepository repository,
                                                     PspPaymentClient pspPaymentClient,
                                                     ChangedPaymentOrderEventPublisher eventPublisher) {
        return new PaymentExecutionService(repository, pspPaymentClient, eventPublisher);
    }

}
