package gui;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StudentPanel extends JPanel {

    private final StudentDAO dao = new StudentDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;

    // Form fields
    private JTextField tfName, tfEmail, tfPhone, tfDept, tfDate;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    private int selectedStudentId = -1;

    public StudentPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);

        loadTable(null);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JLabel title = new JLabel("Student Registration");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(15, 23, 42));

        tfSearch = new JTextField(18);
        tfSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tfSearch.setToolTipText("Search by name or department");
        JButton btnSearch = styledButton("Search", new Color(59, 130, 246));
        btnSearch.addActionListener(e -> loadTable(tfSearch.getText().trim()));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(new JLabel("Search: "));
        right.add(tfSearch);
        right.add(btnSearch);

        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        return p;
    }

    // ── Center: form + table ──────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.add(buildForm(),  BorderLayout.WEST);
        p.add(buildTable(), BorderLayout.CENTER);
        return p;
    }

    // ── Form ──────────────────────────────────────────────────────────────────
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

        tfName  = formField(); tfEmail = formField();
        tfPhone = formField(); tfDept  = formField();
        tfDate  = formField(); tfDate.setText(LocalDate.now().toString());

        addFormRow(form, gc, 0, "Full Name *",    tfName);
        addFormRow(form, gc, 1, "Email *",        tfEmail);
        addFormRow(form, gc, 2, "Phone",          tfPhone);
        addFormRow(form, gc, 3, "Department",     tfDept);
        addFormRow(form, gc, 4, "Enrolled Date (YYYY-MM-DD)", tfDate);

        // Buttons
        btnAdd    = styledButton("Add Student",    new Color(34, 197, 94));
        btnUpdate = styledButton("Update",         new Color(59, 130, 246));
        btnDelete = styledButton("Delete",         new Color(239, 68, 68));
        btnClear  = styledButton("Clear",          new Color(100, 116, 139));
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        btnAdd.addActionListener(e    -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e  -> clearForm());

        gc.gridy = 5; gc.insets = new Insets(14, 4, 4, 4);
        form.add(btnAdd, gc);
        gc.gridy = 6; gc.insets = new Insets(4, 4, 4, 4);
        JPanel row = new JPanel(new GridLayout(1, 3, 6, 0));
        row.setOpaque(false); row.add(btnUpdate); row.add(btnDelete); row.add(btnClear);
        form.add(row, gc);

        return form;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols = {"ID", "Full Name", "Email", "Phone", "Department", "Enrolled"};
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
        table.setShowGrid(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onRowSelected(); }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));
        return sp;
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    private void addStudent() {
        if (!validateForm()) return;
        try {
            Student s = formToStudent(-1);
            if (dao.addStudent(s)) {
                showMsg("Student added successfully!", false);
                clearForm(); loadTable(null);
            }
        } catch (SQLException ex) { showMsg("Error: " + ex.getMessage(), true); }
    }

    private void updateStudent() {
        if (selectedStudentId == -1 || !validateForm()) return;
        try {
            Student s = formToStudent(selectedStudentId);
            if (dao.updateStudent(s)) {
                showMsg("Student updated!", false);
                clearForm(); loadTable(null);
            }
        } catch (SQLException ex) { showMsg("Error: " + ex.getMessage(), true); }
    }

    private void deleteStudent() {
        if (selectedStudentId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this student and all their grades?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            if (dao.deleteStudent(selectedStudentId)) {
                showMsg("Student deleted.", false);
                clearForm(); loadTable(null);
            }
        } catch (SQLException ex) { showMsg("Error: " + ex.getMessage(), true); }
    }

    private void loadTable(String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Student> list = (keyword == null || keyword.isEmpty())
                ? dao.getAllStudents() : dao.searchStudents(keyword);
            for (Student s : list) {
                tableModel.addRow(new Object[]{
                    s.getStudentId(), s.getFullName(), s.getEmail(),
                    s.getPhone(), s.getDepartment(), s.getEnrolledOn()
                });
            }
        } catch (SQLException ex) { showMsg("DB error: " + ex.getMessage(), true); }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedStudentId = (int) tableModel.getValueAt(row, 0);
        tfName.setText((String) tableModel.getValueAt(row, 1));
        tfEmail.setText((String) tableModel.getValueAt(row, 2));
        tfPhone.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        tfDept.setText((String) tableModel.getValueAt(row, 4));
        tfDate.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnAdd.setEnabled(false);
    }

    private void clearForm() {
        tfName.setText(""); tfEmail.setText(""); tfPhone.setText("");
        tfDept.setText(""); tfDate.setText(LocalDate.now().toString());
        selectedStudentId = -1;
        btnAdd.setEnabled(true); btnUpdate.setEnabled(false); btnDelete.setEnabled(false);
        table.clearSelection();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private boolean validateForm() {
        if (tfName.getText().trim().isEmpty() || tfEmail.getText().trim().isEmpty()) {
            showMsg("Full Name and Email are required.", true); return false;
        }
        try { LocalDate.parse(tfDate.getText().trim()); }
        catch (Exception e) { showMsg("Date must be YYYY-MM-DD format.", true); return false; }
        return true;
    }

    private Student formToStudent(int id) {
        Student s = new Student();
        s.setStudentId(id);
        s.setFullName(tfName.getText().trim());
        s.setEmail(tfEmail.getText().trim());
        s.setPhone(tfPhone.getText().trim());
        s.setDepartment(tfDept.getText().trim());
        s.setEnrolledOn(LocalDate.parse(tfDate.getText().trim()));
        return s;
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
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    private void showMsg(String msg, boolean error) {
        JOptionPane.showMessageDialog(this, msg,
            error ? "Error" : "Success",
            error ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
}
