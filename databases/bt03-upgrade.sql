-- Project: Servlet MVC Practice
-- Upgrade: Bài tập 03
-- Purpose: Add products and user_otps tables

USE servlet_mvc_practice;

-- =========================================================
-- Table: products
-- Relationship: categories (1) --- (n) products
-- =========================================================

CREATE TABLE IF NOT EXISTS products (
    product_id INT UNSIGNED AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    quantity INT UNSIGNED NOT NULL DEFAULT 0,
    description TEXT NULL,
    cate_id INT UNSIGNED NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_products
        PRIMARY KEY (product_id),

    CONSTRAINT ck_products_price
        CHECK (price >= 0),

    CONSTRAINT fk_products_category
        FOREIGN KEY (cate_id)
        REFERENCES categories (cate_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_products_category (cate_id),
    INDEX idx_products_newest (created_at, product_id)
) ENGINE = InnoDB;

-- =========================================================
-- Table: user_otps
-- Purpose:
--   ACCOUNT_ACTIVATION
--   PASSWORD_RESET
-- =========================================================

CREATE TABLE IF NOT EXISTS user_otps (
    otp_id BIGINT UNSIGNED AUTO_INCREMENT,
    user_id INT UNSIGNED NOT NULL,
    purpose VARCHAR(30) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    attempt_count TINYINT UNSIGNED NOT NULL DEFAULT 0,
    consumed_at DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_otps
        PRIMARY KEY (otp_id),

    CONSTRAINT ck_user_otps_purpose
        CHECK (
            purpose IN (
                'ACCOUNT_ACTIVATION',
                'PASSWORD_RESET'
            )
        ),

    CONSTRAINT fk_user_otps_user
        FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    INDEX idx_user_otps_lookup (
        user_id,
        purpose,
        consumed_at,
        created_at
    )
) ENGINE = InnoDB;