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

public class GradePanel extends JPanel {

    private final GradeDAO   gradeDAO   = new GradeDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JComboBox<Student> cbStudent;
    private JTextField tfSubject, tfMarks, tfSemester;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnFilter;
    private JComboBox<String> cbFilter;

    private int selectedGradeId = -1;

    public GradePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);

        loadStudentsIntoCombo();
        loadTable(-1);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JLabel title = new JLabel("Grade Tracking");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(15, 23, 42));

        // Filter by student
        cbFilter = new JComboBox<>();
        cbFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cbFilter.setPreferredSize(new Dimension(200, 30));
        btnFilter = styledButton("Filter", new Color(59, 130, 246));
        btnFilter.addActionListener(e -> {
            Student sel = (Student) cbFilter.getSelectedItem();
            loadTable(sel != null && cbFilter.getSelectedIndex() > 0 ? sel.getStudentId() : -1);
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(new JLabel("Filter by Student: "));
        right.add(cbFilter);
        right.add(btnFilter);

        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        return p;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.add(buildForm(),  BorderLayout.WEST);
        p.add(buildTable(), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        form.setPreferredSize(new Dimension(280, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 4, 5, 4);
        gc.gridx = 0; gc.weightx = 1;

        cbStudent  = new JComboBox<>();
        cbStudent.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tfSubject  = formField();
        tfMarks    = formField();
        tfSemester = formField(); tfSemester.setText("Semester 1");

        addFormRow(form, gc, 0, "Student *",  cbStudent);
        addFormRow(form, gc, 1, "Subject *",  tfSubject);
        addFormRow(form, gc, 2, "Marks (0–100) *", tfMarks);
        addFormRow(form, gc, 3, "Semester *", tfSemester);

        btnAdd    = styledButton("Add Grade",  new Color(34, 197, 94));
        btnUpdate = styledButton("Update",     new Color(59, 130, 246));
        btnDelete = styledButton("Delete",     new Color(239, 68, 68));
        btnClear  = styledButton("Clear",      new Color(100, 116, 139));
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        btnAdd.addActionListener(e    -> addGrade());
        btnUpdate.addActionListener(e -> updateGrade());
        btnDelete.addActionListener(e -> deleteGrade());
        btnClear.addActionListener(e  -> clearForm());

        gc.gridy = 8; gc.insets = new Insets(14, 4, 4, 4);
        form.add(btnAdd, gc);
        gc.gridy = 9; gc.insets = new Insets(4, 4, 4, 4);
        JPanel row = new JPanel(new GridLayout(1, 3, 6, 0));
        row.setOpaque(false); row.add(btnUpdate); row.add(btnDelete); row.add(btnClear);
        form.add(row, gc);
        return form;
    }

    private JScrollPane buildTable() {
        String[] cols = {"ID", "Student", "Subject", "Marks", "Grade", "Semester"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setGridColor(new Color(226, 232, 240));
        table.getColumnModel().getColumn(0).setPreferredWidth(40);

        // Color-code grade column
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                setHorizontalAlignment(CENTER);
                String grade = val != null ? val.toString() : "";
                if (!sel) {
                    switch (grade) {
                        case "A+": setBackground(new Color(220, 252, 231)); break;
                        case "A":  setBackground(new Color(220, 252, 231)); break;
                        case "B":  setBackground(new Color(219, 234, 254)); break;
                        case "C":  setBackground(new Color(254, 249, 195)); break;
                        default:   setBackground(new Color(254, 226, 226));
                    }
                }
                return this;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onRowSelected(); }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));
        return sp;
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    private void addGrade() {
        if (!validateForm()) return;
        try {
            Grade g = formToGrade(-1);
            if (gradeDAO.addGrade(g)) {
                showMsg("Grade added!", false);
                clearForm(); loadTable(-1);
            }
        } catch (SQLException ex) { showMsg("Error: " + ex.getMessage(), true); }
    }

    private void updateGrade() {
        if (selectedGradeId == -1 || !validateForm()) return;
        try {
            Grade g = formToGrade(selectedGradeId);
            if (gradeDAO.updateGrade(g)) {
                showMsg("Grade updated!", false);
                clearForm(); loadTable(-1);
            }
        } catch (SQLException ex) { showMsg("Error: " + ex.getMessage(), true); }
    }

    private void deleteGrade() {
        if (selectedGradeId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this grade record?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            if (gradeDAO.deleteGrade(selectedGradeId)) {
                showMsg("Grade deleted.", false);
                clearForm(); loadTable(-1);
            }
        } catch (SQLException ex) { showMsg("Error: " + ex.getMessage(), true); }
    }

    private void loadTable(int studentId) {
        tableModel.setRowCount(0);
        try {
            List<Grade> list = (studentId == -1)
                ? gradeDAO.getAllGrades()
                : gradeDAO.getGradesByStudent(studentId);
            for (Grade g : list) {
                tableModel.addRow(new Object[]{
                    g.getGradeId(), g.getStudentName(), g.getSubject(),
                    g.getMarks(), g.getGrade(), g.getSemester()
                });
            }
        } catch (SQLException ex) { showMsg("DB error: " + ex.getMessage(), true); }
    }

    private void loadStudentsIntoCombo() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            cbStudent.removeAllItems();
            cbFilter.removeAllItems();
            cbFilter.addItem(null); // "All students"

            for (Student s : students) {
                cbStudent.addItem(s);
                cbFilter.addItem(s);
            }
        } catch (SQLException ex) { showMsg("Could not load students: " + ex.getMessage(), true); }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedGradeId = (int) tableModel.getValueAt(row, 0);
        tfSubject.setText((String) tableModel.getValueAt(row, 2));
        tfMarks.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        tfSemester.setText((String) tableModel.getValueAt(row, 5));
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnAdd.setEnabled(false);
    }

    private void clearForm() {
        cbStudent.setSelectedIndex(0);
        tfSubject.setText(""); tfMarks.setText(""); tfSemester.setText("Semester 1");
        selectedGradeId = -1;
        btnAdd.setEnabled(true); btnUpdate.setEnabled(false); btnDelete.setEnabled(false);
        table.clearSelection();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private boolean validateForm() {
        if (cbStudent.getSelectedItem() == null) { showMsg("Select a student.", true); return false; }
        if (tfSubject.getText().trim().isEmpty())  { showMsg("Subject is required.", true); return false; }
        try {
            double m = Double.parseDouble(tfMarks.getText().trim());
            if (m < 0 || m > 100) { showMsg("Marks must be between 0 and 100.", true); return false; }
        } catch (NumberFormatException e) { showMsg("Marks must be a number.", true); return false; }
        if (tfSemester.getText().trim().isEmpty()) { showMsg("Semester is required.", true); return false; }
        return true;
    }

    private Grade formToGrade(int id) {
        Grade g = new Grade();
        g.setGradeId(id);
        Student s = (Student) cbStudent.getSelectedItem();
        g.setStudentId(s.getStudentId());
        g.setSubject(tfSubject.getText().trim());
        g.setMarks(Double.parseDouble(tfMarks.getText().trim()));
        g.setSemester(tfSemester.getText().trim());
        return g;
    }

    private void addFormRow(JPanel p, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridy = row * 2;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(71, 85, 105));
        p.add(lbl, gc);
        gc.gridy = row * 2 + 1;
        p.add(field, gc);
    }

    private JTextField formField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(0, 32));
        return tf;
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
        JOptionPane.showMessageDialog(this, msg,
            error ? "Error" : "Success",
            error ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
}
