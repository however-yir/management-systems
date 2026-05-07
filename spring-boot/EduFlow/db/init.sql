-- EduFlow local bootstrap schema + demo data
-- Target: MySQL 8.x

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS teaching_manager
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE teaching_manager;

DROP TABLE IF EXISTS courses_students;
DROP TABLE IF EXISTS course_application;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS course_switch;
DROP TABLE IF EXISTS course_status;
DROP TABLE IF EXISTS place;
DROP TABLE IF EXISTS teacher;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS administrator;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS operation;
DROP TABLE IF EXISTS course_examination;

CREATE TABLE department (
  department_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE administrator (
  administrator_id INT PRIMARY KEY AUTO_INCREMENT,
  account VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  name VARCHAR(64) NOT NULL,
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE teacher (
  teacher_id INT PRIMARY KEY AUTO_INCREMENT,
  teacher_number VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(64) NOT NULL,
  department_id INT NOT NULL,
  password VARCHAR(128) NOT NULL DEFAULT '123456',
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_teacher_department FOREIGN KEY (department_id) REFERENCES department(department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE student (
  student_id INT PRIMARY KEY AUTO_INCREMENT,
  student_number VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(64) NOT NULL,
  student_class VARCHAR(64) NOT NULL,
  password VARCHAR(128) NOT NULL DEFAULT '123456',
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE place (
  place_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE course_status (
  course_status_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE course_switch (
  course_switch_id INT PRIMARY KEY,
  status CHAR(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE course (
  course_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  teacher_id INT,
  credit VARCHAR(16),
  hour VARCHAR(16),
  time VARCHAR(64),
  place_id INT,
  description VARCHAR(512),
  course_status_id INT,
  max_students INT DEFAULT 60,
  current_students INT DEFAULT 0,
  version INT DEFAULT 0,
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(teacher_id),
  CONSTRAINT fk_course_place FOREIGN KEY (place_id) REFERENCES place(place_id),
  CONSTRAINT fk_course_status FOREIGN KEY (course_status_id) REFERENCES course_status(course_status_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE courses_students (
  courses_students_id INT PRIMARY KEY AUTO_INCREMENT,
  course_id INT NOT NULL,
  student_id INT NOT NULL,
  score FLOAT NULL,
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_course_student (course_id, student_id),
  CONSTRAINT fk_cs_course FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
  CONSTRAINT fk_cs_student FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE operation (
  operation_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE course_examination (
  course_examination_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE course_application (
  course_application_id INT PRIMARY KEY AUTO_INCREMENT,
  teacher_id INT NOT NULL,
  course_id INT NULL,
  course_name VARCHAR(128) NOT NULL,
  course_credit VARCHAR(16),
  course_hour VARCHAR(16),
  course_time VARCHAR(64),
  course_place_id INT,
  course_description VARCHAR(512),
  operation_id INT NOT NULL,
  course_examination_id INT NOT NULL,
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_ca_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(teacher_id),
  CONSTRAINT fk_ca_course FOREIGN KEY (course_id) REFERENCES course(course_id),
  CONSTRAINT fk_ca_place FOREIGN KEY (course_place_id) REFERENCES place(place_id),
  CONSTRAINT fk_ca_operation FOREIGN KEY (operation_id) REFERENCES operation(operation_id),
  CONSTRAINT fk_ca_examination FOREIGN KEY (course_examination_id) REFERENCES course_examination(course_examination_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO department (name) VALUES
  ('计算机学院'),
  ('数学学院'),
  ('外国语学院'),
  ('经济管理学院');

INSERT INTO administrator (account, password, name) VALUES
  ('admin', '123456', '系统管理员');

INSERT INTO teacher (teacher_number, name, department_id, password) VALUES
  ('T1001', '张老师', 1, '123456'),
  ('T1002', '李老师', 2, '123456'),
  ('T1003', '王老师', 4, '123456');

INSERT INTO student (student_number, name, student_class, password) VALUES
  ('S2025001', '赵同学', '计科 2401', '123456'),
  ('S2025002', '钱同学', '计科 2402', '123456'),
  ('S2025003', '孙同学', '经管 2401', '123456');

INSERT INTO place (name) VALUES
  ('A101'),
  ('B204'),
  ('线上课堂');

INSERT INTO course_status (name) VALUES
  ('待选'),
  ('可选'),
  ('授课中'),
  ('结束'),
  ('等待课程安排');

INSERT INTO course_switch (course_switch_id, status) VALUES
  (1, '1');

INSERT INTO operation (name) VALUES
  ('新增'),
  ('修改'),
  ('删除');

INSERT INTO course_examination (name) VALUES
  ('待审批'),
  ('通过'),
  ('未通过');

INSERT INTO course (name, teacher_id, credit, hour, time, place_id, description, course_status_id) VALUES
  ('Java 程序设计', 1, '3', '48', '周一 1-2 节', 1, '面向对象与工程实践', 1),
  ('高等数学', 2, '4', '64', '周三 3-4 节', 2, '微积分与线性代数基础', 2),
  ('管理学导论', 3, '2', '32', '周五 1-2 节', 3, '组织与管理核心理论', 1);

INSERT INTO courses_students (course_id, student_id, score) VALUES
  (1, 1, 88),
  (2, 1, NULL),
  (2, 2, NULL);

UPDATE course c
SET c.current_students = (
  SELECT COUNT(*)
  FROM courses_students cs
  WHERE cs.course_id = c.course_id
);

INSERT INTO course_application
(teacher_id, course_name, course_credit, course_hour, course_time, course_place_id, course_description, operation_id, course_examination_id)
VALUES
  (1, 'Web 前端工程', '3', '48', '周四 1-2 节', 1, '涵盖 Vue 与工程化实践', 1, 1),
  (2, '概率统计', '3', '48', '周二 5-6 节', 2, '概率分布与统计推断', 1, 2);

-- ------------------------------------------
-- 选课冲突处理演示数据（重点）
-- ------------------------------------------
-- 说明：
-- 1) uk_course_student：防止同一学生重复选同一课程
-- 2) max_students/current_students：配合服务层容量校验
-- 3) version：支持乐观锁 CAS 更新
INSERT INTO course (name, teacher_id, credit, hour, time, place_id, description, course_status_id, max_students, current_students, version)
VALUES ('并发系统专题（冲突演示）', 1, '2', '32', '周二 1-2 节', 1, '用于演示重复选课与人数上限冲突', 2, 1, 0, 0);

SET @conflict_course_id = LAST_INSERT_ID();
INSERT INTO courses_students (course_id, student_id, score) VALUES (@conflict_course_id, 3, NULL);
UPDATE course SET current_students = 1 WHERE course_id = @conflict_course_id;

-- ------------------------------------------
-- 演示 A：重复选课冲突（唯一约束）
-- ------------------------------------------
-- INSERT INTO courses_students (course_id, student_id, score)
-- VALUES (@conflict_course_id, 3, NULL);
-- 预期：Duplicate entry ... for key 'uk_course_student'

-- ------------------------------------------
-- 演示 B：人数上限冲突（乐观锁 CAS）
-- ------------------------------------------
-- SELECT course_id, max_students, current_students, version
-- FROM course WHERE course_id = @conflict_course_id;
-- UPDATE course
-- SET current_students = current_students + 1, version = version + 1
-- WHERE course_id = @conflict_course_id
--   AND version = 0
--   AND current_students < max_students;
-- 预期：受影响行数为 0（课程已满）

-- ------------------------------------------
-- 演示 C：并发悲观锁（双会话）
-- ------------------------------------------
-- 会话A:
-- START TRANSACTION;
-- SELECT * FROM course WHERE course_id = @conflict_course_id FOR UPDATE;
--
-- 会话B:
-- START TRANSACTION;
-- SELECT * FROM course WHERE course_id = @conflict_course_id FOR UPDATE;
-- -- 预期：会话B阻塞，直到会话A COMMIT/ROLLBACK
