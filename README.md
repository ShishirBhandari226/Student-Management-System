# 🎓 Student Management System

A desktop-based **Student Management System** built with Java Swing and MySQL, designed to streamline student registration, grade tracking, and academic report generation.

---

## 📌 Project Overview

This is an academic project that demonstrates the integration of a Java GUI frontend with a relational MySQL database backend via JDBC. The application provides an intuitive interface for managing student data in an educational setting.

---

## ✨ Features

- **Student Registration** — Add, update, and delete student profiles with personal and academic details
- **Grade Tracking** — Record and manage grades across subjects and semesters
- **Report Generation** — Generate academic summaries and transcripts for individual students
- **Search & Filter** — Look up students by name, ID, or department

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java (JDK 8+) |
| GUI Framework | Java Swing |
| Database | MySQL |
| DB Connectivity | JDBC (Java Database Connectivity) |
| IDE | IntelliJ IDEA / Eclipse |

---

## 🗂️ Project Structure

```
StudentManagementSystem/
├── src/
│   ├── main/
│   │   ├── MainApp.java          # Entry point
│   │   ├── gui/
│   │   │   ├── Dashboard.java    # Main dashboard window
│   │   │   ├── StudentForm.java  # Registration form
│   │   │   ├── GradePanel.java   # Grade tracking panel
│   │   │   └── ReportPanel.java  # Report generation panel
│   │   ├── dao/
│   │   │   ├── StudentDAO.java   # Student DB operations
│   │   │   └── GradeDAO.java     # Grade DB operations
│   │   ├── model/
│   │   │   ├── Student.java      # Student entity
│   │   │   └── Grade.java        # Grade entity
│   │   └── db/
│   │       └── DBConnection.java # JDBC connection manager
├── database/
│   └── schema.sql                # MySQL schema & seed data
├── lib/
│   └── mysql-connector-j.jar     # MySQL JDBC driver
└── README.md
```

---

## ⚙️ Setup & Installation

### Prerequisites

- Java JDK 8 or higher
- MySQL Server 5.7+
- MySQL Connector/J (JDBC Driver)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/student-management-system.git
   cd student-management-system
   ```

2. **Set up the database**
   ```bash
   mysql -u root -p
   ```
   ```sql
   CREATE DATABASE student_management;
   USE student_management;
   SOURCE database/schema.sql;
   ```

3. **Configure DB connection**

   Open `src/main/db/DBConnection.java` and update your credentials:
   ```java
   private static final String URL  = "jdbc:mysql://localhost:3306/student_management";
   private static final String USER = "your_mysql_username";
   private static final String PASS = "your_mysql_password";
   ```

4. **Add the JDBC driver**

   Place `mysql-connector-j.jar` inside the `lib/` folder and add it to your project's classpath.

5. **Compile and run**
   ```bash
   javac -cp lib/mysql-connector-j.jar -d out src/main/**/*.java
   java  -cp out:lib/mysql-connector-j.jar main.MainApp
   ```
   On Windows, replace `:` with `;` in the classpath.

---

## 🗃️ Database Schema

```sql
CREATE TABLE students (
    student_id   INT PRIMARY KEY AUTO_INCREMENT,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(100) UNIQUE,
    department   VARCHAR(50),
    enrolled_on  DATE
);

CREATE TABLE grades (
    grade_id     INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT,
    subject      VARCHAR(100),
    marks        DECIMAL(5,2),
    semester     VARCHAR(20),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);
```

---

## 📸 Screenshots

> _Add screenshots of your application here_

| Dashboard | Student Registration | Grade Tracking |
|-----------|---------------------|----------------|
| _(screenshot)_ | _(screenshot)_ | _(screenshot)_ |

---

## 🚀 Future Improvements

- [ ] Add login/authentication for admin and students
- [ ] Export reports as PDF
- [ ] REST API integration for web access
- [ ] Dark mode for the GUI

---

## 👤 Author

**Your Name**
- GitHub: [@your-username](https://github.com/your-username)
- LinkedIn: [your-linkedin](https://linkedin.com/in/your-linkedin)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
