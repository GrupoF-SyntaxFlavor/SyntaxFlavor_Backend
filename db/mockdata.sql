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

-- Insert data into Orders
-- pending-cancelled-delivered
INSERT INTO Orders (custom, order_timestamp, status, created_at, updated_at) VALUES
(1, '2024-09-18 12:34:56', 'pending', '2024-09-18 12:34:56', '2024-09-18 12:34:56'),  -- Pedido 1
(2, '2024-09-18 08:12:34', 'pending', '2024-09-18 08:12:34', '2024-09-18 08:12:34'),  -- Pedido 2
(3, '2024-09-18 14:23:45', 'pending', '2024-09-18 14:23:45', '2024-09-18 14:23:45'),  -- Pedido 3
(1, '2024-09-18 09:45:12', 'pending', '2024-09-18 09:45:12', '2024-09-18 09:45:12'),  -- Pedido 4
(2, '2024-09-18 10:05:23', 'pending', '2024-09-18 10:05:23', '2024-09-18 10:05:23'),  -- Pedido 5
(3, '2024-09-18 13:22:19', 'pending', '2024-09-18 13:22:19', '2024-09-18 13:22:19'),  -- Pedido 6
(1, '2024-09-18 11:15:30', 'pending', '2024-09-18 11:15:30', '2024-09-18 11:15:30'),  -- Pedido 7
(2, '2024-09-18 16:45:12', 'pending', '2024-09-18 16:45:12', '2024-09-18 16:45:12'),  -- Pedido 8
(3, '2024-09-18 07:50:34', 'pending', '2024-09-18 07:50:34', '2024-09-18 07:50:34'),  -- Pedido 9
(1, '2024-09-18 15:00:00', 'pending', '2024-09-18 15:00:00', '2024-09-18 15:00:00'),  -- Pedido 10
(2, '2024-09-18 09:00:45', 'pending', '2024-09-18 09:00:45', '2024-09-18 09:00:45'),  -- Pedido 11
(3, '2024-09-18 14:55:12', 'pending', '2024-09-18 14:55:12', '2024-09-18 14:55:12'),  -- Pedido 12
(1, '2024-09-18 08:30:15', 'pending', '2024-09-18 08:30:15', '2024-09-18 08:30:15'),  -- Pedido 13
(2, '2024-09-18 13:10:40', 'pending', '2024-09-18 13:10:40', '2024-09-18 13:10:40'),  -- Pedido 14
(3, '2024-09-18 15:22:30', 'pending', '2024-09-18 15:22:30', '2024-09-18 15:22:30'),  -- Pedido 15
(1, '2024-09-18 12:00:00', 'pending', '2024-09-18 12:00:00', '2024-09-18 12:00:00'),  -- Pedido 16
(2, '2024-09-18 11:45:33', 'pending', '2024-09-18 11:45:33', '2024-09-18 11:45:33'),  -- Pedido 17
(3, '2024-09-18 07:30:45', 'pending', '2024-09-18 07:30:45', '2024-09-18 07:30:45'),  -- Pedido 18
(1, '2024-09-18 10:22:18', 'pending', '2024-09-18 10:22:18', '2024-09-18 10:22:18'),  -- Pedido 19
(2, '2024-09-18 16:00:00', 'pending', '2024-09-18 16:00:00', '2024-09-18 16:00:00'),  -- Pedido 20
(1, '2024-09-18 11:00:11', 'pending', '2024-09-18 11:00:11', '2024-09-18 11:00:11'),  -- Pedido 21
(2, '2024-09-18 08:15:30', 'pending', '2024-09-18 08:15:30', '2024-09-18 08:15:30'),  -- Pedido 22
(3, '2024-09-18 14:05:20', 'pending', '2024-09-18 14:05:20', '2024-09-18 14:05:20'),  -- Pedido 23
(1, '2024-09-18 09:30:40', 'pending', '2024-09-18 09:30:40', '2024-09-18 09:30:40'),  -- Pedido 24
(2, '2024-09-18 15:45:50', 'pending', '2024-09-18 15:45:50', '2024-09-18 15:45:50');  -- Pedido 25


