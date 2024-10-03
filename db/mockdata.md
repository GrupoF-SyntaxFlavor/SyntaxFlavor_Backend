# Introducción

Orden en que se deben cargar los datos simulados (mockdata) en la base de datos. Seguir este orden garantiza la integridad y consistencia de los datos durante el proceso de pruebas y desarrollo.

1. **Users**: Cargar primero los datos de usuarios para establecer las relaciones básicas.
2. **Customers**: A continuación, cargar los datos de clientes que los usuarios pueden interactuar.
3. **Orders**: Luego, cargar los datos de pedidos que relacionan usuarios y clientes.
4. **Bills**: Luego, cargar los datos de facturas que están asociados con los pedidos.
5. **MenuItems**: Luego, cargar los datos de facturas que están asociados con los pedidos, utilizando el `script` correspondiente.
6. **OrderItems**: Luego, cargar los datos de items que están asociados con los pedidos y los items de menú.

Asegúrese de seguir este orden para evitar problemas de referencia y asegurar que todas las relaciones entre los datos se mantengan correctamente.

---

# Procedimiento para ejecutar el script de carga de datos del menu y subida de imágenes

Para poder cargar los datos de `menu_items` y subir las imágenes correspondientes al sistema MinIO, realizamos los siguientes pasos:

1. **Dirigete a la carpeta scripts**:  
   Usa la teminal para entrar en la carpeta sripts utilizando el siguiente comando:

   ```bash
    cd .\db\scripts

   ```

2. **Instalación de dependencias**:  
   Se instalaron las siguientes bibliotecas necesarias para la ejecución del script en Node.js:

   ```bash
   npm install pg axios form-data
   ```

3. **Ajustes en el script:**:
   - Conexión a la base de datos: En el archivo load_menu_items.js, se configuró la conexión a la base de datos utilizando pg para ejecutar los inserts del archivo SQL.
   - Subida de imágenes: Utilizando axios y form-data, se programó la carga de imágenes en el endpoint especificado.
4. **Ejecución del script**:

   - Ejecutamos el script para cargar los datos y subir las imágenes

   ```bash
   node load_menu_items.js
   Esto cargó los datos desde el archivo SQL y luego subió las imágenes desde la carpeta `img_menu_items/` a MinIO, asociándolas a cada ítem del menú.



   ```
