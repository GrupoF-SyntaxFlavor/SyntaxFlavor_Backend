const fs = require("fs");
const { Client } = require("pg"); // Para conectarse a PostgreSQL
const axios = require("axios"); // Para hacer solicitudes HTTP
const FormData = require("form-data"); // Para manejar multipart/form-data

// Configuración de la conexión a la base de datos PostgreSQL
const DB_HOST = process.env.DB_HOST || "146.190.141.101"; // Cambia syntaxflavor_db por localhost
const DB_PORT = process.env.DB_PORT || "5432";
const DB_NAME = process.env.DB_NAME || "syntaxflavor";
const DB_USER = process.env.DB_USER || "postgres";
const DB_PASSWORD = process.env.DB_PASSWORD || "123456";

// Configuración del MinIO y el API
const BASE_URL = "http://146.190.141.101:8080/api/v1/public/menu/item";

const IMG_FOLDER = "../img_menu_items";

// Función para ejecutar los inserts desde el archivo SQL
async function executeSqlFile(filename) {
  const client = new Client({
    user: DB_USER,
    host: DB_HOST,
    database: DB_NAME,
    password: DB_PASSWORD,
    port: DB_PORT,
  });

  try {
    await client.connect(); // Conectar a la base de datos
    const sqlScript = fs.readFileSync(filename, "utf8"); // Leer el archivo SQL
    await client.query(sqlScript); // Ejecutar el script SQL
    console.log("Inserts ejecutados correctamente.");
  } catch (err) {
    console.error(`Error al ejecutar el archivo SQL: ${err}`);
  } finally {
    await client.end(); // Cerrar la conexión a la base de datos
  }
}

// Función para subir las imágenes a MinIO usando el endpoint PATCH
async function uploadImage(itemId, imageFile) {
  const url = `${BASE_URL}/${itemId}/image`;

  try {
    const form = new FormData();
    form.append("file", fs.createReadStream(imageFile));

    const headers = form.getHeaders();

    const response = await axios.patch(url, form, { headers });
    if (response.status === 200) {
      console.log(
        `Imagen ${imageFile} subida exitosamente para el item ${itemId}`
      );
    } else {
      console.error(
        `Error subiendo la imagen ${imageFile} para el item ${itemId}: ${response.status}`
      );
    }
  } catch (err) {
    console.error(`Error al subir la imagen ${imageFile}: ${err}`);
  }
}

// Función principal para cargar las imágenes
async function loadImages() {
  for (let i = 1; i <= 50; i++) {
    const imageFile = `${IMG_FOLDER}/photo${i}.png`;
    await uploadImage(i, imageFile);
  }
}

(async function () {
  // 1. Ejecutar los inserts del archivo SQL
  await executeSqlFile("../sql/menu_items.sql");

  // 2. Subir las imágenes
  await loadImages();
})();
