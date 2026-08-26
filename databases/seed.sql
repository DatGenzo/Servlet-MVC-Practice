USE servlet_mvc_practice;

INSERT IGNORE INTO categories (cate_name, icons)
VALUES
    ('Điện thoại', NULL),
    ('Máy tính xách tay', NULL),
    ('Phụ kiện', NULL);

INSERT INTO users (
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