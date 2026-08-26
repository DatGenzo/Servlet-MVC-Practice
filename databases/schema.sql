-- Project: Servlet MVC Practice
-- Database: MySQL
-- Purpose: Login, Cookie, Session and Category CRUD

CREATE DATABASE IF NOT EXISTS servlet_mvc_practice
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE servlet_mvc_practice;

-- Table: users

CREATE TABLE IF NOT EXISTS users (
    user_id INT UNSIGNED AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_users
        PRIMARY KEY (user_id),

    CONSTRAINT uq_users_username
        UNIQUE (username),

    CONSTRAINT uq_users_email
        UNIQUE (email)
);

-- Table: categories

CREATE TABLE IF NOT EXISTS categories (
    cate_id INT UNSIGNED AUTO_INCREMENT,
    cate_name VARCHAR(100) NOT NULL,
    icons VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_categories
        PRIMARY KEY (cate_id),

    CONSTRAINT uq_categories_name
        UNIQUE (cate_name)
);