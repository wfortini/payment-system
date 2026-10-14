# Payment System

Sistema de pagamentos formado por serviços Spring Boot 4, Java 21, Kafka, PostgreSQL e MongoDB.

## Projetos

| Projeto | Responsabilidade | Dependências |
| --- | --- | --- |
| `PaymentService` | Receber e persistir eventos e ordens de pagamento e publicar no Kafka | PostgreSQL e Kafka |
| `PaymentExecutor` | Consumir `payment-events` e persistir a execução | Kafka e MongoDB |
| `LedgerService` | Operações de ledger | — |
| `kafka-server` | Cluster Kafka local com três brokers KRaft | Docker |

## Imagens Docker

As imagens das aplicações são geradas pelo Maven com o Jib:

```bash
cd PaymentService
mvn clean verify compile jib:dockerBuild

cd ../PaymentExecutor
mvn clean verify compile jib:dockerBuild

cd ../LedgerService
mvn clean verify compile jib:dockerBuild
```

Imagens geradas:

- `payment-service:0.0.1-SNAPSHOT`
- `payment-executor:0.0.1-SNAPSHOT`
- `ledger-service:0.0.1-SNAPSHOT`

## Rede Docker compartilhada do Kafka

Os brokers Kafka e as aplicações estão definidos em arquivos Docker Compose diferentes:

- Brokers: `kafka-server/docker-compose.yml`
- Aplicações e bancos: `docker-compose.yml`

Cada projeto Docker Compose cria sua própria rede por padrão. Por isso, um container iniciado pelo Compose principal não consegue resolver nomes como `kafka-1` em uma rede criada pelo Compose de `kafka-server`.

Além disso, `localhost` dentro de um container aponta para o próprio container. Portanto, as aplicações não devem usar `localhost:49092` para acessar o Kafka quando estiverem executando no Docker.

A integração utiliza a rede externa compartilhada:

```text
payment-kafka-network
```

Crie essa rede uma única vez:

```bash
docker network inspect payment-kafka-network \
  || docker network create payment-kafka-network
```

Os brokers anunciam seus listeners internos na rede compartilhada:

```text
kafka-1:19092
kafka-2:19092
kafka-3:19092
```

O `PaymentService` e o `PaymentExecutor` recebem a seguinte configuração pelo Compose principal:

```yaml
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka-1:19092,kafka-2:19092,kafka-3:19092
```

### Endereços por ambiente

Use endereços diferentes conforme o local onde a aplicação está executando:

| Ambiente | Bootstrap servers |
| --- | --- |
| Aplicação dentro do Docker | `kafka-1:19092,kafka-2:19092,kafka-3:19092` |
| Aplicação executada diretamente no host | `localhost:29092,localhost:39092,localhost:49092` |

## Subir o ambiente

Primeiro, garanta que a rede compartilhada existe:

```bash
docker network inspect payment-kafka-network \
  || docker network create payment-kafka-network
```

Depois inicie os brokers:

```bash
cd /home/wellington/Java/kafka/kafka-server
docker compose up -d
```

Por fim, inicie aplicações e bancos:

```bash
cd /home/wellington/Java/kafka
docker compose up -d
```

## Serviços locais

| Serviço | Endereço |
| --- | --- |
| PaymentService | `http://localhost:8080` |
| PaymentService Swagger | `http://localhost:8080/swagger-ui.html` |
| LedgerService | `http://localhost:8081` |
| PostgreSQL | `localhost:5432` |
| MongoDB | `localhost:27017` |
| Kafka broker 1 | `localhost:29092` |
| Kafka broker 2 | `localhost:39092` |
| Kafka broker 3 | `localhost:49092` |

## Verificar a conectividade

Confirme que as aplicações resolvem os brokers pela rede compartilhada:

```bash
docker exec payment-service getent hosts kafka-1 kafka-2 kafka-3
docker exec payment-executor getent hosts kafka-1 kafka-2 kafka-3
```

Confira os containers:

```bash
docker compose ps

cd kafka-server
docker compose ps
```

Confira a conexão do consumer:

```bash
docker logs payment-executor
```

O log deve informar que o consumer entrou no grupo `payment-executor` e recebeu uma partição do tópico `payment-events`.

## Recriar containers após uma mudança

Após gerar uma nova imagem pelo Maven, recrie somente o serviço alterado:

```bash
docker compose up -d --force-recreate payment-service
docker compose up -d --force-recreate payment-executor
docker compose up -d --force-recreate ledger-service
```

Não é necessário recriar a rede `payment-kafka-network` a cada execução.
