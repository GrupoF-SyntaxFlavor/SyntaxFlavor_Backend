# Comandos útiles

## Ingreso a la base de datos por consola

Para ingresar a la Base de Datos:

1. Ingrese a la consola del contenedor:

```
docker exec -it syntaxflavor_db bash
```
2. Ingrese a la Base de datos dentro del contenedor:

```
psql -U postgres --db syntaxflavor
```
3. Para verificar las tablas existentes dentro de la base de datos:

```
\dt
```
3. Para verificar atributos de una tabla existente dentro de la base de datos:

```
\d users
```

## Ingreso a la base de datos por consola


docker run -d --name syntaxflavor_keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -e KC_PROXY=edge --restart always quay.io/keycloak/keycloak:latest start-dev
