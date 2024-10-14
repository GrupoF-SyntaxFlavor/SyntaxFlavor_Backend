# SyntaxFlavor_Backend

---
## Log de cambios

### 2024-10-03
- **Autenticación por token:** Se agregó acceso mediante `JWT` a las APIs de creación de factura, obtención de ítems de menú y creación de orden.
- **Confirmación de correos con Keycloak:** Implementada la integración de Keycloak para el envío de correos de confirmación a usuarios no verificados. Añadida la carpeta `/theme` en `/resources` para personalizar el contenido del correo en español.
- **Estructura de códigos de respuesta:** Se definió el catálogo de códigos de respuesta para la API, que sigue un formato estándar basado en el dominio y la operación.
- **Estándar de salida de API:** Definido un formato común para las respuestas de la API, que incluye un código de respuesta, un `payload` y un mensaje de error opcional.
---
# Introducción

Para este proyecto estamos usando una arquitectura monolítica por capas vertical separada por dominios.

Es decir, cada dominio se encuentra dentro de su propio paquete y cuenta con al menos 5 subpaquetes:
- **api:** Interfaz externa que se expone para interacción con el backend.
- **bl:** Lógica de negocio, su principal función es convertir objetos entity en dto.
- **repository:** Patrón de acceso a DB que estandariza los procesos de escritura, lectura y modificación.
- **entity:** Representación de las entidades del modelo ER de DB, estas clases programan de manera dinámica el esquema en Postgres.
- **dto:** Son objetos de entrada y salida, el tipo de objeto que se envía y recibe dentro de la capa API. Es el trabajo de BL añadir, calcular u ocultar información según corresponda para escribir a DB o mostrar al cliente.

Opcionalmente podrían existir paquetes adicionales:
- **service:** Cuando se requiera un servicio externo (MinIO, Keycloak, etc.), estas clases se ocuparán de la conexión y comunicación con los módulos correspondientes.
- **config:** Cuando un servicio o funcionalidad requiera información que puede estar sujeta a cambios (entornos de dev vs. prod), esta configuración se declarará en estos paquetes.

## Estándar de salida

Todas las salidas de API deben seguir el siguiente formato:

```json
{
    "responseCode": "DOM-001",  // código del resultado REF: Catálogo de códigos
    "payload": {},              // DTO del resultado del endpoint
    "errorMessage": ""          // Mensaje de error (si hay o null en caso contrario)
}
```

## Catálogo de códigos

El catálogo de códigos sigue un estándar en dos partes:

`DOM`: Las primeras 3 letras definen el dominio del cuál proviene la respuesta, por lo general es una abreviatura como `USR` para usuario. Los códigos de dominio definidos hasta el momento son los siguientes:

- `USR`: user : _usuario_

`001`: El número serializa las salidas del backend para identificar de mejor manera la operación que se ha realizado y si ha sucedido una excepción.

Los números de código deben seguir la siguiente estructura:

| Valor | Operación relacionada | Significado                                  |
|-------|-----------------------|----------------------------------------------|
| 000   | `GET`                 | Los datos se han recuperado correctamente    |
| 001   | `POST`                | Los datos se han escrito correctamente       |
| 002   | `PUT`, `PATCH`        | Los datos se han actualizado correctamente   |
| 003   | `DELETE`              | Los datos se han eliminado correctamente     |
| 00X   | `ANY`                 | La operación X se ha realizado correctamente |
| 6XX   | `ANY`                 | Ha ocurrido un error en la operación XX      |

**Nota:** El número de error corresponde a su relación con la operación.
- Ejemplo:
  Error al escribir en DB: **601**

## Autenticación y API por token

Se agregó acceso por token a las siguientes APIs. Para probarlas, asegúrate de copiar el `access_token` y reemplazar `<JWT_TOKEN>` en los ejemplos.

### Crear el registro de una "factura"
POST http://localhost:8080/api/v1/bill  
Authorization: Bearer `<JWT_TOKEN>`  
Content-Type: application/json  
Accept: application/json

```json
{
  "orderId": 1,
  "userId": 1,
  "billName": "Montero",
  "nit": "12334135"
}
```

### Obtener ítems de menú
GET http://localhost:8080/api/v1/menu/item  
Authorization: Bearer `<JWT_TOKEN>`  
Accept: application/json

### Crear una ORDEN
POST http://localhost:8080/api/v1/order  
Authorization: Bearer `<JWT_TOKEN>`  
Content-Type: application/json  
Accept: application/json

```json
{
  "customerId": 1,
  "itemIdQuantityMap": {
    "1": 2,
    "2": 1
  }
}
```
### Pasos a seguir para levantar el backend (Provisional)

#### Cambia a tu ip:

1. Json de creación de keycloak:
- En Windows abre una terminal Powershell(importante)
- Ejecuta el comando:
  ```bash
    .\update-keycloak-urls.ps1

   ```
Este comando permite cambiar las urls del Json para la creación de keycloak a tu ip actual.

- Para macOS/Linux (Bash): Ejecuta el script Bash para realizar el reemplazo en macOS:

```bash
    .\update-keycloak-urls.sh

   ```
   NOTA: esta solución es temporal, ya que sólo reemplaza variables en el json por la ip utilizada.
#### Crear el contenedor docker:
- Haz click derecho en el archivo "docker-compose.yaml"
- Selecciona la opción "Compose Up - Select Services"
- Marca todas las opciones **menos syntaxflavor_backend**
- Espera a que se creen todos los contenedores

#### Correr el backend:
- Introduce el comando en la consola para levantar el backend:
  ```bash
    mvn clean install exec:java

   ```

#### Haz correr los scripts js:
1. **Dirigete a la carpeta scripts**:  
   ```bash
    cd .\db\scripts

   ```
2. **Instalación de dependencias**:  
   ```bash
   npm install pg axios form-data
   ```
  NOTA: tambien puede utilizarse el comando "npm i"
3. **Ejecución del script `load_users_keycloak.js`**:

   ```bash
   node load_users_keycloak.js
   ```
4. **Ejecución del script `load_menu_items.js`**:

   ```bash
   node load_menu_items.js
   ```

5. **Ejecución del script `load_mockdata.js`**:
   ```bash
   node load_mockdata.js
   ```
#### Notas importantes:
- Recuerda que cuando creas un usuario debes verificar el correo mediante MailHog antes de hacer login.
