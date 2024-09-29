# Introducción

Este documento explica el orden en que se deben cargar los datos simulados (mockdata) en la base de datos. Seguir este orden garantiza la integridad y consistencia de los datos durante el proceso de pruebas y desarrollo.

1. **Users**: Cargar primero los datos de usuarios para establecer las relaciones básicas.
2. **Customers**: A continuación, cargar los datos de clientes que los usuarios pueden interactuar.
3. **Orders**: Luego, cargar los datos de pedidos que relacionan usuarios y clientes.
4. **Bills**: Luego, cargar los datos de facturas que están asociados con los pedidos.
5. continuar...

Asegúrese de seguir este orden para evitar problemas de referencia y asegurar que todas las relaciones entre los datos se mantengan correctamente.