-- Project: Servlet MVC Practice
-- Upgrade: Requirement after Bài tập 03
-- Purpose: Add User profile fields used by JPA profile update

USE servlet_mvc_practice;

SET @phone_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'phone'
);

SET @phone_ddl = IF(
    @phone_column_exists = 0,
    'ALTER TABLE users ADD COLUMN phone VARCHAR(20) NULL AFTER email',
    'SELECT ''users.phone already exists'' AS migration_status'
);

PREPARE phone_statement FROM @phone_ddl;
EXECUTE phone_statement;
DEALLOCATE PREPARE phone_statement;

SET @images_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'images'
);

SET @images_ddl = IF(
    @images_column_exists = 0,
    'ALTER TABLE users ADD COLUMN images VARCHAR(255) NULL AFTER phone',
    'SELECT ''users.images already exists'' AS migration_status'
);

PREPARE images_statement FROM @images_ddl;
EXECUTE images_statement;
DEALLOCATE PREPARE images_statement;
