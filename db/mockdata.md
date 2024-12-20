## **Consideraciones:**

1. Se recomienda recontruir el contenedor antes de ejecutar los scripts, de otra forma podrían ocurrir conflictos a causa de los ids.
2. El sistema debe estar corriendo a la hora de ejecutar `load_menu_items.js` de otra forma no sé podrá realizar el cargado de imágenes ya que se hace mediante un **endpoint**.
3. En el caso de fallar el cargado de imágenes, no vuelva a correr el archivo `load_menu_items.js` por que se crearan registros repetidos. Se recomienda borrar la base de datos antes de correrlo nuevamente.

---

## Procedimiento para ejecutar los scripts de carga de datos

Para poder cargar los datos de `menu_items` y subir las imágenes correspondientes al sistema MinIO, realizamos los siguientes pasos:

1. **Dirigete a la carpeta scripts**:  
   Usa la terminal para entrar en la carpeta sripts utilizando el siguiente comando:

   ```bash
    cd .\db\scripts

   ```

2. **Instalación de dependencias**:  
   Se instalaron las siguientes bibliotecas necesarias para la ejecución del script en Node.js:

   ```bash
   npm install pg axios form-data
   ```

3. **Ejecución del script `load_users_keycloak.js`**:

   Ejecutamos el script para cargar los datos y subir las imágenes

   ```bash
   node load_users_keycloak.js
   ```

   Este carga los datos desde el archivo TXT de users y customers para cargarlos en keycloak y luego en la DB.

4. **Ejecución del script `load_menu_items.js`**:

   Ejecutamos el script para cargar los datos y subir las imágenes

   ```bash
   node load_menu_items.js
   ```

   Este carga los datos desde el archivo SQL y luego sube las imágenes desde la carpeta `img_menu_items/` a MinIO, asociándolas a cada ítem del menú.



5. **Ejecución del script `load_mockdata.js`**:

   - Ejecutamos el script para cargar los datos y subir las imágenes

   ```bash
   node load_mockdata.js
   ```

   Este carga los datos desde el archivo SQL y luego sube las imágenes desde la carpeta `img_menu_items/` a MinIO, asociándolas a cada ítem del menú.

6. **Ejecución del script `load_kitchen_users.js`**:

   - Ejecutamos el script para cargar los usuarios de cocina

   ```bash
   node load_kitchen_users.js
   ```

   Este carga los datos desde el archivo SQL y crea los usuarios en keycloak.