-- Insert data into OrderItems
INSERT INTO order_items (order_id, menu_item_id, quantity, price, created_at, updated_at) VALUES
-- Pedido 1 con 2 items
(1, 1, 2, 50.00, '2024-09-18 12:34:56', '2024-09-18 12:34:56'),  -- Onigiris de Atún x2
(1, 7, 1, 30.00, '2024-09-18 12:34:56', '2024-09-18 12:34:56'),  -- Pastel de Chocolate x1
-- Pedido 2 con 3 items
(2, 1, 2, 50.00, '2024-09-18 08:12:34', '2024-09-18 08:12:34'), -- Onigiris de Atún x2
(2, 6, 1, 15.00, '2024-09-18 08:12:34', '2024-09-18 08:12:34'), -- Té Helado x1
(2, 5, 1, 35.00, '2024-09-18 08:12:34', '2024-09-18 08:12:34'), -- Hamburguesa Clásica x1
-- Pedido 3 con 2 items
(3, 3, 1, 40.00, '2024-09-18 14:23:45', '2024-09-18 14:23:45'), -- Tacos de Pollo x1
(3, 7, 1, 30.00, '2024-09-18 14:23:45', '2024-09-18 14:23:45'), -- Pastel de Chocolate x1
-- Pedido 4 con 1 item
(4, 2, 1, 30.00, '2024-09-18 09:45:12', '2024-09-18 09:45:12'), -- Cheesecake de Uvas x1
-- Pedido 5 con 4 items
(5, 3, 2, 80.00, '2024-09-18 10:05:23', '2024-09-18 10:05:23'), -- Tacos de Pollo x2
(5, 6, 3, 45.00, '2024-09-18 10:05:23', '2024-09-18 10:05:23'), -- Té Helado x3
(5, 1, 1, 25.00, '2024-09-18 10:05:23', '2024-09-18 10:05:23'), -- Onigiris de Atún x1
(5, 7, 1, 30.00, '2024-09-18 10:05:23', '2024-09-18 10:05:23'), -- Pastel de Chocolate x1
-- Pedido 6 con 2 items
(6, 4, 2, 100.00, '2024-09-18 13:22:19', '2024-09-18 13:22:19'), -- Pizza de Pepperoni x2
(6, 5, 1, 35.00, '2024-09-18 13:22:19', '2024-09-18 13:22:19'), -- Hamburguesa Clásica x1
-- Pedido 7 con 3 items
(7, 1, 3, 75.00, '2024-09-18 11:15:30', '2024-09-18 11:15:30'), -- Onigiris de Atún x3
(7, 2, 2, 60.00, '2024-09-18 11:15:30', '2024-09-18 11:15:30'), -- Cheesecake de Uvas x2
(7, 6, 1, 15.00, '2024-09-18 11:15:30', '2024-09-18 11:15:30'), -- Té Helado x1
-- Pedido 8 con 1 item
(8, 5, 1, 35.00, '2024-09-18 16:45:12', '2024-09-18 16:45:12'), -- Hamburguesa Clásica x1
-- Pedido 9 con 3 items
(9, 3, 2, 80.00, '2024-09-18 07:50:34', '2024-09-18 07:50:34'), -- Tacos de Pollo x2
(9, 7, 1, 30.00, '2024-09-18 07:50:34', '2024-09-18 07:50:34'), -- Pastel de Chocolate x1
(9, 6, 1, 15.00, '2024-09-18 07:50:34', '2024-09-18 07:50:34'), -- Té Helado x1
-- Pedido 10 con 2 items
(10, 4, 1, 50.00, '2024-09-18 15:00:00', '2024-09-18 15:00:00'), -- Pizza de Pepperoni x1
(10, 2, 2, 60.00, '2024-09-18 15:00:00', '2024-09-18 15:00:00'), -- Cheesecake de Uvas x2
-- Pedido 11 con 1 item
(11, 6, 1, 15.00, '2024-09-18 09:00:45', '2024-09-18 09:00:45'), -- Té Helado x1
-- Pedido 12 con 3 items
(12, 7, 2, 60.00, '2024-09-18 14:55:12', '2024-09-18 14:55:12'), -- Pastel de Chocolate x2
(12, 4, 1, 50.00, '2024-09-18 14:55:12', '2024-09-18 14:55:12'), -- Pizza de Pepperoni x1
(12, 1, 1, 25.00, '2024-09-18 14:55:12', '2024-09-18 14:55:12'), -- Onigiris de Atún x1
-- Pedido 13 con 2 items
(13, 5, 2, 70.00, '2024-09-18 08:30:15', '2024-09-18 08:30:15'), -- Hamburguesa Clásica x2
(13, 2, 1, 30.00, '2024-09-18 08:30:15', '2024-09-18 08:30:15'), -- Cheesecake de Uvas x1
-- Pedido 14 con 3 items
(14, 3, 2, 80.00, '2024-09-18 13:10:40', '2024-09-18 13:10:40'), -- Tacos de Pollo x2
(14, 7, 1, 30.00, '2024-09-18 13:10:40', '2024-09-18 13:10:40'), -- Pastel de Chocolate x1
(14, 1, 1, 25.00, '2024-09-18 13:10:40', '2024-09-18 13:10:40'), -- Onigiris de Atún x1
-- Pedido 15 con 4 items
(15, 4, 2, 100.00, '2024-09-18 15:22:30', '2024-09-18 15:22:30'), -- Pizza de Pepperoni x2
(15, 5, 1, 35.00, '2024-09-18 15:22:30', '2024-09-18 15:22:30'), -- Hamburguesa Clásica x1
(15, 2, 2, 60.00, '2024-09-18 15:22:30', '2024-09-18 15:22:30'), -- Cheesecake de Uvas x2
(15, 7, 1, 30.00, '2024-09-18 15:22:30', '2024-09-18 15:22:30'), -- Pastel de Chocolate x1
-- Pedido 16 con 2 items
(16, 6, 2, 30.00, '2024-09-18 12:00:00', '2024-09-18 12:00:00'), -- Té Helado x2
(16, 3, 1, 40.00, '2024-09-18 12:00:00', '2024-09-18 12:00:00'), -- Tacos de Pollo x1
-- Pedido 17 con 1 item
(17, 5, 1, 35.00, '2024-09-18 11:45:33', '2024-09-18 11:45:33'), -- Hamburguesa Clásica x1
-- Pedido 18 con 3 items
(18, 4, 2, 100.00, '2024-09-18 07:30:45', '2024-09-18 07:30:45'), -- Pizza de Pepperoni x2
(18, 7, 1, 30.00, '2024-09-18 07:30:45', '2024-09-18 07:30:45'), -- Pastel de Chocolate x1
(18, 6, 1, 15.00, '2024-09-18 07:30:45', '2024-09-18 07:30:45'), -- Té Helado x1
-- Pedido 19 con 2 items
(19, 3, 2, 80.00, '2024-09-18 10:22:18', '2024-09-18 10:22:18'), -- Tacos de Pollo x2
(19, 6, 1, 15.00, '2024-09-18 10:22:18', '2024-09-18 10:22:18'), -- Té Helado x1
-- Pedido 20 con 4 items
(20, 4, 2, 100.00, '2024-09-18 16:00:00', '2024-09-18 16:00:00'), -- Pizza de Pepperoni x2
(20, 2, 1, 30.00, '2024-09-18 16:00:00', '2024-09-18 16:00:00'), -- Cheesecake de Uvas x1
(20, 1, 2, 50.00, '2024-09-18 16:00:00', '2024-09-18 16:00:00'), -- Onigiris de Atún x2
(20, 7, 1, 30.00, '2024-09-18 16:00:00', '2024-09-18 16:00:00'), -- Pastel de Chocolate x1
-- Pedido 21 con 1 item
(21, 6, 1, 15.00, '2024-09-18 11:00:11', '2024-09-18 11:00:11'), -- Té Helado x1
-- Pedido 22 con 2 items
(22, 2, 2, 60.00, '2024-09-18 08:15:30', '2024-09-18 08:15:30'), -- Cheesecake de Uvas x2
(22, 5, 1, 35.00, '2024-09-18 08:15:30', '2024-09-18 08:15:30'), -- Hamburguesa Clásica x1
-- Pedido 23 con 3 items
(23, 3, 1, 40.00, '2024-09-18 14:05:20', '2024-09-18 14:05:20'), -- Tacos de Pollo x1
(23, 7, 1, 30.00, '2024-09-18 14:05:20', '2024-09-18 14:05:20'), -- Pastel de Chocolate x1
(23, 6, 1, 15.00, '2024-09-18 14:05:20', '2024-09-18 14:05:20'), -- Té Helado x1
-- Pedido 24 con 2 items
(24, 5, 1, 35.00, '2024-09-18 09:30:40', '2024-09-18 09:30:40'), -- Hamburguesa Clásica x1
(24, 2, 1, 30.00, '2024-09-18 09:30:40', '2024-09-18 09:30:40'), -- Cheesecake de Uvas x1
-- Pedido 25 con 4 items
(25, 3, 2, 80.00, '2024-09-18 15:45:50', '2024-09-18 15:45:50'), -- Tacos de Pollo x2
(25, 6, 1, 15.00, '2024-09-18 15:45:50', '2024-09-18 15:45:50'), -- Té Helado x1
(25, 1, 2, 50.00, '2024-09-18 15:45:50', '2024-09-18 15:45:50'), -- Onigiris de Atún x2
(25, 4, 1, 50.00, '2024-09-18 15:45:50', '2024-09-18 15:45:50'); -- Pizza de Pepperoni x1

