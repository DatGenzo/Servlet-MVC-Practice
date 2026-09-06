USE servlet_mvc_practice;

INSERT IGNORE INTO categories (cate_name, icons)
VALUES
    ('Điện thoại', NULL),
    ('Máy tính xách tay', NULL),
    ('Phụ kiện', NULL);

INSERT IGNORE INTO users (
    username,
    password_hash,
    full_name,
    email,
    role,
    is_active
)
VALUES (
    'admin',
    '$2a$12$1KUGA4bRwKelt4rSwSQ4Z.LRhWYdPYwHA41n3YJqn1U5s14jMaCVW',
    'Quản trị viên',
    'admin@local.test',
    'ADMIN',
    TRUE
);

-- =========================================================
-- Bài tập 03: Product seed
-- Có thể chạy lại mà không tạo trùng Product mẫu
-- =========================================================

INSERT INTO products (
    product_name,
    price,
    quantity,
    description,
    cate_id,
    created_at,
    updated_at
)
SELECT
    sample.product_name,
    sample.price,
    sample.quantity,
    sample.description,
    c.cate_id,
    sample.created_at,
    sample.created_at
FROM (
    SELECT
        'BT03 - iPhone 15' AS product_name,
        19990000.00 AS price,
        10 AS quantity,
        'Điện thoại dùng kiểm thử Bài 03.' AS description,
        'Điện thoại' AS category_name,
        '2026-09-01 08:00:00' AS created_at

    UNION ALL SELECT
        'BT03 - Samsung Galaxy S24',
        18490000.00,
        12,
        'Điện thoại Android dùng kiểm thử.',
        'Điện thoại',
        '2026-09-01 09:00:00'

    UNION ALL SELECT
        'BT03 - Xiaomi 14',
        16990000.00,
        8,
        'Điện thoại Xiaomi dùng kiểm thử.',
        'Điện thoại',
        '2026-09-01 10:00:00'

    UNION ALL SELECT
        'BT03 - OPPO Reno12',
        10990000.00,
        15,
        'Điện thoại OPPO dùng kiểm thử.',
        'Điện thoại',
        '2026-09-01 11:00:00'

    UNION ALL SELECT
        'BT03 - Vivo V30',
        9990000.00,
        11,
        'Điện thoại Vivo dùng kiểm thử.',
        'Điện thoại',
        '2026-09-01 12:00:00'

    UNION ALL SELECT
        'BT03 - MacBook Air M3',
        27990000.00,
        5,
        'Máy tính xách tay Apple dùng kiểm thử.',
        'Máy tính xách tay',
        '2026-09-01 13:00:00'

    UNION ALL SELECT
        'BT03 - Dell Inspiron 14',
        18990000.00,
        7,
        'Máy tính xách tay Dell dùng kiểm thử.',
        'Máy tính xách tay',
        '2026-09-01 14:00:00'

    UNION ALL SELECT
        'BT03 - ASUS Vivobook 15',
        17490000.00,
        9,
        'Máy tính xách tay ASUS dùng kiểm thử.',
        'Máy tính xách tay',
        '2026-09-01 15:00:00'

    UNION ALL SELECT
        'BT03 - Lenovo IdeaPad Slim 5',
        18490000.00,
        6,
        'Máy tính xách tay Lenovo dùng kiểm thử.',
        'Máy tính xách tay',
        '2026-09-01 16:00:00'

    UNION ALL SELECT
        'BT03 - Tai nghe Bluetooth',
        1290000.00,
        20,
        'Phụ kiện tai nghe dùng kiểm thử.',
        'Phụ kiện',
        '2026-09-02 08:00:00'

    UNION ALL SELECT
        'BT03 - Chuột không dây',
        490000.00,
        25,
        'Phụ kiện chuột dùng kiểm thử.',
        'Phụ kiện',
        '2026-09-02 12:00:00'

    UNION ALL SELECT
        'BT03 - Bàn phím cơ',
        1490000.00,
        18,
        'Phụ kiện bàn phím dùng kiểm thử.',
        'Phụ kiện',
        '2026-09-02 16:00:00'

    UNION ALL SELECT
        'BT03 - Sạc nhanh USB-C',
        690000.00,
        30,
        'Phụ kiện sạc dùng kiểm thử.',
        'Phụ kiện',
        '2026-09-03 08:00:00'
) AS sample
INNER JOIN categories AS c
    ON c.cate_name = sample.category_name
LEFT JOIN products AS existing_product
    ON existing_product.product_name = sample.product_name
WHERE existing_product.product_id IS NULL;