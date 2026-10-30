# Payment System

Sistema de pagamentos formado por serviços Spring Boot 4, Java 21, Kafka, PostgreSQL e MongoDB, com observabilidade baseada em Grafana, Loki, Tempo e Prometheus.

## Projetos

| Projeto | Responsabilidade | Dependências |
| --- | --- | --- |
| `PaymentService` | Receber e persistir eventos e ordens de pagamento e publicar no Kafka | PostgreSQL e Kafka |
| `PaymentExecutor` | Consumir `payment-events` e persistir a execução | Kafka e MongoDB |
| `LedgerService` | Operações de ledger | — |
| `kafka-server` | Cluster Kafka local com três brokers KRaft | Docker |

## Observabilidade

O ambiente local inclui uma stack de observabilidade para centralizar logs, métricas e traces do `PaymentService`.

```text
PaymentService -- logs ----------------------> Loki -------+
               -- métricas /actuator/prometheus --> Prometheus --> Grafana
               -- traces (Zipkin) ------------> Tempo ------+
```

O Grafana é o ponto central de consulta. Os datasources do Loki, Prometheus e Tempo são provisionados automaticamente pelo arquivo `docker/grafana/datasources.yml`.

### Loki — logs

O Loki recebe e armazena os logs do `PaymentService`. O envio é configurado em:

```text
PaymentService/src/main/resources/logback-spring.xml
```

O `Loki4jAppender` envia os logs para o endpoint interno:

```text
http://loki:3100/loki/api/v1/push
```

Cada registro recebe labels para facilitar as consultas no Grafana:

- `application`: nome definido em `spring.application.name`;
- `host`: nome do container que gerou o log;
- `level`: nível do log, como `INFO`, `WARN` ou `ERROR`.

Exemplo de consulta LogQL no Grafana:

```logql
{application="PaymentService"}
```

### Prometheus — métricas

O `PaymentService` disponibiliza métricas por meio do Spring Boot Actuator e do Micrometer. O endpoint utilizado pelo Prometheus é:

```text
http://payment-service:8080/actuator/prometheus
```

Os endpoints de gerenciamento habilitados estão definidos em `PaymentService/src/main/resources/application.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.distribution.percentiles-histogram.http.server.requests=true
```

O Prometheus coleta as métricas a cada 2 segundos, conforme `docker/prometheus/prometheus.yml`. No host, o endpoint também pode ser consultado em:

```text
http://localhost:8080/actuator/prometheus
```

### Tempo — traces distribuídos

O Tempo armazena os traces gerados pelo Micrometer Tracing no `PaymentService`. A aplicação usa a bridge Brave e o Zipkin Reporter, enquanto o Tempo mantém o receiver Zipkin disponível na porta `9411`.

A amostragem está configurada em 100% para o ambiente local:

```properties
management.tracing.sampling.probability=1.0
```

O arquivo `ObservationConfig.java` registra o `ObservedAspect`, permitindo instrumentar métodos com `@Observed`. O armazenamento local do Tempo é configurado em `docker/tempo/tempo.yml` e persistido no diretório `data/tempo`.

### Grafana — visualização

O Grafana reúne as três fontes de dados:

| Datasource | Endereço usado dentro da rede Docker | Finalidade |
| --- | --- | --- |
| Prometheus | `http://prometheus:9090` | Métricas e exemplars |
| Loki | `http://loki:3100` | Logs centralizados |
| Tempo | `http://tempo:3200` | Traces distribuídos |

O acesso anônimo com perfil de administrador está habilitado somente para facilitar o uso no ambiente local. Não utilize essa configuração em produção.

Para abrir o Grafana:

```text
http://localhost:3000
```

No menu **Explore**, selecione Loki para consultar logs, Prometheus para consultar métricas ou Tempo para pesquisar traces. A configuração também relaciona logs do Loki aos traces do Tempo por meio do `TraceID`.

### Iniciar e verificar a stack

Os componentes de observabilidade sobem junto com os demais serviços:

```bash
docker compose up -d loki prometheus tempo grafana
docker compose ps loki prometheus tempo grafana
```

Para acompanhar os logs de inicialização:

```bash
docker compose logs -f grafana loki prometheus tempo
```

Endpoints úteis para verificar a saúde dos componentes:

```bash
curl http://localhost:3000/api/health
curl http://localhost:3100/ready
curl http://localhost:3110/ready
curl http://localhost:9090/-/ready
```

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
| Grafana | `http://localhost:3000` |
| Loki | `http://localhost:3100` |
| Tempo | `http://localhost:3110` |
| Tempo Zipkin receiver | `http://localhost:9411` |
| Prometheus | `http://localhost:9090` |

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
