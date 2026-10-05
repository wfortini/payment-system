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
                description = "API para pagamentos, eventos de pagamento e ordens de pagamento.",
                contact = @Contact(name = "WFortini"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        ),
        tags = {
                @Tag(name = "Payments", description = "Operações básicas de pagamento"),
                @Tag(name = "Payment Events", description = "Eventos persistidos de checkout"),
                @Tag(name = "Payment Orders", description = "Ordens persistidas de pagamento")
        }
)
public class OpenApiConfiguration {
}
