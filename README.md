# SyntaxFlavor_Backend

Para este proyecto estamos usando una arquitectura monolítica por capas vertical separada por dominios.

Es decir, cada dominio se encuentra dentro de su propio paquete y cuenta con al menos 5 subpaquetes:
- **api:** Interfaz externa que se expone para interacción con el backend
- **bl:** Lógica de negocio, su principal función es convertir objetos entity en dto
- **repository:** Patrón de acceso a DB que estandariza los procesos de escritura, lectura y modificación
- **entity:** Representación de las entidades del modelo ER de DB, estas clases programan de manera dinámica el esquema en Postgres
- **dto:** Son objetos de entrada y salida, son el tipo de objeto que se envía y recibe dentro de la capa API. Es el trabajo de BL añadir, calcular u ocultar información según correponda para escribir a DB o mostrar al cliente.

Opcionalmente podrían existir paquetes adicionales:
- **service:** Cuando se requiera un servicio externo (MinIO, Keycloak, etc.) estas clases se ocuparán de la conexión y comunicación con los módulos correspondientes.
- **config:** Cuando un servicio o funcionalidad requiera información que puede estar sujeta a cambios (entornos de dev vs. prod) esta configuración se declarará en estos paquetes

## Estándar de salida

Todas las salidas de API deben seguir el siguiente formato:

```json
{
    "code": "DOM-001" //código del resultado REF: Catálogo de códigos
    "payload": {} // DTO del resultado del endpoint
    "error_message": "" // Mensaje de error (si hay o null en caso contrario)
}
```

## Catálogo de códigos

El catálogo de códigos sigue un estándar en dos partes:

`DOM`: Las primeras 3 letras definen el dominio del cuál proviene la respuesta, por lo general es una abreviatura como `USR` para usuario, los códigos de dominio definidos hasta el momento son los siguientes:

- `USR` : user : _usuario_

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