-- Insert data into Bills
INSERT INTO Bills (id, orders_id, nit, bill_name, total_cost, created_at, updated_at) VALUES
(1, 1, '123456789', 'John Doe Billing', 90.00, '2024-09-18 12:34:56', '2024-09-18 12:34:56'),  -- Bill for order 1
(2, 2, '987654321', 'Jane Smith Billing', 135.00, '2024-09-18 08:12:34', '2024-09-18 08:12:34'), -- Bill for order 2
(3, 3, '456789123', 'Alice Johnson Billing', 90.00, '2024-09-18 14:23:45', '2024-09-18 14:23:45'), -- Bill for order 3
(4, 4, '123456789', 'John Doe Billing', 15.00, '2024-09-18 09:45:12', '2024-09-18 09:45:12'), -- Bill for order 4
(5, 5, '987654321', 'Jane Smith Billing', 60.00, '2024-09-18 10:05:23', '2024-09-18 10:05:23'), -- Bill for order 5
(6, 6, '456789123', 'Alice Johnson Billing', 25.00, '2024-09-18 13:22:19', '2024-09-18 13:22:19'), -- Bill for order 6
(7, 7, '123456789', 'John Doe Billing', 105.00, '2024-09-18 11:15:30', '2024-09-18 11:15:30'), -- Bill for order 7
(8, 8, '987654321', 'Jane Smith Billing', 100.00, '2024-09-18 16:45:12', '2024-09-18 16:45:12'), -- Bill for order 8
(9, 9, '456789123', 'Alice Johnson Billing', 15.00, '2024-09-18 07:50:34', '2024-09-18 07:50:34'), -- Bill for order 9
(10, 10, '123456789', 'John Doe Billing', 90.00, '2024-09-18 15:00:00', '2024-09-18 15:00:00'), -- Bill for order 10
(11, 11, '987654321', 'Jane Smith Billing', 95.00, '2024-09-18 09:00:45', '2024-09-18 09:00:45'), -- Bill for order 11
(12, 12, '456789123', 'Alice Johnson Billing', 35.00, '2024-09-18 14:55:12', '2024-09-18 14:55:12'), -- Bill for order 12
(13, 13, '123456789', 'John Doe Billing', 120.00, '2024-09-18 08:30:15', '2024-09-18 08:30:15'), -- Bill for order 13
(14, 14, '987654321', 'Jane Smith Billing', 25.00, '2024-09-18 13:10:40', '2024-09-18 13:10:40'), -- Bill for order 14
(15, 15, '456789123', 'Alice Johnson Billing', 100.00, '2024-09-18 15:22:30', '2024-09-18 15:22:30'), -- Bill for order 15
(16, 16, '123456789', 'John Doe Billing', 60.00, '2024-09-18 12:00:00', '2024-09-18 12:00:00'), -- Bill for order 16
(17, 17, '987654321', 'Jane Smith Billing', 105.00, '2024-09-18 11:45:33', '2024-09-18 11:45:33'), -- Bill for order 17
(18, 18, '456789123', 'Alice Johnson Billing', 30.00, '2024-09-18 07:30:45', '2024-09-18 07:30:45'), -- Bill for order 18
(19, 19, '123456789', 'John Doe Billing', 45.00, '2024-09-18 10:22:18', '2024-09-18 10:22:18'), -- Bill for order 19
(20, 20, '987654321', 'Jane Smith Billing', 60.00, '2024-09-18 16:00:00', '2024-09-18 16:00:00'), -- Bill for order 20
(21, 21, '123456789', 'John Doe Billing', 130.00, '2024-09-18 11:00:11', '2024-09-18 11:00:11'), -- Bill for order 21
(22, 22, '987654321', 'Jane Smith Billing', 35.00, '2024-09-18 08:15:30', '2024-09-18 08:15:30'), -- Bill for order 22
(23, 23, '456789123', 'Alice Johnson Billing', 90.00, '2024-09-18 14:05:20', '2024-09-18 14:05:20'), -- Bill for order 23
(24, 24, '123456789', 'John Doe Billing', 140.00, '2024-09-18 09:30:40', '2024-09-18 09:30:40'), -- Bill for order 24
(25, 25, '987654321', 'Jane Smith Billing', 60.00, '2024-09-18 15:45:50', '2024-09-18 15:45:50'); -- Bill for order 25