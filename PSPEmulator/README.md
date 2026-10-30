# PSPEmulator

Emulador de PSP desenvolvido com Spring Boot 4, Java 21, Maven e arquitetura MVC.

## Criar um pagamento

```http
POST /api/v1/payments
Content-Type: application/json
```

```json
{
  "payment_order_id": "181e536d-a140-46f8-a469-d8d9459d1b90",
  "buyer_account": "account-456",
  "amount": "125.90",
  "currency": "BRL"
}
```

Resposta `201 Created`:

```json
{
  "external_id": "181e536d-a140-46f8-a469-d8d9459d1b90"
}
```

O pagamento é persistido antes da publicação do evento. Após o commit, o evento é enviado de forma assíncrona para `POST /v1/notifications/transfer` na URL configurada por `PSP_NOTIFICATION_BASE_URL`.

## Build e imagem Docker

```bash
mvn clean verify
mvn compile jib:dockerBuild
```

Imagem gerada: `psp-emulator:0.0.1-SNAPSHOT`.
