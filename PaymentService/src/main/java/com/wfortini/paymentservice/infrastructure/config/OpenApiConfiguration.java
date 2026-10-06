package com.wfortini.paymentservice.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(
        info = @Info(
                title = "PaymentService API",
                version = "v1",
                description = "API para pagamentos e criação transacional de eventos com suas ordens.",
                contact = @Contact(name = "WFortini"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        ),
        tags = {
                @Tag(name = "Payments", description = "Operações básicas de pagamento"),
                @Tag(name = "Payment Events", description = "Eventos e respectivas ordens persistidos em conjunto")
        }
)
public class OpenApiConfiguration {
}
