const fs = require('fs');
const readline = require('readline');
const axios = require('axios');

// Crear interfaces para leer los archivos línea por línea
const usersFileStream = fs.createReadStream('../sql/users.txt');
const customersFileStream = fs.createReadStream('../sql/customers.txt');

const usersRL = readline.createInterface({
  input: usersFileStream,
  crlfDelay: Infinity
});

const customersRL = readline.createInterface({
  input: customersFileStream,
  crlfDelay: Infinity
});

// Arrays para almacenar las líneas de cada archivo
let usersData = [];
let customersData = [];

// Leer archivo users.txt
usersRL.on('line', (line) => {
    // Usar regex para extraer los valores entre comillas simples, permitiendo comillas dentro de contraseñas
    const matches = line.match(/'((?:[^']|'')*)'/g);
  
    if (matches && matches.length === 3) {
      // Quitar las comillas simples y manejar comillas escapadas dentro de las contraseñas
      const name = matches[0].replace(/'/g, '');
      const email = matches[1].replace(/'/g, '');
      const password = matches[2].replace(/'/g, '').replace(/''/g, "'");
  
      // Agregar los datos extraídos al array
      usersData.push({ name, email, password });
    } else {
      console.log(`Línea no válida en users.txt: ${line}`);
    }
  });

// Leer archivo customers.txt
customersRL.on('line', (line) => {
    // Usar regex para extraer 'bill_name' entre comillas y 'nit' como número (sin comillas)
    const matches = line.match(/'([^']+)'|(\d+)/g);
  
    if (matches && matches.length >= 3) {
      const billName = matches[0].replace(/'/g, '');  // nombre de factura
      const nit = matches[1];                         // nit (número)
      const userId = matches[2];                      // id de usuario (por si lo necesitas)
  
      // Agregar los datos extraídos al array
      customersData.push({ billName, nit, userId });
    } else {
      console.log(`Línea no válida en customers.txt: ${line}`);
    }
  });
  

// Al cerrar ambos archivos, combinar los datos y enviarlos al endpoint
usersRL.on('close', () => {
  customersRL.on('close', async () => {
    if (usersData.length === customersData.length) {
      for (let i = 0; i < usersData.length; i++) {
        // Combinar los datos de users.txt y customers.txt
        const userData = {
          name: usersData[i].name,
          email: usersData[i].email,
          password: usersData[i].password,
          nit: customersData[i].nit,
          billName: customersData[i].billName
        };

        // Hacer la petición POST al endpoint
        try {
          const response = await axios.post(`http://146.190.141.101:8080/api/v1/public/signup?type=customer`, userData, {
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            }
          });
          console.log(`Usuario ${userData.name} agregado correctamente`);
        } catch (error) {
          console.error(`Error al agregar el usuario ${userData.name}: `, error.message);
        }
      }
    } else {
      console.error('La cantidad de líneas en users.txt no coincide con customers.txt');
    }
  });
});
