-- IMPORTANT: DO NOT CHANGE THE GIVEN SCHEMA UNLESS YOU HAVE A GOOD REASON
-- IF YOU DO CHANGE IT WRITE THE JUSTIFICATION IN A COMMENT ABOVE THE CHANGE

drop database if exists restaurant;

create database restaurant;

use restaurant;

create table customers (
  username varchar(64) not null,
  password varchar(128) not null,

-- CHANGE JUSTIFICATION: Added primary key
  constraint pk_username primary key(username)
);

insert into customers(username, password) values
  ('fred', sha2('fred', 224)),
  ('barney', sha2('barney', 224)),
  ('wilma', sha2('wilma', 224)),
  ('betty', sha2('betty', 224)),
  ('pebbles', sha2('pebbles', 224));

-- TODO: Task 1.2
-- Write your task 1.2 below

-- Grant fred access to the DB
SELECT "GRANTING ALL PRIVILEGES TO FRED..";
GRANT ALL PRIVILEGES ON restaurant.* TO 'fred'@'%';

-- Apply changes to privileges
FLUSH PRIVILEGES;

-- ORDERS TABLE
SELECT "CREATING PLACE_ORDERS TABLE...";
CREATE TABLE place_orders (
    order_id VARCHAR(8) NOT NULL, -- this is the PK
    payment_id VARCHAR(128) UNIQUE NOT NULL,
    order_date DATE NOT NULL,
    total DECIMAL(8,2) NOT NULL,
    username varchar(64) NOT NULL, -- this is the FK

    constraint pk_order_id primary key(order_id),
    constraint fk_username foreign key (username) references customers(username)
);