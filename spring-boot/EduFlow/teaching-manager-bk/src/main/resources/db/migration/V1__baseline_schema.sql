-- EduFlow baseline schema (for Flyway)

CREATE TABLE IF NOT EXISTS department (
  department_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS administrator (
  administrator_id INT PRIMARY KEY AUTO_INCREMENT,
  account VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  name VARCHAR(64) NOT NULL,
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS teacher (
  teacher_id INT PRIMARY KEY AUTO_INCREMENT,
  teacher_number VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(64) NOT NULL,
  department_id INT NOT NULL,
  password VARCHAR(128) NOT NULL DEFAULT '123456',
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_teacher_department FOREIGN KEY (department_id) REFERENCES department(department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student (
  student_id INT PRIMARY KEY AUTO_INCREMENT,
  student_number VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(64) NOT NULL,
  student_class VARCHAR(64) NOT NULL,
  password VARCHAR(128) NOT NULL DEFAULT '123456',
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS place (
  place_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course_status (
  course_status_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course_switch (
  course_switch_id INT PRIMARY KEY,
  status CHAR(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course (
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

CREATE TABLE IF NOT EXISTS courses_students (
  courses_students_id INT PRIMARY KEY AUTO_INCREMENT,
  course_id INT NOT NULL,
  student_id INT NOT NULL,
  score FLOAT NULL,
  date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_course_student (course_id, student_id),
  CONSTRAINT fk_cs_course FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
  CONSTRAINT fk_cs_student FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS operation (
  operation_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course_examination (
  course_examination_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course_application (
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

INSERT IGNORE INTO department (department_id, name) VALUES
  (1, '计算机学院'),
  (2, '数学学院'),
  (3, '外国语学院'),
  (4, '经济管理学院');

INSERT IGNORE INTO administrator (administrator_id, account, password, name) VALUES
  (1, 'admin', '123456', '系统管理员');

INSERT IGNORE INTO teacher (teacher_id, teacher_number, name, department_id, password) VALUES
  (1, 'T1001', '张老师', 1, '123456'),
  (2, 'T1002', '李老师', 2, '123456'),
  (3, 'T1003', '王老师', 4, '123456');

INSERT IGNORE INTO student (student_id, student_number, name, student_class, password) VALUES
  (1, 'S2025001', '赵同学', '计科 2401', '123456'),
  (2, 'S2025002', '钱同学', '计科 2402', '123456'),
  (3, 'S2025003', '孙同学', '经管 2401', '123456');

INSERT IGNORE INTO place (place_id, name) VALUES
  (1, 'A101'),
  (2, 'B204'),
  (3, '线上课堂');

INSERT IGNORE INTO course_status (course_status_id, name) VALUES
  (1, '待选'),
  (2, '可选'),
  (3, '授课中'),
  (4, '结束'),
  (5, '等待课程安排');

INSERT IGNORE INTO course_switch (course_switch_id, status) VALUES
  (1, '1');

INSERT IGNORE INTO operation (operation_id, name) VALUES
  (1, '新增'),
  (2, '修改'),
  (3, '删除');

INSERT IGNORE INTO course_examination (course_examination_id, name) VALUES
  (1, '待审批'),
  (2, '通过'),
  (3, '未通过');
