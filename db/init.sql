-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2024-10-01 20:56:41.953

-- tables
-- Table: Bills
CREATE TABLE Bills (
    id int  NOT NULL,
    orders_id int  NOT NULL,
    bill_name varchar(255)  NOT NULL,
    nit varchar(20)  NOT NULL,
    total_cost decimal(10,2)  NOT NULL,
    created_at timestamp  NULL,
    updated_at timestamp  NULL,
    CONSTRAINT Bills_pk PRIMARY KEY (id)
);

-- Table: Customers
CREATE TABLE Customers (
    id serial  NOT NULL,
    users_id int  NOT NULL,
    bill_name varchar(255)  NOT NULL,
    nit varchar(20)  NOT NULL,
    created_at timestamp  NULL DEFAULT current_timestamp,
    updated_at timestamp  NULL DEFAULT current_timestamp,
    CONSTRAINT Customers_pk PRIMARY KEY (id)
);

-- Table: MenuItems
CREATE TABLE MenuItems (
    id serial  NOT NULL,
    name varchar(255)  NOT NULL,
    image_url varchar(255)  NOT NULL,
    description text  NULL,
    price decimal(10,2)  NOT NULL,
    created_at timestamp  NULL DEFAULT current_timestamp,
    updated_at timestamp  NULL DEFAULT current_timestamp,
    CONSTRAINT MenuItems_pk PRIMARY KEY (id)
);

-- Table: OrderItems
CREATE TABLE OrderItems (
    id serial  NOT NULL,
    order_id integer  NULL,
    menu_item_id integer  NULL,
    quantity integer  NOT NULL,
    price decimal(10,2)  NOT NULL,
    created_at timestamp  NULL DEFAULT current_timestamp,
    updated_at timestamp  NULL DEFAULT current_timestamp,
    CONSTRAINT OrderItems_pk PRIMARY KEY (id)
);

-- Table: Orders
CREATE TABLE Orders (
    id serial  NOT NULL,
    customer_id integer  NULL,
    order_timestamp timestamp  NULL DEFAULT current_timestamp,
    status varchar(50)  NOT NULL DEFAULT 'pending',
    created_at timestamp  NULL DEFAULT current_timestamp,
    updated_at timestamp  NULL DEFAULT current_timestamp,
    CONSTRAINT Orders_pk PRIMARY KEY (id)
);

-- Table: Users
CREATE TABLE Users (
    id serial  NOT NULL,
    kc_user_id varchar(255)  NULL,
    name varchar(255)  NOT NULL,
    email varchar(255)  NOT NULL,
    password varchar(255)  NOT NULL,
    created_at timestamp  NULL DEFAULT current_timestamp,
    updated_at timestamp  NULL DEFAULT current_timestamp,
    CONSTRAINT AK_0 UNIQUE (email) NOT DEFERRABLE  INITIALLY IMMEDIATE,
    CONSTRAINT Users_pk PRIMARY KEY (id)
);

-- End of file.

