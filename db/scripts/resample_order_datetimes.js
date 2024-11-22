const fs = require("fs");
const { Client } = require("pg"); // Para conectarse a PostgreSQL

// Configuración de la conexión a la base de datos PostgreSQL
const DB_HOST = process.env.DB_HOST || "localhost"; // Cambia syntaxflavor_db por localhost
const DB_PORT = process.env.DB_PORT || "5432";
const DB_NAME = process.env.DB_NAME || "syntaxflavor";
const DB_USER = process.env.DB_USER || "postgres";
const DB_PASSWORD = process.env.DB_PASSWORD || "123456";

const client = new Client({
    host: DB_HOST,
    port: DB_PORT,
    database: DB_NAME,
    user: DB_USER,
    password: DB_PASSWORD,
});

client.connect();

async function updateOrderTimestamps() {
    console.log("Updating order timestamps...");
    for (let id = 1; id <= 1000; id++) {
        const randomTimestamp = new Date(
            Date.now() - Math.floor(Math.random() * 30 * 24 * 60 * 60 * 1000)
        ).toISOString();

        const query_orders = "UPDATE orders SET order_timestamp = $1 WHERE id = $2";
        const query_bills = "UPDATE bills SET created_at = $1 WHERE orders_id = $2";
        const values = [randomTimestamp, id];

        try {
            await client.query(query_orders, values);
            await client.query(query_bills, values);
            //console.log(`Order ${id} updated with timestamp ${randomTimestamp}`);
        } catch (err) {
            console.error(`Error updating order ${id}:`, err);
        }
    }

    console.log("Order timestamps updated!");

    client.end();
}

updateOrderTimestamps();