const fs = require('fs');
const readline = require('readline');
const axios = require('axios');

// Crear interfaces para leer los archivos línea por línea
const kitchenUsersFileStream = fs.createReadStream('../sql/kitchen_users.sql');
const kitchensFileStream = fs.createReadStream('../sql/kitchens.sql');

const kitchenUsersRL = readline.createInterface({
    input: kitchenUsersFileStream,
    crlfDelay: Infinity
});

const kitchensRL = readline.createInterface({
    input: kitchensFileStream,
    crlfDelay: Infinity
});

// Arrays para almacenar las líneas de cada archivo
let kitchenUsersData = [];
let kitchensData = [];

// Leer archivo kitchen_users.sql
kitchenUsersRL.on('line', (line) => {
    // Usar regex para extraer los valores entre comillas simples, permitiendo comillas dentro de contraseñas
    const matches = line.match(/'((?:[^']|'')*)'/g);

    if (matches && matches.length === 3) {
        //console.log("matches: ", matches);
        // Quitar las comillas simples y manejar comillas escapadas dentro de las contraseñas
        const name = matches[0].replace(/'/g, '');
        const email = matches[1].replace(/'/g, '');
        const password = matches[2].replace(/'/g, '').replace(/''/g, "'");

        // Agregar los datos extraídos al array
        kitchenUsersData.push({ name, email, password });
    } else {
        console.log(`Línea no válida en kitchen_users.sql: ${line}`);
    }
});

// Leer archivo kitchens.sql
kitchensRL.on('line', (line) => {
    // Usar regex para extraer 'bill_name' entre comillas y 'nit' como número (sin comillas)
    const matches = line.match(/'([^']+)'|(\d+)/g);

    if (matches && matches.length == 2) {
        const location = matches[0].replace(/'/g, '');  // nombre de factura
        const userId = matches[1];                         // id de usuario (por si lo necesitas)

        // Agregar los datos extraídos al array
        kitchensData.push({ location, userId });
    } else {
        console.log(`Línea no válida en kitchens.sql: ${line}`);
    }
});

// Al cerrar ambos archivos, combinar los datos y enviarlos al endpoint
kitchenUsersRL.on('close', () => {
    kitchensRL.on('close', async () => {
        if(kitchenUsersData.length !== kitchensData.length) {
            console.log('La cantidad de usuarios de cocina y cocinas no coincide');
            return;
        }

        for (let i = 0; i < kitchenUsersData.length; i++) {
            // Combinar los datos de kitchen_users.sql y kitchens.sql
            const kitchenUserData = {
                name: kitchenUsersData[i].name,
                email: kitchenUsersData[i].email,
                password: kitchenUsersData[i].password,
                location: kitchensData[i].location
            };

            // Enviar los datos al endpoint
            try {
                //console.log(kitchenUserData);
                const response = await axios.post('http://localhost:8080/api/v1/public/signup?type=kitchen', kitchenUserData);
                console.log(`Usuario de cocina creado: ${response.data.payload.name}`);
            } catch (error) {
                console.error(`Error al crear el usuario de cocina: ${error.message}`);
            }
        }
    });
});
