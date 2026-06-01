-- ============================================
-- Student Management System - Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS student_management;
USE student_management;

-- Students table
CREATE TABLE IF NOT EXISTS students (
    student_id   INT PRIMARY KEY AUTO_INCREMENT,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    phone        VARCHAR(20),
    department   VARCHAR(50),
    enrolled_on  DATE NOT NULL
);

-- Grades table
CREATE TABLE IF NOT EXISTS grades (
    grade_id     INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT NOT NULL,
    subject      VARCHAR(100) NOT NULL,
    marks        DECIMAL(5,2) NOT NULL,
    semester     VARCHAR(20) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Sample data
INSERT INTO students (full_name, email, phone, department, enrolled_on) VALUES
('Aarav Sharma',    'aarav@example.com',    '9800000001', 'Computer Science', '2023-01-15'),
('Priya Thapa',     'priya@example.com',    '9800000002', 'Information Tech',  '2023-01-15'),
('Bikash Karki',    'bikash@example.com',   '9800000003', 'Computer Science', '2023-03-10'),
('Sita Rai',        'sita@example.com',     '9800000004', 'Electronics',      '2022-09-01'),
('Rohan Basnet',    'rohan@example.com',    '9800000005', 'Civil Eng',        '2022-09-01');

INSERT INTO grades (student_id, subject, marks, semester) VALUES
(1, 'Mathematics',     88.5, 'Semester 1'),
(1, 'Java Programming',92.0, 'Semester 1'),
(1, 'Database Systems',85.0, 'Semester 1'),
(2, 'Mathematics',     76.0, 'Semester 1'),
(2, 'Java Programming',80.5, 'Semester 1'),
(2, 'Database Systems',78.0, 'Semester 1'),
(3, 'Mathematics',     91.0, 'Semester 1'),
(3, 'Java Programming',95.5, 'Semester 1'),
(4, 'Circuit Theory',  70.0, 'Semester 1'),
(4, 'Mathematics',     65.5, 'Semester 1'),
(5, 'Mechanics',       82.0, 'Semester 1'),
(5, 'Mathematics',     79.0, 'Semester 1');
