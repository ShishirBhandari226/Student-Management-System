package gui;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private StudentPanel  studentPanel;
    private GradePanel    gradePanel;
    private ReportPanel   reportPanel;

    public Dashboard() {
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 41, 59));
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // App title
        JLabel appTitle = new JLabel("  SMS");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        appTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 8, 0));

        JLabel appSub = new JLabel("  Student Management");
        appSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        appSub.setForeground(new Color(148, 163, 184));
        appSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        appSub.setBorder(BorderFactory.createEmptyBorder(0, 20, 24, 0));

        sidebar.add(appTitle);
        sidebar.add(appSub);

        // Nav buttons
        String[] navLabels = {"🎓  Students", "📊  Grades", "📄  Reports"};
        String[] cardNames = {"STUDENTS", "GRADES", "REPORTS"};
        JButton[] navButtons = new JButton[navLabels.length];

        for (int i = 0; i < navLabels.length; i++) {
            final String cardName = cardNames[i];
            final int idx = i;
            JButton btn = createNavButton(navLabels[i]);
            navButtons[i] = btn;
            btn.addActionListener(e -> {
                cardLayout.show(contentPanel, cardName);
                for (JButton b : navButtons) b.setBackground(new Color(30, 41, 59));
                btn.setBackground(new Color(51, 65, 85));
                if (cardName.equals("REPORTS")) reportPanel.refreshReport();
            });
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(4));
        }

        // --- Content area ---
        cardLayout    = new CardLayout();
        contentPanel  = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(248, 250, 252));

        studentPanel = new StudentPanel();
        gradePanel   = new GradePanel();
        reportPanel  = new ReportPanel();

        contentPanel.add(studentPanel, "STUDENTS");
        contentPanel.add(gradePanel,   "GRADES");
        contentPanel.add(reportPanel,  "REPORTS");

        // Default active
        navButtons[0].setBackground(new Color(51, 65, 85));

        add(sidebar,       BorderLayout.WEST);
        add(contentPanel,  BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(new Color(30, 41, 59));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(210, 48));
        btn.setMinimumSize(new Dimension(210, 48));
        btn.setPreferredSize(new Dimension(210, 48));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(203, 213, 225));
            }
        });
        return btn;
    }
}
