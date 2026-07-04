-- Insert sample customers
INSERT INTO customers (name, email, phone, address) VALUES
('John Doe', 'john.doe@email.com', '+1-555-0101', '123 Main St, New York, NY 10001'),
('Jane Smith', 'jane.smith@email.com', '+1-555-0102', '456 Oak Ave, Los Angeles, CA 90001'),
('Bob Johnson', 'bob.johnson@email.com', '+1-555-0103', '789 Pine Rd, Chicago, IL 60601'),
('Alice Williams', 'alice.williams@email.com', '+1-555-0104', '321 Elm St, Houston, TX 77001'),
('Charlie Brown', 'charlie.brown@email.com', '+1-555-0105', '654 Maple Dr, Phoenix, AZ 85001');

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category) VALUES
('Laptop Pro 15', 'High-performance laptop with 16GB RAM', 1299.99, 50, 'Electronics'),
('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 29.99, 200, 'Electronics'),
('Office Chair', 'Comfortable ergonomic office chair', 249.99, 75, 'Furniture'),
('Desk Lamp', 'LED desk lamp with adjustable brightness', 39.99, 150, 'Furniture'),
('USB-C Cable', 'Premium USB-C charging cable 6ft', 19.99, 300, 'Accessories'),
('Keyboard Mechanical', 'RGB mechanical gaming keyboard', 149.99, 100, 'Electronics'),
('Monitor 27"', '4K UHD monitor with HDR support', 399.99, 60, 'Electronics'),
('Webcam HD', '1080p HD webcam with microphone', 79.99, 120, 'Electronics'),
('Notebook Set', 'Set of 3 premium notebooks', 24.99, 250, 'Stationery'),
('Pen Set', 'Professional pen set with case', 34.99, 180, 'Stationery');

-- Insert sample orders
INSERT INTO orders (customer_id, status, total_amount, shipping_address) VALUES
(1, 'DELIVERED', 1349.98, '123 Main St, New York, NY 10001'),
(1, 'SHIPPED', 69.98, '123 Main St, New York, NY 10001'),
(2, 'PROCESSING', 649.98, '456 Oak Ave, Los Angeles, CA 90001'),
(2, 'DELIVERED', 479.98, '456 Oak Ave, Los Angeles, CA 90001'),
(3, 'PENDING', 1699.97, '789 Pine Rd, Chicago, IL 60601'),
(4, 'DELIVERED', 289.97, '321 Elm St, Houston, TX 77001'),
(4, 'SHIPPED', 59.98, '321 Elm St, Houston, TX 77001'),
(5, 'DELIVERED', 174.98, '654 Maple Dr, Phoenix, AZ 85001');

-- Insert sample order items
-- Order 1 (Customer 1 - John Doe)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(1, 1, 1, 1299.99, 1299.99),
(1, 2, 1, 29.99, 29.99),
(1, 5, 1, 19.99, 19.99);

-- Order 2 (Customer 1 - John Doe)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(2, 4, 1, 39.99, 39.99),
(2, 2, 1, 29.99, 29.99);

-- Order 3 (Customer 2 - Jane Smith)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(3, 3, 2, 249.99, 499.98),
(3, 6, 1, 149.99, 149.99);

-- Order 4 (Customer 2 - Jane Smith)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(4, 7, 1, 399.99, 399.99),
(4, 8, 1, 79.99, 79.99);

-- Order 5 (Customer 3 - Bob Johnson)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(5, 1, 1, 1299.99, 1299.99),
(5, 7, 1, 399.99, 399.99);

-- Order 6 (Customer 4 - Alice Williams)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(6, 3, 1, 249.99, 249.99),
(6, 4, 1, 39.99, 39.99);

-- Order 7 (Customer 4 - Alice Williams)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(7, 9, 2, 24.99, 49.98),
(7, 10, 1, 34.99, 34.99);

-- Order 8 (Customer 5 - Charlie Brown)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(8, 6, 1, 149.99, 149.99),
(8, 9, 1, 24.99, 24.99);

-- Made with Bob
