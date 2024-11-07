const fs = require("fs");
const { Client } = require("pg"); // Para conectarse a PostgreSQL

// Configuración de la conexión a la base de datos PostgreSQL
const DB_HOST = process.env.DB_HOST || "146.190.141.101"; // Cambia syntaxflavor_db por localhost
const DB_PORT = process.env.DB_PORT || "5432";
const DB_NAME = process.env.DB_NAME || "syntaxflavor";
const DB_USER = process.env.DB_USER || "postgres";
const DB_PASSWORD = process.env.DB_PASSWORD || "123456";

// Función para ejecutar los inserts desde el archivo SQL
async function executeSqlFile(filename) {
  const client = new Client({
    user: DB_USER,
    host: DB_HOST,
    database: DB_NAME,
    password: DB_PASSWORD,
    port: DB_PORT,
  });

  console.log(`Iniciando la ejecución del archivo: ${filename}`);

  try {
    await client.connect(); // Conectar a la base de datos
    console.log(`Conexión a la base de datos establecida para ${filename}`);

    const sqlScript = fs.readFileSync(filename, "utf8"); // Leer el archivo SQL
    await client.query(sqlScript); // Ejecutar el script SQL

    console.log(`Inserts ejecutados correctamente desde ${filename}.`);
  } catch (err) {
    console.error(
      `Error al ejecutar el archivo SQL ${filename}: ${err.message}`
    );
    throw new Error(`Proceso detenido debido a error en ${filename}`);
  } finally {
    try {
      await client.end(); // Cerrar la conexión a la base de datos
      console.log(`Conexión cerrada correctamente para ${filename}`);
    } catch (endErr) {
      console.error(
        `Error al cerrar la conexión para ${filename}: ${endErr.message}`
      );
    }
  }
}

// Función principal para ejecutar los archivos SQL en orden
async function loadSqlFilesInOrder() {
  const sqlFiles = [
    // "../sql/users.sql",
    // "../sql/customers.sql",
    "../sql/orders.sql",
    "../sql/bills.sql",
    "../sql/order_items.sql",
  ];

  for (const file of sqlFiles) {
    try {
      console.log(`Procesando el archivo: ${file}`);
      await executeSqlFile(file);
    } catch (err) {
      console.error(err.message);
      break; // Detener la ejecución si ocurre un error en algún archivo
    }
  }
}

(async function () {
  
  //llamar a endpoint de usuarios
  // Ejecutar los archivos SQL en orden
  await loadSqlFilesInOrder();
})();
