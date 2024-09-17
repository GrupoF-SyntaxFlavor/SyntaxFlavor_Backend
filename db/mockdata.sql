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

