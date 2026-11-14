# PaymentExecutor

Consumer de eventos de pagamento construído com Java 21, Spring Boot 4, Kafka e MongoDB, organizado com Arquitetura Hexagonal.

## Arquitetura

```text
domain/model                         Modelos e invariantes sem dependência de framework
application/port/in                  Casos de uso de processamento e callback
application/port/out                 Contratos de persistência, PSP e eventos
application/service                  Implementação do caso de uso
infrastructure/adapter/in/kafka      Adaptador de entrada Kafka
infrastructure/adapter/in/rest       Callback HTTP do PSP
infrastructure/adapter/out/http      Cliente HTTP do PSP
infrastructure/adapter/out/kafka     Producer de mudança da ordem
infrastructure/adapter/out/mongodb   Adaptador de saída MongoDB
infrastructure/config                Composição das dependências
```

O serviço consome o tópico existente `payment-events` e salva um documento por checkout no banco `executordb`, collection `payment-executors`. O `checkoutId` é o identificador do documento, tornando o reprocessamento do mesmo evento idempotente.

Os campos persistidos seguem `camelCase`, convenção usual para documentos MongoDB. Valores monetários são convertidos para `BigDecimal`, UUIDs permanecem tipados e `receivedAt` registra o momento do consumo em UTC.

## Fluxo de execução do pagamento

1. O evento recebido de `payment-events` é persistido no MongoDB.
2. Cada ordem é enviada para `POST /api/v1/payments` no PSP configurado.
3. Uma resposta `201 Created` preenche `externalId` e altera o status para `EXECUTING`.
4. Uma resposta `400 Bad Request` mantém o status atual e preenche `errorMessage`.
5. O PSP envia o resultado para `POST /v1/notifications/transfer`.
6. A ordem é localizada pelo `externalId`, tem seu status atualizado e gera um evento no tópico `payment-orders-event`.

O campo embutido `paymentOrders.externalId` possui o índice MongoDB `payment_order_external_id_idx`.

Evento publicado após o callback:

```json
{
  "payment_order_id": "181e536d-a140-46f8-a469-d8d9459d1b90",
  "external_id": "381e536d-a140-46f8-a469-d8d9459d1b90",
  "status": "SUCCESS",
  "message": null
}
```

O callback responde com `204 No Content` quando encontra a ordem ou `404 Not Found` quando o `external_id` não existe.

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
- `PSP_BASE_URL` — padrão `http://localhost:8082`
- `PAYMENT_ORDER_EVENT_TOPIC` — padrão `payment-orders-event`

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
