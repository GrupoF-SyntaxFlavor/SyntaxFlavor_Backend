#!/bin/bash

# Obtener la IP de la interfaz Wi-Fi usando ipconfig
MY_IP=$(ipconfig | grep -A 5 "Wi-Fi" | grep "IPv4" | awk '{print $NF}')

# Define la variable KEYCLOAK_HOST con el puerto 8080
# KEYCLOAK_HOST="http://$MY_IP:8080"
# KEYCLOAK_IP="http://$MY_IP:8082"
KEYCLOAK_HOST="http://172.18.8.179:8080"
KEYCLOAK_IP="172.18.8.179:8082"

# Reemplaza los placeholders en el archivo JSON
sed -i "s|\${KEYCLOAK_HOST}|$KEYCLOAK_HOST|g" src/main/resources/imports/keycloak-realm/realm-export.json
sed -i "s|\${KEYCLOAK_IP}|$KEYCLOAK_IP|g" src/main/resources/imports/keycloak-realm/realm-export.json
