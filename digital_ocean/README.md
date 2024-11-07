# Clonación Parcial de Repositorio para DigitalOcean

Este tutorial muestra cómo clonar solo la carpeta `digital_ocean` de un repositorio utilizando un **checkout parcial** (sparse checkout). Esto es útil para reducir el tamaño de la descarga y enfocarse únicamente en los archivos necesarios para la implementación en un entorno como un droplet de DigitalOcean.

## Comandos

Sigue estos pasos para realizar una clonación parcial:

1. **Clonar el Repositorio Sin Realizar Checkout Completo**  
   Este comando clona el repositorio sin realizar un checkout de los archivos y solo descarga el historial del último commit. Esto ayuda a reducir el tamaño de la clonación inicial.
   ```bash
   git clone --no-checkout --depth=1 <url>
   ```
    - Reemplaza `<url>` con la URL de tu repositorio en GitHub u otro servicio de Git.

2. **Entrar en el Directorio del Repositorio**  
   Cambia al directorio del repositorio recién clonado:
   ```bash
   cd <repo-name>
   ```
    - Reemplaza `<repo-name>` con el nombre de la carpeta creada por `git clone`.

3. **Inicializar el Modo de Sparse Checkout**  
   Esto configura Git para hacer un checkout selectivo de archivos o carpetas específicas.
   ```bash
   git sparse-checkout init --cone
   ```

4. **Definir la Carpeta a Descargar**  
   Indica a Git que solo descargue el contenido de la carpeta `digital_ocean`.
   ```bash
   git sparse-checkout set digital_ocean
   ```

5. **Realizar el Checkout**  
   Ejecuta `git checkout` para descargar únicamente los archivos de la carpeta `digital_ocean`.
   ```bash
   git checkout
   ```

## Explicación de los Comandos

- `--no-checkout`: Clona el repositorio sin realizar el checkout de los archivos.
- `--depth=1`: Realiza una clonación superficial (shallow clone) solo con el último commit, reduciendo el tamaño del historial.
- `git sparse-checkout init --cone`: Activa el sparse checkout, limitando el checkout a carpetas específicas.
- `git sparse-checkout set digital_ocean`: Define `digital_ocean` como la única carpeta que se descargará.
- `git checkout`: Ejecuta el checkout de los archivos en la carpeta especificada.

## Ejemplo Completo

```bash
git clone --no-checkout --depth=1 https://github.com/GrupoF-SyntaxFlavor/SyntaxFlavor_Backend.git
cd SyntaxFlavor_Backend
git sparse-checkout init --cone
git sparse-checkout set digital_ocean
git checkout
```

Este procedimiento descargará solo la carpeta `digital_ocean` del repositorio, optimizando el uso de ancho de banda y almacenamiento en tu droplet de DigitalOcean.