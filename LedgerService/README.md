# LedgerService

Ledger contábil construído com Java 21, Spring Boot 4, Kafka, PostgreSQL, JPA e Flyway, organizado com Clean Architecture.

## Double-entry

O serviço consome eventos do tópico `payment_processed`. Para cada pagamento com status `SUCCESS`, persiste atomicamente:

- uma `ledger_transaction` com status `POSTED`;
- um lançamento `DEBIT` na conta do comprador;
- um lançamento `CREDIT` na conta do vendedor.

Os débitos e créditos precisam usar a mesma moeda e ter totais iguais. O domínio rejeita uma transação `POSTED` desbalanceada antes da persistência.

Eventos diferentes de `SUCCESS` geram uma transação `FAILED`, sem movimentação financeira. O `external_id` é salvo como `external_reference` e possui restrição `UNIQUE`, garantindo idempotência contra reprocessamentos.

## Evento consumido

Tópico padrão: `payment_processed`.

```json
{
  "payment_order_id": "781e536d-a140-46f8-a469-d8d9459d1b90",
  "status": "SUCCESS",
  "seller_account": "592f47db-027d-4d56-82e3-343ecf9e8bca",
  "buyer_account": "a7f036ab-247f-4c10-984d-45f4e589142e",
  "amount": 10.00,
  "description": "payment sent with success",
  "transaction_type": "PAYMENT",
  "currency": "BRL",
  "external_id": "381e536d-a140-46f8-a469-d8d9459d1b90"
}
```

Para `PAYMENT`, `FEE` e `ADJUSTMENT`, o comprador é debitado e o vendedor creditado. Para `REFUND` e `REVERSAL`, o sentido é invertido.

## Persistência

A migration `V1__create_ledger_tables.sql` cria:

- `ledger_transaction`: cabeçalho, referência externa, tipo, status e datas;
- `ledger_entry`: lançamentos imutáveis de débito e crédito relacionados à transação;
- índices para consultas por conta, transação e ordem de pagamento.

Configuração local padrão:

```text
URL:     jdbc:postgresql://localhost:5433/ledger_service
Usuário: ledger
Senha:   ledger
```

## Configurações

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`
- `LEDGER_CONSUMER_GROUP` — padrão `ledger-service`
- `PAYMENT_PROCESSED_TOPIC` — padrão `payment_processed`

## APIs de consulta

- `GET /api/v1/ledger/entries/{entryId}`
- `GET /api/v1/ledger/accounts/{accountId}/balance?currency=BRL`

O saldo é derivado dos lançamentos: créditos são positivos e débitos negativos.

Swagger UI:

- execução local: `http://localhost:8080/swagger-ui.html`
- Docker Compose: `http://localhost:8081/swagger-ui.html`

## Testes e imagem Docker

```bash
mvn clean verify
mvn compile jib:dockerBuild
```

A imagem gerada é `ledger-service:0.0.1-SNAPSHOT`.

Para subir PostgreSQL, LedgerService e os demais componentes:

```bash
docker compose up -d
```
