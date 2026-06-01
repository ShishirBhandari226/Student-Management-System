package model;

import java.time.LocalDate;

public class Student {

    private int studentId;
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private LocalDate enrolledOn;

    public Student() {}

    public Student(int studentId, String fullName, String email, String phone, String department, LocalDate enrolledOn) {
        this.studentId  = studentId;
        this.fullName   = fullName;
        this.email      = email;
        this.phone      = phone;
        this.department = department;
        this.enrolledOn = enrolledOn;
    }

    // Getters
    public int       getStudentId()  { return studentId; }
    public String    getFullName()   { return fullName; }
    public String    getEmail()      { return email; }
    public String    getPhone()      { return phone; }
    public String    getDepartment() { return department; }
    public LocalDate getEnrolledOn() { return enrolledOn; }

    // Setters
    public void setStudentId(int studentId)    { this.studentId  = studentId; }
    public void setFullName(String fullName)   { this.fullName   = fullName; }
    public void setEmail(String email)         { this.email      = email; }
    public void setPhone(String phone)         { this.phone      = phone; }
    public void setDepartment(String dept)     { this.department = dept; }
    public void setEnrolledOn(LocalDate date)  { this.enrolledOn = date; }

    @Override
    public String toString() { return fullName; }
}
