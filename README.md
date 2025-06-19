# littlebee-keycloak

Este projeto é uma demo simples usando Spring Boot com integração ao Keycloak.

## Pré-requisitos

- Java 17 ou superior
- Maven 3.8+
- Docker (para rodar o Keycloak)

## Como rodar a aplicação

Execute o comando abaixo na pasta do projeto para iniciar a aplicação Spring Boot:

```bash
mvn spring-boot:run
```

O servidor será iniciado em: `http://localhost:8080`

## Como rodar o Keycloak com Docker

O projeto já inclui um arquivo `docker-compose.yml` e um `realm-export.json` para subir o Keycloak pré-configurado.

### Subindo o Keycloak

Na pasta `keycloak-demo`, execute:

```bash
docker compose up
```

O Keycloak estará disponível em: `http://localhost:8080`

- Usuário admin: `admin`
- Senha admin: `admin`

### Realm, Roles e Client já configurados

Ao subir o Keycloak, será importado automaticamente:
- Realm: `littlebee`
- Roles: `ADMIN` e `USER`
- Client: `littlebee-client` (confidential, com client secret)

#### Exemplo de client configurado
- **client_id:** `littlebee-client`
- **client_secret:** `UsmLa1LrNOoF4YDW6QS2301H9IEWq0jP`
- **grant_type:** `client_credentials`

## Como obter um token de acesso

Faça uma requisição POST para:

```
POST http://localhost:8080/realms/littlebee/protocol/openid-connect/token
```

Com o corpo (x-www-form-urlencoded):

```
grant_type=client_credentials
client_id=littlebee-client
client_secret=UsmLa1LrNOoF4YDW6QS2301H9IEWq0jP
```

## Como rodar os testes

Execute:

```bash
mvn test
```

## Comandos Maven úteis

- **Build do projeto:**
  ```bash
  mvn clean package
  ```
- **Rodar apenas os testes:**
  ```bash
  mvn test
  ```
- **Rodar a aplicação:**
  ```bash
  mvn spring-boot:run
  ```

## Integração Contínua

O projeto possui workflow no GitHub Actions que faz build e executa os testes a cada push. (Em desenvolvimento)