# PaymentExecutor

Consumer de eventos de pagamento construído com Java 21, Spring Boot 4, Kafka e MongoDB, organizado com Arquitetura Hexagonal.

## Arquitetura

```text
domain/model                         Modelos e invariantes sem dependência de framework
application/port/in                  Caso de uso de processamento do evento
application/port/out                 Contrato de persistência
application/service                  Implementação do caso de uso
infrastructure/adapter/in/kafka      Adaptador de entrada Kafka
infrastructure/adapter/out/mongodb   Adaptador de saída MongoDB
infrastructure/config                Composição das dependências
```

O serviço consome o tópico existente `payment-events` e salva um documento por checkout no banco `executordb`, collection `payment-executors`. O `checkoutId` é o identificador do documento, tornando o reprocessamento do mesmo evento idempotente.

Os campos persistidos seguem `camelCase`, convenção usual para documentos MongoDB. Valores monetários são convertidos para `BigDecimal`, UUIDs permanecem tipados e `receivedAt` registra o momento do consumo em UTC.

## Executar localmente

Pré-requisitos: Java 21, Maven e MongoDB disponível em `localhost:27017`.

```bash
mvn spring-boot:run
```

Configurações sobrescrevíveis:

- `SPRING_KAFKA_BOOTSTRAP_SERVERS` — padrão `localhost:49092`
- `PAYMENT_EVENT_TOPIC` — padrão `payment-events`
- `PAYMENT_EXECUTOR_CONSUMER_GROUP` — padrão `payment-executor`
- `SPRING_MONGODB_URI` — padrão `mongodb://localhost:27017/executordb`

## Testes e build

```bash
mvn clean verify
```

## Imagem Docker com Maven

```bash
mvn compile jib:dockerBuild
```

A imagem local gerada será `payment-executor:0.0.1-SNAPSHOT`.

Para iniciar MongoDB e PaymentExecutor a partir do diretório pai:

```bash
docker compose up -d mongo payment-executor
```
