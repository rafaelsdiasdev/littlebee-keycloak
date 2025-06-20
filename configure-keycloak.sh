#!/bin/bash

# Espera o Keycloak iniciar completamente, checando a porta TCP
echo "Waiting for Keycloak to start..."
while ! nc -z keycloak 8080; do
    printf '.'
    sleep 2
done

echo "Keycloak is up!"
sleep 10 # Adiciona uma espera extra para garantir que o Keycloak esteja pronto

# Configurações
KEYCLOAK_URL="http://keycloak:8080"
REALM="littlebee"
CLIENT_ID="littlebee-client"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin"

# 1. Obter token de admin do realm 'master'
echo "Getting admin token..."
ADMIN_TOKEN=$(curl -s -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$ADMIN_USER" \
  -d "password=$ADMIN_PASSWORD" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r .access_token)

if [ -z "$ADMIN_TOKEN" ]; then
    echo "Failed to get admin token"
    exit 1
fi
echo "Admin token obtained."

# 2. Obter o ID do nosso cliente ('littlebee-client')
echo "Getting client ID..."
CLIENT_UUID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/clients?clientId=$CLIENT_ID" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')

if [ -z "$CLIENT_UUID" ]; then
    echo "Client '$CLIENT_ID' not found"
    exit 1
fi
echo "Client ID for '$CLIENT_ID' is $CLIENT_UUID"

# 3. Obter o ID do Service Account User para o nosso cliente
echo "Getting Service Account User ID..."
SERVICE_ACCOUNT_USER_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/clients/$CLIENT_UUID/service-account-user" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.id')

if [ -z "$SERVICE_ACCOUNT_USER_ID" ]; then
    echo "Service Account User for client '$CLIENT_ID' not found"
    exit 1
fi
echo "Service Account User ID is $SERVICE_ACCOUNT_USER_ID"

# 4. Obter as representações das roles 'ADMIN' e 'USER'
echo "Getting role representations..."
ADMIN_ROLE_REP=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/roles/ADMIN" -H "Authorization: Bearer $ADMIN_TOKEN")
USER_ROLE_REP=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/roles/USER" -H "Authorization: Bearer $ADMIN_TOKEN")

# 5. Atribuir as roles ao Service Account User
echo "Assigning roles to Service Account User..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/users/$SERVICE_ACCOUNT_USER_ID/role-mappings/realm" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "[$ADMIN_ROLE_REP, $USER_ROLE_REP]"

echo "Keycloak configuration finished successfully!" 