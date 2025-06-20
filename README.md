# Keycloak Demo - Spring Boot Resource Server

Este projeto demonstra a integração entre uma aplicação Spring Boot (Resource Server) e o Keycloak, usando autenticação JWT e autorização baseada em roles.

## Arquitetura
- **Keycloak**: Gerencia usuários, clientes e roles. Emite tokens JWT.
- **Spring Boot**: API protegida, valida tokens JWT emitidos pelo Keycloak.
- **Docker Compose**: Orquestra os containers para ambiente local.
- **Script configure-keycloak.sh**: Após a importação do realm, atribui automaticamente as roles `USER` e `ADMIN` ao service account do client via API REST do Keycloak.

## Como rodar

### 1. Build da aplicação
```sh
mvn clean package
```

### 2. Subir o ambiente
```sh
docker compose up --build -d
```

- Keycloak estará em: [http://keycloak:8080/](http://keycloak:8080/)
- API estará em: [http://localhost:8082/](http://localhost:8082/)

> **Nota:** O script `configure-keycloak.sh` será executado automaticamente (via Docker ou manualmente, conforme seu setup) para garantir que o service account do client tenha as roles corretas.

### 3. Gerar um token JWT

Faça uma requisição para o endpoint de token do Keycloak:

```sh
curl -X POST \
  http://keycloak:8080/realms/littlebee/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=littlebee-client" \
  -d "client_secret=SEU_CLIENT_SECRET" \
  -d "grant_type=client_credentials"
```

O campo `access_token` da resposta é o JWT.

### 4. Testar os endpoints protegidos

#### Endpoint público
```sh
curl http://localhost:8082/public
```

#### Endpoint protegido (USER ou ADMIN)
```sh
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" http://localhost:8082/user
```

#### Endpoint protegido (ADMIN)
```sh
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" http://localhost:8082/admin
```

#### Health check
```sh
curl http://localhost:8082/health
```

## Roles e autorização
- O token JWT precisa conter as roles `USER` e/ou `ADMIN` em `realm_access.roles`.
- O script `configure-keycloak.sh` garante que o service account do client tenha essas roles atribuídas automaticamente via API.

## Dicas de troubleshooting
- Se receber 401, confira se o token está correto, não expirado e com as roles certas.
- Se receber 403, o token foi aceito, mas não tem a role necessária.
- Se a UI do Keycloak travar em "Loading...", acesse via [http://keycloak:8080/](http://keycloak:8080/) e adicione `keycloak` ao seu `/etc/hosts` se necessário.
- O issuer do token (`iss`) deve ser igual ao `issuer-uri` do `application.yml`.

## Limpeza e manutenção
- Não use o `keycloak-spring-boot-starter` em resource servers.
- Toda a configuração de segurança está centralizada em `SecurityConfig.java` e `JwtAuthConverter.java`.
- O script `configure-keycloak.sh` pode ser adaptado para outras customizações pós-importação.

---

Dúvidas ou sugestões? Abra uma issue ou entre em contato!