-- Insert data into Users
INSERT INTO Users (name, email, phone, created_at, updated_at) VALUES
('John Doe', 'john.doe@example.com', '123-456-7890', current_timestamp, current_timestamp),
('Jane Smith', 'jane.smith@example.com', '987-654-3210', current_timestamp, current_timestamp),
('Alice Johnson', 'alice.j@example.com', '555-123-4567', current_timestamp, current_timestamp);

-- Insert data into Customers
INSERT INTO Customers (users_id, nit, bill_name) VALUES 
(1, '123456789', 'John Doe Billing'),
(2, '987654321', 'Jane Smith Billing'),
(3, '456789123', 'Michael Johnson Billing');
-- Insert data into MenuItems
INSERT INTO menu_items (name, description, price, created_at, updated_at) VALUES
('Margherita Pizza', 'Classic pizza with tomatoes, mozzarella, and basil', 8.99, current_timestamp, current_timestamp),
('Pepperoni Pizza', 'Pizza topped with pepperoni and cheese', 9.99, current_timestamp, current_timestamp),
('Caesar Salad', 'Fresh salad with romaine lettuce, croutons, and Caesar dressing', 6.99, current_timestamp, current_timestamp);

-- Insert data into Orders
INSERT INTO Orders (custom, order_timestamp, status, created_at, updated_at) VALUES
(1, current_timestamp, 'completed', current_timestamp, current_timestamp),
(2, current_timestamp, 'pending', current_timestamp, current_timestamp),
(3, current_timestamp, 'in progress', current_timestamp, current_timestamp);

-- Insert data into OrderItems
INSERT INTO order_items (order_id, menu_item_id, quantity, price, created_at, updated_at) VALUES
(1, 1, 2, 17.98, current_timestamp, current_timestamp), -- Margherita Pizza x2
(1, 3, 1, 6.99, current_timestamp, current_timestamp), -- Caesar Salad x1
(2, 2, 3, 29.97, current_timestamp, current_timestamp), -- Pepperoni Pizza x3
(3, 1, 1, 8.99, current_timestamp, current_timestamp);  -- Margherita Pizza x1

-- Insert data into Bills
INSERT INTO Bills (id, orders_id, nit, bill_name, total_cost, created_at, updated_at) VALUES
(1, 1, '123456789', 'Smith', 24.97, current_timestamp, current_timestamp), -- Bill for order 1
(2, 2, '987654321', 'Johnson', 29.97, current_timestamp, current_timestamp), -- Bill for order 2
(3, 3, '555555555', 'Doe', 8.99, current_timestamp, current_timestamp);  -- Bill for order 3

-- Insertar en la tabla Customers, utilizando los ids correspondientes de Users

INSERT INTO Customers (users_id, nit, bill_name)
VALUES (3, '456789123', 'Michael Johnson Billing');

-- Insertando datos en la tabla MenuItems
INSERT INTO menu_items (name, description, price, created_at, updated_at) VALUES
    ('Onigiris de Atún', 
    'Deliciosos triángulos de arroz rellenos de atún fresco, sazonados con un toque de salsa de soya y envueltos en una capa de alga nori crujiente.', 
    25, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP 
    -- Imagen: 'https://images.pond5.com/pixel-sushi-vector-illustration-isolated-illustration-155825087_iconm.jpeg'
    ),
    ('Cheesecake de Uvas', 
    'Un postre delicioso y fresco, perfecto para cualquier ocasión.', 
    30, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP 
    -- Imagen: 'https://assets.tmecosys.com/image/upload/t_web767x639/img/recipe/vimdb/230649.jpg'
    ),
    ('Tacos de Pollo', 
    'Tacos de pollo con guacamole y salsa de chipotle.', 
    40, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP 
    -- Imagen: 'https://www.vvsupremo.com/wp-content/uploads/2017/06/Chicken-Tacos-900x570-sRGB.jpg'
    ),
    ('Pizza de Pepperoni', 
    'Pizza de pepperoni con queso mozzarella y salsa de tomate.', 
    50, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP 
    -- Imagen: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIoXjS-sXqWGIsMTB_m3av-Oh-Fgi93hBrzg&s'
    ),
    ('Hamburguesa Clásica', 
    'Hamburguesa con carne de res, lechuga, tomate, cebolla y queso cheddar.', 
    35, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP 
    -- Imagen: 'https://img.freepik.com/fotos-premium/foto-stock-hamburguesa-clasica-aislada-blanco_940723-217.jpg'
    ),
    ('Té Helado', 
    'Té helado de limón, perfecto para refrescarte en un día caluroso.', 
    15, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP 
    -- Imagen: 'https://imag.bonviveur.com/te-helado.jpg'
    ),
    ('Pastel de Chocolate', 
    'Un pastel de chocolate esponjoso y delicioso, perfecto para los amantes del chocolate.', 
    30, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP 
    -- Imagen: 'https://i.pinimg.com/736x/42/36/b1/4236b10d070cb898106d84a6f2fa4a2c.jpg'
);
