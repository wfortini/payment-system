package com.wfortini.paymentservice.infrastructure.config;

import com.wfortini.paymentservice.application.port.out.PaymentRepository;
import com.wfortini.paymentservice.application.port.out.PaymentEventStore;
import com.wfortini.paymentservice.application.port.out.PaymentOrderStore;
import com.wfortini.paymentservice.application.service.PaymentApplicationService;
import com.wfortini.paymentservice.application.service.PaymentPersistenceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class PaymentConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    PaymentApplicationService paymentApplicationService(PaymentRepository repository, Clock clock) {
        return new PaymentApplicationService(repository, clock);
    }

    @Bean
    PaymentPersistenceService paymentPersistenceService(
            PaymentEventStore paymentEventStore,
            PaymentOrderStore paymentOrderStore
    ) {
        return new PaymentPersistenceService(paymentEventStore, paymentOrderStore);
    }
}
