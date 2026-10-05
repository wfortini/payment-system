# LedgerService

API de lançamentos contábeis construída com Java 21, Spring Boot 4 e Maven, organizada com Clean Architecture.

## Arquitetura

```text
domain                     Modelos e regras contábeis sem dependência de framework
application/port/in        Casos de uso disponibilizados pela aplicação
application/port/out       Contratos exigidos pela aplicação
application/service        Implementação dos casos de uso
infrastructure/adapter/in  Adaptador HTTP/REST
infrastructure/adapter/out Persistência em memória
infrastructure/config      Composição das dependências com Spring
```

## Executar

Pré-requisitos: Java 21 e Maven 3.6.3 ou superior.

```bash
mvn spring-boot:run
```

Criar um crédito:

```bash
curl -i -X POST http://localhost:8080/api/v1/ledger/entries \
  -H 'Content-Type: application/json' \
  -d '{
    "accountId":"d9d1ac44-c1dd-4eb9-a784-cbced8d30190",
    "type":"CREDIT",
    "amount":100.00,
    "currency":"BRL",
    "description":"Initial deposit"
  }'
```

Consultar um lançamento:

```bash
curl http://localhost:8080/api/v1/ledger/entries/{entryId}
```

Consultar o saldo:

```bash
curl 'http://localhost:8080/api/v1/ledger/accounts/d9d1ac44-c1dd-4eb9-a784-cbced8d30190/balance?currency=BRL'
```

## Testes e build

```bash
mvn clean verify
```

## Imagem Docker pelo Maven

O Jib Maven Plugin gera a imagem diretamente no Docker local, sem `Dockerfile`:

```bash
mvn compile jib:dockerBuild
```

A imagem gerada será `ledger-service:0.0.1-SNAPSHOT`:

```bash
docker run --rm -p 8080:8080 ledger-service:0.0.1-SNAPSHOT
```

## Swagger UI e OpenAPI

Ao executar o projeto diretamente, a documentação fica disponível em:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificação OpenAPI (JSON): `http://localhost:8080/v3/api-docs`

Ao subir a stack pelo Docker Compose, use a porta externa `8081`:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- Especificação OpenAPI (JSON): `http://localhost:8081/v3/api-docs`
