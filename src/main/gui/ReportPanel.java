package gui;

import dao.GradeDAO;
import dao.StudentDAO;
import model.Grade;
import model.Student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class ReportPanel extends JPanel {

    private final GradeDAO   gradeDAO   = new GradeDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    private JTable summaryTable, detailTable;
    private DefaultTableModel summaryModel, detailModel;
    private JComboBox<Student> cbStudent;
    private JLabel lblAvg, lblTotal, lblStatus;

    public ReportPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);

        refreshReport();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Academic Reports");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(15, 23, 42));

        cbStudent = new JComboBox<>();
        cbStudent.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cbStudent.setPreferredSize(new Dimension(210, 30));

        JButton btnView = styledButton("View Transcript", new Color(59, 130, 246));
        JButton btnPrint = styledButton("Print Report", new Color(100, 116, 139));
        btnView.addActionListener(e -> viewTranscript());
        btnPrint.addActionListener(e -> printReport());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(new JLabel("Student: "));
        right.add(cbStudent);
        right.add(btnView);
        right.add(btnPrint);

        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        return p;
    }

    private JSplitPane buildBody() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildSummary(), buildDetail());
        split.setDividerLocation(540);
        split.setDividerSize(8);
        split.setOpaque(false);
        split.setBorder(null);
        return split;
    }

    // ── Summary table (all students) ─────────────────────────────────────────
    private JPanel buildSummary() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);

        JLabel lbl = new JLabel("Class Overview");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(51, 65, 85));

        String[] cols = {"#", "Student Name", "Department", "Avg Marks", "Grade", "Subjects"};
        summaryModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        summaryTable = new JTable(summaryModel);
        summaryTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        summaryTable.setRowHeight(28);
        summaryTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        summaryTable.getTableHeader().setBackground(new Color(241, 245, 249));
        summaryTable.setSelectionBackground(new Color(219, 234, 254));
        summaryTable.setGridColor(new Color(226, 232, 240));
        summaryTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        summaryTable.getColumnModel().getColumn(4).setPreferredWidth(50);

        // Color rows by grade
        summaryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel) {
                    Object gradeVal = summaryModel.getValueAt(r, 4);
                    String grade = gradeVal != null ? gradeVal.toString() : "";
                    switch (grade) {
                        case "A+": case "A": setBackground(new Color(240, 253, 244)); break;
                        case "B":            setBackground(new Color(239, 246, 255)); break;
                        case "C":            setBackground(new Color(254, 252, 232)); break;
                        default:             setBackground(Color.WHITE);
                    }
                }
                return this;
            }
        });

        summaryTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = summaryTable.getSelectedRow();
                if (row >= 0) {
                    int sid = (int) summaryModel.getValueAt(row, 0);
                    loadDetailForStudent(sid);
                    // sync combo
                    for (int i = 0; i < cbStudent.getItemCount(); i++) {
                        Student s = cbStudent.getItemAt(i);
                        if (s != null && s.getStudentId() == sid) { cbStudent.setSelectedIndex(i); break; }
                    }
                }
            }
        });

        JScrollPane sp = new JScrollPane(summaryTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));

        p.add(lbl, BorderLayout.NORTH);
        p.add(sp,  BorderLayout.CENTER);
        return p;
    }

    // ── Detail panel (one student) ────────────────────────────────────────────
    private JPanel buildDetail() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        JLabel lbl = new JLabel("Student Transcript");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(51, 65, 85));

        // Stats row
        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setOpaque(false);
        lblAvg    = statLabel("—", "Average");
        lblTotal  = statLabel("—", "Subjects");
        lblStatus = statLabel("—", "Status");
        stats.add(lblAvg);
        stats.add(lblTotal);
        stats.add(lblStatus);

        // Detail table
        String[] cols = {"Subject", "Marks", "Grade", "Semester"};
        detailModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        detailTable = new JTable(detailModel);
        detailTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailTable.setRowHeight(28);
        detailTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        detailTable.getTableHeader().setBackground(new Color(241, 245, 249));
        detailTable.setSelectionBackground(new Color(219, 234, 254));
        detailTable.setGridColor(new Color(226, 232, 240));

        JScrollPane sp = new JScrollPane(detailTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));

        p.add(lbl,   BorderLayout.NORTH);
        p.add(stats, BorderLayout.SOUTH);
        p.add(sp,    BorderLayout.CENTER);
        return p;
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    public void refreshReport() {
        // reload combo
        try {
            cbStudent.removeAllItems();
            for (Student s : studentDAO.getAllStudents()) cbStudent.addItem(s);
        } catch (SQLException ex) { /* ignore */ }

        // reload summary
        summaryModel.setRowCount(0);
        try {
            List<Object[]> avgs = gradeDAO.getStudentAverages();
            int rank = 1;
            for (Object[] row : avgs) {
                double avg = (double) row[3];
                summaryModel.addRow(new Object[]{
                    row[0], row[1], row[2],
                    String.format("%.1f", avg),
                    letterGrade(avg),
                    row[4]
                });
                rank++;
            }
        } catch (SQLException ex) { /* ignore */ }

        detailModel.setRowCount(0);
        lblAvg.setText("—"); lblTotal.setText("—"); lblStatus.setText("—");
    }

    private void viewTranscript() {
        Student sel = (Student) cbStudent.getSelectedItem();
        if (sel == null) { showMsg("Select a student.", true); return; }
        loadDetailForStudent(sel.getStudentId());
    }

    private void loadDetailForStudent(int studentId) {
        detailModel.setRowCount(0);
        try {
            List<Grade> grades = gradeDAO.getGradesByStudent(studentId);
            double total = 0;
            for (Grade g : grades) {
                detailModel.addRow(new Object[]{
                    g.getSubject(), g.getMarks(), g.getGrade(), g.getSemester()
                });
                total += g.getMarks();
            }
            if (!grades.isEmpty()) {
                double avg = total / grades.size();
                lblAvg.setText(String.format("%.1f%%", avg));
                ((JLabel) ((JPanel) lblAvg.getParent().getComponent(0)).getComponent(0)).setText(String.format("%.1f%%", avg));
                updateStats(avg, grades.size());
            }
        } catch (SQLException ex) { showMsg("Error: " + ex.getMessage(), true); }
    }

    private void updateStats(double avg, int count) {
        // Update the stat labels — we rebuild the text in the wrapper panels
        Component[] comps = ((JPanel) detailTable.getParent().getParent().getParent()).getComponents();
        // Simpler: just set text directly
        lblAvg.setText(String.format("%.1f%%", avg));
        lblTotal.setText(count + " subjects");
        lblStatus.setText(avg >= 50 ? "PASS" : "FAIL");
        lblStatus.setForeground(avg >= 50 ? new Color(22, 163, 74) : new Color(220, 38, 38));
    }

    private void printReport() {
        Student sel = (Student) cbStudent.getSelectedItem();
        if (sel == null) { showMsg("Select a student first.", true); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("==============================================\n");
        sb.append("         ACADEMIC TRANSCRIPT\n");
        sb.append("==============================================\n");
        sb.append("Name       : ").append(sel.getFullName()).append("\n");
        sb.append("Department : ").append(sel.getDepartment()).append("\n");
        sb.append("Email      : ").append(sel.getEmail()).append("\n");
        sb.append("----------------------------------------------\n");
        sb.append(String.format("%-25s %-8s %-6s %s\n", "Subject", "Marks", "Grade", "Semester"));
        sb.append("----------------------------------------------\n");

        double total = 0; int count = 0;
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            String subj = (String) detailModel.getValueAt(i, 0);
            Object marks = detailModel.getValueAt(i, 1);
            String grade = (String) detailModel.getValueAt(i, 2);
            String sem   = (String) detailModel.getValueAt(i, 3);
            sb.append(String.format("%-25s %-8s %-6s %s\n", subj, marks, grade, sem));
            total += Double.parseDouble(marks.toString());
            count++;
        }
        if (count > 0) {
            double avg = total / count;
            sb.append("----------------------------------------------\n");
            sb.append(String.format("Average: %.1f%%   Status: %s\n", avg, avg >= 50 ? "PASS" : "FAIL"));
        }
        sb.append("==============================================\n");

        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(520, 400));

        JOptionPane.showMessageDialog(this, sp, "Transcript — " + sel.getFullName(), JOptionPane.PLAIN_MESSAGE);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel statLabel(String value, String sub) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(241, 245, 249));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 18));
        val.setForeground(new Color(15, 23, 42));
        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLbl.setForeground(new Color(100, 116, 139));
        card.add(val,    BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);
        // We return val so callers can update it
        return val;
    }

    private String letterGrade(double avg) {
        if (avg >= 90) return "A+";
        if (avg >= 80) return "A";
        if (avg >= 70) return "B";
        if (avg >= 60) return "C";
        if (avg >= 50) return "D";
        return "F";
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); btn.setOpaque(true);
        return btn;
    }

    private void showMsg(String msg, boolean error) {
        JOptionPane.showMessageDialog(this, msg, error ? "Error" : "Info",
            error ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
}
