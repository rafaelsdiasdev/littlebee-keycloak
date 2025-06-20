# Keycloak com Spring Boot - Exemplo Completo

Este projeto demonstra a integração de uma aplicação Spring Boot 3 com o Keycloak, utilizando Docker Compose para orquestrar os serviços.

A aplicação expõe três endpoints:
- `/public`: Acesso público, não requer autenticação.
- `/user`: Requer autenticação e a role `USER`.
- `/admin`: Requer autenticação e a role `ADMIN`.

## Arquitetura

O ambiente é configurado utilizando Docker Compose e consiste em três serviços principais:

1.  **`keycloak`**: A instância do Keycloak, que é responsável pela gestão de identidade e acesso. O serviço importa automaticamente o realm `littlebee` a partir do arquivo `realm-export.json`.
2.  **`keycloak-configurator`**: Um serviço utilitário que é executado após o Keycloak iniciar. Ele executa o script `configure-keycloak.sh` para atribuir as roles `ADMIN` e `USER` ao *Service Account* do cliente `littlebee-client`. Isso é necessário porque a importação de realm não atribui roles a service accounts automaticamente.
3.  **`littlebee-app`**: A aplicação Spring Boot que contém os endpoints protegidos.

## Pré-requisitos

- Docker
- Docker Compose

## Como Executar

1.  **Clone o repositório:**
    ```sh
    git clone <url-do-repositorio>
    cd keycloak-demo
    ```

2.  **Construa o projeto Spring Boot:**
    ```sh
    ./mvnw clean package
    ```

3.  **Inicie os serviços com Docker Compose:**
    ```sh
    docker compose up --build
    ```
    Os logs de todos os serviços serão exibidos no terminal. O serviço `keycloak-configurator` será executado e, após a conclusão, sairá com código 0.

## Como Testar a Aplicação

Após os serviços estarem em execução, você pode testar os endpoints.

### 1. Obter um Token de Acesso

Use o `grant_type=client_credentials` para obter um token de acesso para o cliente `littlebee-client`. Este token conterá as roles `ADMIN` e `USER` que foram atribuídas pelo script de configuração.

Execute o comando abaixo no seu terminal:

```bash
export TOKEN=$(curl -s -X POST "http://localhost:8080/realms/littlebee/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=littlebee-client" \
  -d "client_secret=p3aIe2zALF2k3gP1iN5wQ2vX8yZ7jR4t" | jq -r .access_token)

echo "Token: $TOKEN"
```

### 2. Testar os Endpoints

Agora, use o token obtido para fazer chamadas aos endpoints.

- **Endpoint público:**
  ```sh
  curl http://localhost:8082/public
  # Resposta esperada: Hello from public endpoint!
  ```

- **Endpoint de usuário (requer role USER):**
  ```sh
  curl -H "Authorization: Bearer $TOKEN" http://localhost:8082/user
  # Resposta esperada: Hello from user endpoint, <CLIENT_ID>!
  ```

- **Endpoint de admin (requer role ADMIN):**
  ```sh
  curl -H "Authorization: Bearer $TOKEN" http://localhost:8082/admin
  # Resposta esperada: Hello from admin endpoint, <CLIENT_ID>!
  ```

## Parando a Aplicação

Para parar e remover os containers e a rede, execute:

```sh
docker compose down
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