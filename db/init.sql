/* -- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2024-10-01 20:56:41.953

-- tables
-- Table: bills
CREATE TABLE bills (
                       id serial  NOT NULL,
                       orders_id int  NOT NULL,
                       bill_name varchar(255)  NOT NULL,
                       nit varchar(20)  NOT NULL,
                       total_cost decimal(10,2)  NOT NULL,
                       created_at timestamp  NULL,
                       updated_at timestamp  NULL,
                       CONSTRAINT bills_pk PRIMARY KEY (id)
);

-- Table: customers
CREATE TABLE customers (
                           id serial  NOT NULL,
                           users_id int  NOT NULL,
                           bill_name varchar(255)  NOT NULL,
                           nit varchar(20)  NOT NULL,
                           created_at timestamp  NULL DEFAULT current_timestamp,
                           updated_at timestamp  NULL DEFAULT current_timestamp,
                           CONSTRAINT customers_pk PRIMARY KEY (id)
);

-- Table: menu_items
CREATE TABLE menu_items (
                            id serial  NOT NULL,
                            name varchar(255)  NOT NULL,
                            image_url varchar(255)  NOT NULL,
                            description text  NULL,
                            price decimal(10,2)  NOT NULL,
                            created_at timestamp  NULL DEFAULT current_timestamp,
                            updated_at timestamp  NULL DEFAULT current_timestamp,
                            CONSTRAINT menu_items_pk PRIMARY KEY (id)
);

-- Table: order_items
CREATE TABLE order_items (
                             id serial  NOT NULL,
                             order_id integer  NULL,
                             menu_item_id integer  NULL,
                             quantity integer  NOT NULL,
                             price decimal(10,2)  NOT NULL,
                             created_at timestamp  NULL DEFAULT current_timestamp,
                             updated_at timestamp  NULL DEFAULT current_timestamp,
                             CONSTRAINT order_items_pk PRIMARY KEY (id)
);

-- Table: orders
CREATE TABLE orders (
                        id serial  NOT NULL,
                        customer_id integer  NULL,
                        order_timestamp timestamp  NULL DEFAULT current_timestamp,
                        status varchar(50)  NOT NULL DEFAULT 'pending',
                        created_at timestamp  NULL DEFAULT current_timestamp,
                        updated_at timestamp  NULL DEFAULT current_timestamp,
                        CONSTRAINT orders_pk PRIMARY KEY (id)
);

-- Table: users
CREATE TABLE users (
                       id serial  NOT NULL,
                       kc_user_id varchar(255)  NULL,
                       name varchar(255)  NOT NULL,
                       email varchar(255)  NOT NULL,
                       password varchar(255)  NOT NULL,
                       created_at timestamp  NULL DEFAULT current_timestamp,
                       updated_at timestamp  NULL DEFAULT current_timestamp,
                       CONSTRAINT AK_0 UNIQUE (email) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                       CONSTRAINT users_pk PRIMARY KEY (id)
);

-- End of file.
*/
