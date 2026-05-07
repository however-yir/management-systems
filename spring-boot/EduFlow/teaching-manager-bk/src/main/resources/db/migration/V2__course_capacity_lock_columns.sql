-- Add capacity and optimistic lock columns for enrollment concurrency control
-- MySQL 8.x does not support `ADD COLUMN IF NOT EXISTS`, so we guard with metadata checks.

SET @stmt = IF(
  EXISTS(
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'course'
      AND COLUMN_NAME = 'max_students'
  ),
  'SELECT 1',
  'ALTER TABLE course ADD COLUMN max_students INT DEFAULT 60'
);
PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @stmt = IF(
  EXISTS(
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'course'
      AND COLUMN_NAME = 'current_students'
  ),
  'SELECT 1',
  'ALTER TABLE course ADD COLUMN current_students INT DEFAULT 0'
);
PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @stmt = IF(
  EXISTS(
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'course'
      AND COLUMN_NAME = 'version'
  ),
  'SELECT 1',
  'ALTER TABLE course ADD COLUMN version INT DEFAULT 0'
);
PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE course c
SET c.current_students = (
  SELECT COUNT(*)
  FROM courses_students cs
  WHERE cs.course_id = c.course_id
)
WHERE c.current_students IS NULL OR c.current_students = 0;
