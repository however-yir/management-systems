-- ============================================
-- EduFlow RBAC 升级版数据库脚本（架构演进方案）
-- 说明：该脚本为 2D.3 架构升级参考，不直接替换当前运行库。
-- ============================================

CREATE DATABASE IF NOT EXISTS teaching_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE teaching_manager;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt加密',
    `real_name` VARCHAR(50) NOT NULL,
    `role` VARCHAR(20) NOT NULL COMMENT 'ROLE_ADMIN/ROLE_TEACHER/ROLE_STUDENT',
    `gender` VARCHAR(10),
    `phone` VARCHAR(20),
    `email` VARCHAR(100),
    `department_id` INT,
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_role` (`role`),
    INDEX `idx_department` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS `department_v2`;
CREATE TABLE `department_v2` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `code` VARCHAR(20) UNIQUE,
    `description` VARCHAR(500),
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='院系表';

DROP TABLE IF EXISTS `course_v2`;
CREATE TABLE `course_v2` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `course_code` VARCHAR(50) NOT NULL UNIQUE,
    `course_name` VARCHAR(100) NOT NULL,
    `teacher_id` INT NOT NULL,
    `credits` DECIMAL(3,1) DEFAULT 3.0,
    `max_students` INT DEFAULT 50,
    `current_students` INT DEFAULT 0,
    `semester` VARCHAR(20) NOT NULL,
    `status` VARCHAR(20) DEFAULT 'DRAFT',
    `description` TEXT,
    `version` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_semester` (`semester`),
    INDEX `idx_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

DROP TABLE IF EXISTS `enrollment`;
CREATE TABLE `enrollment` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` INT NOT NULL,
    `course_id` INT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'SELECTED',
    `enroll_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `withdraw_time` DATETIME,
    UNIQUE KEY `uk_student_course` (`student_id`, `course_id`),
    INDEX `idx_student` (`student_id`),
    INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课表';

DROP TABLE IF EXISTS `course_application_v2`;
CREATE TABLE `course_application_v2` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `course_id` INT NOT NULL,
    `applicant_id` INT NOT NULL,
    `application_type` VARCHAR(20),
    `reason` TEXT,
    `current_data` TEXT,
    `proposed_data` TEXT,
    `status` VARCHAR(20) DEFAULT 'PENDING',
    `handler_id` INT,
    `handler_remark` TEXT,
    `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `handle_time` DATETIME,
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程申请表';

INSERT INTO `user` (`username`, `password`, `real_name`, `role`) VALUES
('admin', '\$2a\$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.yCPO', '系统管理员', 'ROLE_ADMIN');
