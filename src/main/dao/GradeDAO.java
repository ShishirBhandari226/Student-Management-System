package dao;

import db.DBConnection;
import model.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    // Add a new grade
    public boolean addGrade(Grade g) throws SQLException {
        String sql = "INSERT INTO grades (student_id, subject, marks, semester) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, g.getStudentId());
            ps.setString(2, g.getSubject());
            ps.setDouble(3, g.getMarks());
            ps.setString(4, g.getSemester());
            return ps.executeUpdate() > 0;
        }
    }

    // Update a grade
    public boolean updateGrade(Grade g) throws SQLException {
        String sql = "UPDATE grades SET subject=?, marks=?, semester=? WHERE grade_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, g.getSubject());
            ps.setDouble(2, g.getMarks());
            ps.setString(3, g.getSemester());
            ps.setInt(4, g.getGradeId());
            return ps.executeUpdate() > 0;
        }
    }

    // Delete a grade
    public boolean deleteGrade(int gradeId) throws SQLException {
        String sql = "DELETE FROM grades WHERE grade_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, gradeId);
            return ps.executeUpdate() > 0;
        }
    }

    // Get all grades (with student name via JOIN)
    public List<Grade> getAllGrades() throws SQLException {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT g.*, s.full_name FROM grades g " +
                     "JOIN students s ON g.student_id = s.student_id ORDER BY s.full_name, g.semester";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // Get grades for a specific student
    public List<Grade> getGradesByStudent(int studentId) throws SQLException {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT g.*, s.full_name FROM grades g " +
                     "JOIN students s ON g.student_id = s.student_id " +
                     "WHERE g.student_id=? ORDER BY g.semester";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Get average marks per student (for report)
    public List<Object[]> getStudentAverages() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.full_name, s.department, " +
                     "ROUND(AVG(g.marks), 2) as avg_marks, COUNT(g.grade_id) as total_subjects " +
                     "FROM students s LEFT JOIN grades g ON s.student_id = g.student_id " +
                     "GROUP BY s.student_id, s.full_name, s.department ORDER BY avg_marks DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("full_name"),
                    rs.getString("department"),
                    rs.getDouble("avg_marks"),
                    rs.getInt("total_subjects")
                });
            }
        }
        return list;
    }

    private Grade mapRow(ResultSet rs) throws SQLException {
        return new Grade(
            rs.getInt("grade_id"),
            rs.getInt("student_id"),
            rs.getString("full_name"),
            rs.getString("subject"),
            rs.getDouble("marks"),
            rs.getString("semester")
        );
    }
}
