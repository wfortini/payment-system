# PaymentService

API de pagamentos criada com Java 21, Spring Boot 4 e Maven, organizada segundo os limites da Clean Architecture.

## Arquitetura

```text
domain                    Regras e modelos de negócio, sem dependência de framework
application/port/in       Casos de uso expostos pela aplicação
application/port/out      Contratos exigidos pela aplicação
application/service       Implementação dos casos de uso
infrastructure/adapter/in Adaptador HTTP/REST
infrastructure/adapter/out Adaptadores de persistência em memória e PostgreSQL/JPA
infrastructure/config     Composição das dependências com Spring
```

As dependências apontam para dentro: a infraestrutura conhece a aplicação e o domínio, mas o domínio não conhece Spring, HTTP ou persistência.

## Executar

Pré-requisitos: Java 21 e Maven 3.6.3 ou superior.

```bash
mvn spring-boot:run
```

Criar um pagamento:

```bash
curl -i -X POST http://localhost:8080/api/v1/payments \
  -H 'Content-Type: application/json' \
  -d '{"amount":99.90,"currency":"BRL"}'
```

Consultar o pagamento usando o `id` retornado:

```bash
curl http://localhost:8080/api/v1/payments/{id}
```

## Testes e build

```bash
mvn clean verify
```

## Imagem Docker com Maven

O Jib Maven Plugin gera a imagem diretamente no Docker local; não é necessário manter um `Dockerfile`.

```bash
mvn compile jib:dockerBuild
```

A imagem gerada será `payment-service:0.0.1-SNAPSHOT`. Para executá-la:

```bash
docker run --rm -p 8080:8080 payment-service:0.0.1-SNAPSHOT
```

## PostgreSQL e Flyway

O schema é criado automaticamente pela migration `V1__create_payment_tables.sql`. As configurações padrão são:

```text
URL:      jdbc:postgresql://localhost:5432/payment_service
Usuário:  payment
Senha:    payment
```

As configurações podem ser alteradas com `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`.

Suba toda a stack a partir do diretório pai:

```bash
docker compose up -d
```

Serviços expostos:

- PaymentService: `http://localhost:8080`
- LedgerService: `http://localhost:8081`
- PostgreSQL: `localhost:5432`

Recursos persistentes:

- `POST /api/v1/payment-events` — cria o evento e suas ordens na mesma transação
- `GET /api/v1/payment-events/{checkoutId}`

## Swagger UI e OpenAPI

Com o serviço em execução, a documentação interativa fica disponível em:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificação OpenAPI (JSON): `http://localhost:8080/v3/api-docs`

Pela interface Swagger é possível consultar os contratos e executar requisições para pagamentos, eventos de pagamento e ordens de pagamento.

## Eventos Kafka

Ao criar um evento com suas ordens por `POST /api/v1/payment-events`, o PaymentService persiste todo o agregado na mesma transação e publica uma mensagem no tópico `payment-events`. O `checkout_id` é usado como chave Kafka.

Payload publicado:

```json
{
  "checkout_id": "checkout-123",
  "buyer_info": "buyer-123",
  "seller_info": "seller-456",
  "credit_card_info": "**** 1234",
  "is_payment_done": false
}
```

As configurações do producer estão no `application.properties` e podem ser sobrescritas por variáveis de ambiente:

- `SPRING_KAFKA_BOOTSTRAP_SERVERS` — padrão `localhost:49092`
- `PAYMENT_EVENT_TOPIC` — padrão `payment-events`

O producer utiliza confirmação `acks=all`, idempotência e serialização JSON.
