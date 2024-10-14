#!/bin/bash

# Obtener la IP de la interfaz Wi-Fi
MY_IP=$(ifconfig en0 | grep 'inet ' | awk '{print $2}')

if [ -z "$MY_IP" ]; then
    echo "No se pudo obtener la IP de la interfaz Wi-Fi."
    exit 1
fi

# Define las variables KEYCLOAK_HOST y KEYCLOAK_IP
KEYCLOAK_HOST="http://${MY_IP}:8080"
KEYCLOAK_IP="http://${MY_IP}:8082"

# Actualiza el archivo realm-export.json con las nuevas direcciones IP
sed -i '' "s|\${KEYCLOAK_HOST}|$KEYCLOAK_HOST|g" src/main/resources/imports/keycloak-realm/realm-export.json
sed -i '' "s|\${KEYCLOAK_IP}|$KEYCLOAK_IP|g" src/main/resources/imports/keycloak-realm/realm-export.json

echo "Las URLs en realm-export.json han sido actualizadas con la IP $MY_IP."
