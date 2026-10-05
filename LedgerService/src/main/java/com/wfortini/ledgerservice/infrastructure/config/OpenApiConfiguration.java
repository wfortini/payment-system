package com.wfortini.ledgerservice.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(
        info = @Info(
                title = "LedgerService API",
                version = "v1",
                description = "API para lançamentos contábeis e consulta de saldos.",
                contact = @Contact(name = "WFortini"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        ),
        tags = {
                @Tag(name = "Ledger Entries", description = "Criação e consulta de créditos e débitos"),
                @Tag(name = "Account Balances", description = "Consulta de saldo por conta e moeda")
        }
)
public class OpenApiConfiguration {
}
