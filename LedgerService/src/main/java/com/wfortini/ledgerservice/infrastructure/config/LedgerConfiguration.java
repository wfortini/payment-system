package com.wfortini.ledgerservice.infrastructure.config;

import com.wfortini.ledgerservice.application.port.out.LedgerEntryRepository;
import com.wfortini.ledgerservice.application.service.LedgerApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class LedgerConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    LedgerApplicationService ledgerApplicationService(LedgerEntryRepository repository, Clock clock) {
        return new LedgerApplicationService(repository, clock);
    }
}
