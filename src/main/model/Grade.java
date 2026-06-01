package model;

public class Grade {

    private int    gradeId;
    private int    studentId;
    private String studentName;
    private String subject;
    private double marks;
    private String semester;

    public Grade() {}

    public Grade(int gradeId, int studentId, String studentName, String subject, double marks, String semester) {
        this.gradeId     = gradeId;
        this.studentId   = studentId;
        this.studentName = studentName;
        this.subject     = subject;
        this.marks       = marks;
        this.semester    = semester;
    }

    // Getters
    public int    getGradeId()     { return gradeId; }
    public int    getStudentId()   { return studentId; }
    public String getStudentName() { return studentName; }
    public String getSubject()     { return subject; }
    public double getMarks()       { return marks; }
    public String getSemester()    { return semester; }

    // Setters
    public void setGradeId(int gradeId)        { this.gradeId     = gradeId; }
    public void setStudentId(int studentId)    { this.studentId   = studentId; }
    public void setStudentName(String name)    { this.studentName = name; }
    public void setSubject(String subject)     { this.subject     = subject; }
    public void setMarks(double marks)         { this.marks       = marks; }
    public void setSemester(String semester)   { this.semester    = semester; }

    public String getGrade() {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }
}
