import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FacultyDashboardUI extends JFrame {

    private static final String BG_IMAGE = "src/facultybg.png";
    private final String profId;
    private final String fullName;
    private final List<String> subjects;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel underline;

    public FacultyDashboardUI(String profId, String fullName, List<String> subjects) {
        this.profId = profId;
        this.fullName = fullName;
        this.subjects = subjects;
        initUI();
    }

    private void initUI() {
        setTitle("Faculty Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel bg = new BackgroundPanel(BG_IMAGE);
        bg.setLayout(null);
        setContentPane(bg);

        // Welcome Label
        JLabel welcome = new JLabel("Welcome, " + fullName);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 23));
        welcome.setForeground(new Color(40, 40, 40));
        welcome.setBounds(205, 80, 600, 30);
        bg.add(welcome);

        // Logout
        JLabel logout = new JLabel("Log Out");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logout.setForeground(Color.BLACK);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setBounds(1650, 50, 120, 40);
        bg.add(logout);
        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new MainUI().setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                logout.setText("<html><u>Log Out</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logout.setText("Log Out");
            }
        });

        // White rounded main card
        RoundedPanel card = new RoundedPanel(25);
        card.setBounds(40, 180, 1850, 760);
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        bg.add(card);

        // Tab Panel
        JPanel tabPanel = new JPanel(null);
        tabPanel.setOpaque(false);
        tabPanel.setBounds(30, 20, 1700, 60);
        card.add(tabPanel);

        Font tabFont = new Font("Segoe UI", Font.BOLD, 22);
        JLabel tab1 = new JLabel("Courses");
        JLabel tab2 = new JLabel("Grade Management");
        JLabel tab3 = new JLabel("Assignments");
        JLabel tab4 = new JLabel("Resignation");

        JLabel[] tabs = { tab1, tab2, tab3, tab4 };

        tab1.setFont(tabFont);
        tab2.setFont(tabFont);
        tab3.setFont(tabFont);
        tab4.setFont(tabFont);
        tab1.setBounds(20, 10, 220, 40);
        tab2.setBounds(260, 10, 300, 40);
        tab3.setBounds(580, 10, 200, 40);
        tab4.setBounds(800, 10, 200, 40);

        tab1.setForeground(new Color(15, 36, 96));
        tab2.setForeground(Color.GRAY);
        tab3.setForeground(Color.GRAY);
        tab4.setForeground(Color.GRAY);

        for (JLabel t : tabs)
            t.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        for (JLabel t : tabs)
            tabPanel.add(t);

        // Underline
        underline = new JPanel();
        underline.setBackground(new Color(15, 36, 96));
        underline.setBounds(20, 52, 220, 4);
        tabPanel.add(underline);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setBounds(20, 80, 1810, 2);
        card.add(sep);

        // Card panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBounds(20, 90, 1810, 640);
        cardPanel.setOpaque(false);
        card.add(cardPanel);

        // Add panels
        cardPanel.add(new CoursesPanel(), "COURSES");
        cardPanel.add(new GradeManagementPanel(), "GRADES");
        cardPanel.add(new AssignmentsPanel(fullName, subjects), "ASSIGNMENTS");
        cardPanel.add(new ResignationPanel(), "RESIGNATION");

        // Show courses initially
        cardLayout.show(cardPanel, "COURSES");

        // Tab click listeners
        tab1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectTab(tabs, tab1, 20, 80, "COURSES");
            }
        });
        tab2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectTab(tabs, tab2, 260, 205, "GRADES");
            }
        });
        tab3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectTab(tabs, tab3, 580, 130, "ASSIGNMENTS");
            }
        });
        tab4.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectTab(tabs, tab4, 800, 125, "RESIGNATION");
            }
        });

        setVisible(true);
    }

    private void selectTab(JLabel[] tabs, JLabel clicked, int underlineX, int underlineW, String cardName) {
        for (JLabel t : tabs)
            t.setForeground(Color.GRAY);
        clicked.setForeground(new Color(15, 36, 96));
        underline.setBounds(underlineX, 52, underlineW, 4);
        underline.repaint();
        cardLayout.show(cardPanel, cardName);
    }

    // ---------------- GUI PANELS ---------------- //

    private class CoursesPanel extends JPanel {

        // Sidebar state
        private JButton selectedButton;

        // Sidebar colors
        private final Color SIDEBAR_NORMAL = new Color(60, 60, 60);
        private final Color SIDEBAR_HOVER = new Color(80, 80, 80);
        private final Color SIDEBAR_SELECTED = new Color(40, 40, 40);

        public CoursesPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            // ----- LEFT SIDE BUTTONS -----
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setPreferredSize(new Dimension(250, 0));
            leftPanel.setBackground(new Color(250, 245, 240));
            leftPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

            // Buttons
            JButton myCoursesBtn = createSidebarButton("My Courses");
            JButton requestCourseBtn = createSidebarButton("Request Course");

            // ----- WRAPPER PANELS -----

            // Centered wrapper for My Courses
            JPanel myWrapper = new JPanel();
            myWrapper.setLayout(new BoxLayout(myWrapper, BoxLayout.X_AXIS));
            myWrapper.setOpaque(false);
            myWrapper.add(Box.createHorizontalGlue()); // push to center
            myWrapper.add(myCoursesBtn);
            myWrapper.add(Box.createHorizontalGlue());

            // Normal left-aligned wrapper for Request Course
            JPanel reqWrapper = new JPanel();
            reqWrapper.setLayout(new BoxLayout(reqWrapper, BoxLayout.X_AXIS));
            reqWrapper.setOpaque(false);
            reqWrapper.add(requestCourseBtn);

            leftPanel.add(myWrapper);
            leftPanel.add(Box.createVerticalStrut(20));
            leftPanel.add(reqWrapper);

            // ----- RIGHT CONTENT AREA -----
            JPanel contentPanel = new JPanel(new CardLayout());
            contentPanel.setBackground(Color.WHITE);

            JPanel myCoursesPanel = createMyCoursesPanel();
            JPanel requestPanel = createRequestCoursePanel();

            JScrollPane myScroll = new JScrollPane(myCoursesPanel);
            myScroll.setBorder(null); // remove the outer border
            contentPanel.add(myScroll, "MY");

            JScrollPane reqScroll = new JScrollPane(requestPanel);
            reqScroll.setBorder(null); // remove the outer border
            contentPanel.add(reqScroll, "REQ");

            // Switch views
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            myCoursesBtn.addActionListener(e -> cl.show(contentPanel, "MY"));
            requestCourseBtn.addActionListener(e -> cl.show(contentPanel, "REQ"));

            add(leftPanel, BorderLayout.WEST);
            add(contentPanel, BorderLayout.CENTER);
        }

        // Sidebar button style
        private JButton createSidebarButton(String text) {
            JButton btn = new JButton(text);
            btn.setPreferredSize(new Dimension(200, 60));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
            btn.setOpaque(true);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setBackground(new Color(235, 235, 235)); // default

            btn.addActionListener(e -> {
                if (selectedButton != null)
                    selectedButton.setBackground(new Color(235, 235, 235)); // reset old

                btn.setBackground(new Color(211, 211, 211)); // dark gray selected
                selectedButton = btn;
            });

            return btn;
        }

       private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 245, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Read proposed schedule
        String profSchedulePath = "data/FACULTY/FACULTY_MEMBERS/" + profId + "/" + profId + "_Schedule.txt";
        List<String> proposedSchedule = Utils.readFile(profSchedulePath);

        List<ScheduleEntry> acceptedCourses = new ArrayList<>();
        List<ScheduleEntry> pendingCourses = new ArrayList<>();

        // Separate accepted vs pending courses
        for (String line : proposedSchedule) {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 8) continue;

            String yearLevel = parts[0].trim();
            String course = parts[1].trim();
            String section = parts[2].trim();
            String subject = parts[3].trim();
            String day = parts[4].trim();
            String time = parts[5].trim();
            String sy = parts[6].trim();
            String dept = parts[7].trim();

            // Check if this course exists in student's folder (accepted)
            String crsAndsec = course + section;
            String studDirPath = "data/STUDENTS/" + sy + "/" + yearLevel + "/" + dept + "/" + course + "/" + section + "/";
            String scheduleFilePath = studDirPath + crsAndsec + "_Schedule.txt";
            File scheduleFile = new File(scheduleFilePath);

            ScheduleEntry entry = new ScheduleEntry(sy, yearLevel, dept, course, section, subject, day, time);

            if (scheduleFile.exists()) {
                acceptedCourses.add(entry);
            } else {
                pendingCourses.add(entry);
            }
        }

        // ========== MY SCHEDULE SECTION (UPPER) ==========
        JLabel myScheduleTitle = new JLabel("MY SCHEDULE");
        myScheduleTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        myScheduleTitle.setForeground(new Color(10, 20, 70));
        myScheduleTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(myScheduleTitle);
        panel.add(Box.createVerticalStrut(10));

        JPanel myScheduleTable = new JPanel();
        myScheduleTable.setLayout(new BoxLayout(myScheduleTable, BoxLayout.Y_AXIS));
        myScheduleTable.setBackground(Color.WHITE);
        myScheduleTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        myScheduleTable.setAlignmentX(Component.LEFT_ALIGNMENT);

        myScheduleTable.add(createScheduleHeaderRow());

        if (acceptedCourses.isEmpty()) {
            JLabel empty = new JLabel("No accepted courses yet.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            empty.setForeground(Color.GRAY);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            myScheduleTable.add(empty);
        } else {
            for (ScheduleEntry entry : acceptedCourses) {
                myScheduleTable.add(createScheduleDataRow(
                    entry.yearLevel, entry.course, entry.section, entry.subject, 
                    entry.day + " " + entry.time, false
                ));
            }
        }

        panel.add(myScheduleTable);
        panel.add(Box.createVerticalStrut(30));

        // ========== AVAILABLE SCHEDULE SECTION (LOWER) ==========
        JLabel availableTitle = new JLabel("AVAILABLE SCHEDULE");
        availableTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        availableTitle.setForeground(new Color(10, 20, 70));
        availableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(availableTitle);
        panel.add(Box.createVerticalStrut(10));

        JPanel availableTable = new JPanel();
        availableTable.setLayout(new BoxLayout(availableTable, BoxLayout.Y_AXIS));
        availableTable.setBackground(Color.WHITE);
        availableTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        availableTable.setAlignmentX(Component.LEFT_ALIGNMENT);

        availableTable.add(createAvailableHeaderRow());

        if (pendingCourses.isEmpty()) {
            JLabel empty = new JLabel("No pending requests.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            empty.setForeground(Color.GRAY);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            availableTable.add(empty);
        } else {
            int num = 1;
            for (ScheduleEntry entry : pendingCourses) {
                availableTable.add(createAvailableDataRow(
                    num++, entry, panel
                ));
            }
        }

        panel.add(availableTable);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // Helper class for schedule entries
    private class ScheduleEntry {
        String sy, yearLevel, dept, course, section, subject, day, time;

        ScheduleEntry(String sy, String yearLevel, String dept, String course, 
                    String section, String subject, String day, String time) {
            this.sy = sy;
            this.yearLevel = yearLevel;
            this.dept = dept;
            this.course = course;
            this.section = section;
            this.subject = subject;
            this.day = day;
            this.time = time;
        }
    }

    // Header row for MY SCHEDULE
    private JPanel createScheduleHeaderRow() {
        JPanel row = new JPanel(new GridLayout(1, 5));
        row.setPreferredSize(new Dimension(1000, 50));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setBackground(new Color(225, 210, 160));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        String[] headers = {"Year Level", "Course", "Section", "Subject", "Day and Time"};
        for (String header : headers) {
            JLabel label = new JLabel(header);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(new Color(10, 20, 70));
            row.add(label);
        }

        return row;
    }

    // Data row for MY SCHEDULE
    private JPanel createScheduleDataRow(String yearLevel, String course, String section, 
                                        String subject, String dayTime, boolean isAlternate) {
        JPanel row = new JPanel(new GridLayout(1, 5));
        row.setPreferredSize(new Dimension(1000, 45));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        row.setBackground(isAlternate ? new Color(245, 245, 245) : Color.WHITE);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        String[] data = {yearLevel, course, section, subject, dayTime};
        for (String text : data) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            row.add(label);
        }

        return row;
    }

    // Header row for AVAILABLE SCHEDULE
    private JPanel createAvailableHeaderRow() {
        JPanel row = new JPanel(new GridLayout(1, 7));
        row.setPreferredSize(new Dimension(1000, 50));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setBackground(new Color(225, 210, 160));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        String[] headers = {"#", "Year Level", "Course", "Section", "Subject", "Day and Time", "Action"};

        for (String header : headers) {
            JLabel label = new JLabel(header);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(new Color(10, 20, 70));
            row.add(label);
        }

        return row;
    }

    // Data row for AVAILABLE SCHEDULE with Accept/Reject
    private JPanel createAvailableDataRow(int number, ScheduleEntry entry, JPanel parentPanel) {
        JPanel row = new JPanel(new GridLayout(1, 7));
        row.setPreferredSize(new Dimension(1000, 60));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // # column
        JLabel numLabel = new JLabel(String.valueOf(number));
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        numLabel.setHorizontalAlignment(SwingConstants.CENTER);
        row.add(numLabel);

        // Data columns
        String[] data = {
            entry.yearLevel, 
            entry.course, 
            entry.section, 
            entry.subject, 
            entry.day + " " + entry.time
        };
        
        for (String text : data) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            row.add(label);
        }

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        actionPanel.setOpaque(false);

        JButton acceptBtn = new JButton("Accept");
        acceptBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        acceptBtn.setBackground(new Color(76, 175, 80));
        acceptBtn.setForeground(Color.WHITE);
        acceptBtn.setFocusPainted(false);
        acceptBtn.setBorderPainted(false);
        acceptBtn.setPreferredSize(new Dimension(70, 40));
        acceptBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        rejectBtn.setBackground(new Color(244, 67, 54));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFocusPainted(false);
        rejectBtn.setBorderPainted(false);
        rejectBtn.setPreferredSize(new Dimension(70, 40));
        rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ACCEPT button action
        acceptBtn.addActionListener(e -> {
            // Create student schedule file
            String crsAndsec = entry.course + entry.section;
            String studDirPath = "data/STUDENTS/" + entry.sy + "/" + entry.yearLevel + "/" + 
                                entry.dept + "/" + entry.course + "/" + entry.section + "/";
            
            File studDir = new File(studDirPath);
            if (!studDir.exists()) {
                studDir.mkdirs(); // Create directory if it doesn't exist
            }

            String scheduleFilePath = studDirPath + crsAndsec + "_Schedule.txt";
            
            // Write schedule line to student file
            String scheduleLine = entry.yearLevel + "|" + entry.course + "|" + entry.section + "|" + 
                                entry.subject + "|" + entry.day + "|" + entry.time + "|" + 
                                entry.sy + "|" + entry.dept;
            
            try {
                java.nio.file.Files.write(
                    java.nio.file.Paths.get(scheduleFilePath),
                    (scheduleLine + "\n").getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND
                );
                
                JOptionPane.showMessageDialog(row, "Course accepted successfully!");
                
                // Refresh the panel
                refreshCoursesPanel(parentPanel);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(row, "Error accepting course: " + ex.getMessage());
            }
        });

        // REJECT button action
        rejectBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                row,
                "Are you sure you want to reject this course?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from proposed schedule file
                String profSchedulePath = "data/FACULTY/FACULTY_MEMBERS/" + profId + "/" + profId + "_Schedule.txt";
                List<String> lines = Utils.readFile(profSchedulePath);
                
                String lineToRemove = entry.yearLevel + "|" + entry.course + "|" + entry.section + "|" + 
                                    entry.subject + "|" + entry.day + "|" + entry.time + "|" + 
                                    entry.sy + "|" + entry.dept;
                
                lines.remove(lineToRemove);
                
                try {
                    java.nio.file.Files.write(
                        java.nio.file.Paths.get(profSchedulePath),
                        String.join("\n", lines).getBytes()
                    );
                    
                    JOptionPane.showMessageDialog(row, "Course rejected.");
                    
                    // Refresh the panel
                    refreshCoursesPanel(parentPanel);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(row, "Error rejecting course: " + ex.getMessage());
                }
            }
        });

        actionPanel.add(acceptBtn);
        actionPanel.add(rejectBtn);
        row.add(actionPanel);

        // Hover effect
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                row.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                row.setBackground(Color.WHITE);
            }
        });

        return row;
    }

    // Helper method to refresh the courses panel
    private void refreshCoursesPanel(JPanel parentPanel) {
        Container parent = parentPanel.getParent();
        parent.remove(parentPanel);
        parent.add(createMyCoursesPanel());
        parent.revalidate();
        parent.repaint();
    }

        private JPanel createHeaderBox() {
            return courseBox("COURSE NAME", "SCHEDULE", "PROGRAM");
        }

        private JPanel courseBox(String courseName, String schedule, String programHandled) {
            JPanel box = new JPanel();
            box.setLayout(new GridLayout(1, 3));
            box.setPreferredSize(new Dimension(900, 70));
            box.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            box.setBackground(new Color(245, 235, 225));

            JLabel name = new JLabel(courseName);
            name.setFont(new Font("Segoe UI", Font.BOLD, 20));

            JLabel sched = new JLabel(schedule);
            sched.setFont(new Font("Segoe UI", Font.BOLD, 18));
            sched.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel program = new JLabel(programHandled);
            program.setFont(new Font("Segoe UI", Font.BOLD, 18));
            program.setHorizontalAlignment(SwingConstants.CENTER);

            box.add(name);
            box.add(sched);
            box.add(program);

            return box;
        }

        private JPanel createRequestCoursePanel() {
            // Outer panel (white background)
            JPanel outer = new JPanel(new BorderLayout());
            outer.setBackground(Color.WHITE);
            outer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Inner rounded content panel
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBackground(new Color(247, 240, 230)); // light beige
            content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            // Label (dark navy text)
            JLabel label = new JLabel("REASON FOR REQUEST");
            label.setFont(new Font("Segoe UI", Font.BOLD, 20));
            label.setForeground(new Color(10, 20, 70));
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            // Text area container (rounded)
            JPanel textAreaContainer = new JPanel(new BorderLayout());
            textAreaContainer.setBackground(new Color(240, 225, 200)); // beige like screenshot
            textAreaContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JTextArea reason = new JTextArea(7, 30);
            reason.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            reason.setLineWrap(true);
            reason.setWrapStyleWord(true);
            reason.setBorder(null); // remove default border
            reason.setOpaque(false); // match beige background

            textAreaContainer.add(reason);

            // Submit button
            JButton submit = new JButton("Submit");
            submit.setFont(new Font("Segoe UI", Font.BOLD, 18));
            submit.setBackground(new Color(225, 210, 160));
            submit.setForeground(new Color(10, 20, 70));
            submit.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            submit.setFocusPainted(false);

            // Add ActionListener

            submit.addActionListener(e -> {
                String text = reason.getText().trim();
                if (text.isEmpty()) {
                    JOptionPane.showMessageDialog(outer, "Please enter a reason.");
                } else {
                    // Here you can save the reason to file or database
                    JOptionPane.showMessageDialog(outer, "Request submitted!");
                    reason.setText(""); // clear after submission
                }
            });

            // Add all components
            content.add(label);
            content.add(Box.createVerticalStrut(20));
            content.add(textAreaContainer);
            content.add(Box.createVerticalStrut(25));
            content.add(submit);

            // Add to outer panel
            outer.add(content, BorderLayout.CENTER);
            return outer;
        }
    }

 
    public class GradeManagementPanel extends JPanel {

    private JComboBox<String> yearCombo;
    private JComboBox<String> programCombo;
    private JComboBox<String> sectionCombo;
    private JTextField studentIdField;
    private JTextField gradeField;
    private JTable table;
    private DefaultTableModel tableModel;

    private List<String[]> allStudents = new ArrayList<>(); // [id, year, program, lastName]


    public GradeManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        initTopFilters();
        initListPanel();
        initBottomPanel();

        loadAllStudents();
        filterAndUpdateList(); // initial table load
    }

    private void initTopFilters() {
        JPanel topFilters = new JPanel(new GridBagLayout());
        topFilters.setBackground(new Color(247, 240, 230));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(15, 25, 15, 25);
        c.anchor = GridBagConstraints.WEST;

        JLabel lblYear = label("Year Level :");
        JLabel lblProg = label("Program :");
        JLabel lblSec = label("Section :");

        yearCombo = dropdown(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        programCombo = dropdown(new String[]{"BSCE", "BSEE", "BSME", "BSIE", "BTVTE", "BSBM", "BSE", "BSHM", "BSIT", "BSCS", "BSIS", "BSES", "BASLT", "BSA", "BFA", "BGT"});
        sectionCombo = dropdown(new String[]{"A", "B", "C", "D"});

        c.gridx = 0; c.gridy = 0; topFilters.add(lblYear, c);
        c.gridx = 1; topFilters.add(yearCombo, c);
        c.gridx = 2; topFilters.add(lblProg, c);
        c.gridx = 3; topFilters.add(programCombo, c);
        c.gridx = 4; topFilters.add(lblSec, c);
        c.gridx = 5; topFilters.add(sectionCombo, c);

        yearCombo.addActionListener(e -> filterAndUpdateList());
        programCombo.addActionListener(e -> filterAndUpdateList());
        sectionCombo.addActionListener(e -> filterAndUpdateList());

        add(topFilters, BorderLayout.NORTH);
    }

    private void initListPanel() {
         JPanel listPanel = new JPanel(new BorderLayout());
            listPanel.setBackground(new Color(247, 240, 230));
            listPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

            String[] columns = {"Student ID", "Year Level", "Program", "Last Name"};
            
            tableModel = new DefaultTableModel(columns, 0);  // assign here
        
            table = new JTable(tableModel);  // use the class-level tableModel
            table.setFont(new Font("Segoe UI", Font.BOLD, 17));
            table.setForeground(new Color(225, 210, 160));
            table.setRowHeight(25);
            table.setFillsViewportHeight(true);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        // Add mouse listener here:
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String studentId = (String) table.getValueAt(row, 0);
                    studentIdField.setText(studentId);  // fill studentIdField
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(225, 210, 160));
        listPanel.add(scroll, BorderLayout.CENTER);

        add(listPanel, BorderLayout.CENTER);
}


    private void initBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 20));
        bottom.setBackground(Color.WHITE);

        JLabel lblId = label("Enter Student ID :");
        studentIdField = roundedInputField();
        studentIdField.addActionListener(e -> filterAndUpdateList()); // update table on typing Enter

        JLabel lblGrade = label("Enter Grade :");
        gradeField = roundedInputField();

        JButton submitBtn = new JButton("Submit");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitBtn.setBackground(new Color(225, 210, 160));
        submitBtn.setForeground(new Color(10, 20, 70));
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> submitGrade());

        bottom.add(lblId);
        bottom.add(studentIdField);
        bottom.add(lblGrade);
        bottom.add(gradeField);
        bottom.add(submitBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    // ----------------- STYLED COMPONENT HELPERS -----------------
    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(10, 20, 70));
        return lbl;
    }

    private JTextField roundedInputField() {
        JTextField tf = new JTextField(12);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tf.setBackground(new Color(245, 235, 220));
        tf.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return tf;
    }

    private JComboBox<String> dropdown(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cb.setBackground(new Color(245, 235, 220));
        cb.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        cb.setPreferredSize(new Dimension(160, 40));
        return cb;
    }

    // ----------------- DATA HANDLING -----------------
    private void loadAllStudents() {
        allStudents.clear();
        File root = new File("data/STUDENTS");

        System.out.println("Root path: " + root.getAbsolutePath());
        System.out.println("Root exists: " + root.exists());
        System.out.println("Root is directory: " + root.isDirectory());

        List<File> files = getAllStudentFiles(root);
        System.out.println("Files found: " + files.size());

        for (File f : files) {
            List<String> lines = Utils.readFile(f.getPath());
            for (String line : lines) {
                String[] p = line.split("\\|", -1);
                if (p.length >= 4) {
                    allStudents.add(p); // [id, year, program, lastName]
                }
            }
        }
    }

    private void filterAndUpdateList() {
        String year = (String) yearCombo.getSelectedItem();
        String program = (String) programCombo.getSelectedItem();
        // Assuming section filtering logic here if needed
        String searchId = studentIdField.getText().trim().toLowerCase();

        List<String[]> filtered = new ArrayList<>();
        for (String[] s : allStudents) {
            boolean matches = (year == null || s[1].equalsIgnoreCase(year))
                    && (program == null || s[2].equalsIgnoreCase(program))
                    && (searchId.isEmpty() || s[0].toLowerCase().contains(searchId));
            if (matches) filtered.add(s);
        }

        // Clear current rows
        tableModel.setRowCount(0);

        // Add filtered rows
        for (String[] s : filtered) {
            tableModel.addRow(s);
        }
    }


    private void submitGrade() {
        
        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) return;

        double grade;
        try {
            grade = Double.parseDouble(gradeField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid grade.");
            return;
        }

        File studentFile = findStudentFile(studentId);
        if (studentFile == null) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        List<String> lines = Utils.readFile(studentFile.getPath());
        List<String> updated = new ArrayList<>();

        for (String line : lines) {
            String[] p = line.split("\\|", -1);
            if (p.length > 0 && p[0].equalsIgnoreCase(studentId)) {
                updated.add(line + "|" + profId + ":" + grade);
            } else {
                updated.add(line);
            }
        }

        Utils.writeFile(studentFile.getPath(), updated);
        JOptionPane.showMessageDialog(this, "Grade recorded for " + studentId);

        // Reload students and refresh table
        loadAllStudents();
        filterAndUpdateList();
    }

    private File findStudentFile(String studentId) {
        File root = new File("data/STUDENTS");
        List<File> allFiles = getAllStudentFiles(root);

        for (File f : allFiles) {
            List<String> lines = Utils.readFile(f.getPath());
            for (String line : lines) {
                String[] p = line.split("\\|");
                if (p.length > 0 && p[0].equalsIgnoreCase(studentId))
                    return f;
            }
        }
        return null;
    }

    private List<File> getAllStudentFiles(File dir) {
        List<File> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null) return result;

        for (File f : files) {
            if (f.isDirectory())
                result.addAll(getAllStudentFiles(f));
            else if (f.getName().endsWith("_grades.txt"))
                result.add(f);
        }
        return result;
    }
}


    public class AssignmentsPanel extends JPanel {

        private JTextField yearLevelField, programField, sectionField, titleField, deadlineField;
        private JTextArea descArea;
        private JComboBox<String> courseBox;

        private RoundedPanel box;

        private JButton loadBtn, postBtn;

        private String fullName;
        private List<String> subjects;

        public AssignmentsPanel(String fullName, List<String> subjects) {
        this.fullName = fullName;
        this.subjects = subjects;

        setLayout(null);
        setBackground(Color.WHITE);

        // Beige rounded panel
        box = new RoundedPanel(25);
        box.setBackground(new Color(248, 239, 224));
        box.setLayout(null);
        add(box);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 24);

        // ======= DROPDOWNS =======
        JLabel lblYear = new JLabel("Year Level :");
        lblYear.setFont(labelFont);
        box.add(lblYear);

        JComboBox<String> yearBox = new JComboBox<>(new String[]{
                "1st Year", "2nd Year", "3rd Year", "4th Year"
        });
        styleCombo(yearBox);
        box.add(yearBox);
         yearBox.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel lblProgram = new JLabel("Program :");
        lblProgram.setFont(labelFont);
        box.add(lblProgram);

        JComboBox<String> programBox = new JComboBox<>(new String[]{
                "BSCE", "BSEE", "BSME", "BSIE", "BTVTE", "BSBM", "BSE", "BSHM", "BSIT", "BSCS", "BSIS", "BSES", "BASLT", "BSA", "BFA", "BGT"
        });
        styleCombo(programBox);
        box.add(programBox);
         programBox.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel lblSection = new JLabel("Section :");
        lblSection.setFont(labelFont);
        box.add(lblSection);

        JComboBox<String> sectionBox = new JComboBox<>(new String[]{
                "A", "B", "C", "D"
        });
        styleCombo(sectionBox);
        box.add(sectionBox);
         sectionBox.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("Assignment Title");
        lblTitle.setFont(labelFont);
        box.add(lblTitle);

        titleField = createField();
        box.add(titleField);

        // ===== DESCRIPTION =====
        JLabel lblDesc = new JLabel("Description");
        lblDesc.setFont(labelFont);
        box.add(lblDesc);

        descArea = new JTextArea();
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descArea.setBackground(new Color(242, 234, 218));
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        box.add(descScroll);

        // ===== DEADLINE =====
        JLabel lblDeadline = new JLabel("Deadline");
        lblDeadline.setFont(labelFont);
        box.add(lblDeadline);

        deadlineField = createField();
        box.add(deadlineField);

        // ===== POST BUTTON =====
        postBtn = new JButton("POST");
        postBtn.setBackground(new Color(210, 190, 150));
        postBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        box.add(postBtn);
        box.setComponentZOrder(postBtn, 0);
        box.repaint();


        // ===== RESPONSIVE (auto-center + auto-resize) =====
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int width = getWidth() - 80;   // form box left-right padding
                int height = getHeight() - 180;

                // Form box stretch LEFT → RIGHT q
                box.setBounds(40, 40, width, height + 300);

                int insideW = width - 80; // inner padding
                int columnW = insideW / 3; // 3 columns for dropdowns

                // Row 1
                lblYear.setBounds(40, 25, 150, 30);
                yearBox.setBounds(40, 55, columnW - 40, 30);

                lblProgram.setBounds(60 + columnW, 25, 150, 30);
                programBox.setBounds(60 + columnW, 55, columnW - 40, 30);

                lblSection.setBounds(80 + (columnW * 2), 25, 150, 30);
                sectionBox.setBounds(80 + (columnW * 2), 55, columnW - 20, 30);

                // TITLE
                lblTitle.setBounds(40, 110, 200, 30);
                titleField.setBounds(40, 145, insideW, 30);

                // DESCRIPTION
                lblDesc.setBounds(40, 195, 200, 30);
                descScroll.setBounds(40, 230, insideW, 140);

                // DEADLINE & POST button
                lblDeadline.setBounds(40, 390, 200, 30);
                deadlineField.setBounds(40, 425, insideW / 3, 30);

                postBtn.setBounds(width - 200, 455, 150, 40);
            }
        });
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(new Color(242, 234, 218));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }


        // ==========================================================
        // RESPONSIVE LAYOUT ENGINE
        // ====================================================s======
        private void resizeComponents(JScrollPane descScroll) {
            int margin = 40;

            // Resize beige box based on panel size
            box.setBounds(
                    20, // left margin
                    40, // top margin
                    getWidth() - 40, // full width minus margin
                    getHeight() - 80 // full height minus margin
            );

            int boxWidth = box.getWidth();

            // Course row
            courseBox.setBounds(40, 100, boxWidth - 240, 28);
            loadBtn.setBounds(boxWidth - 180, 90, 140, 28);

            // Title
            titleField.setBounds(40, 165, boxWidth - 80, 30);

            // Description grows
            descScroll.setBounds(40, 230, boxWidth - 80, 160);

            // Deadline stays on left
            deadlineField.setBounds(40, 400, 250, 30);

            // Post button moves right
            postBtn.setBounds(boxWidth - 200, 400, 150, 35);
        }

        // ===========================================================
        // LOAD COURSES (SAME AS YOUR CODE)
        // ===========================================================
        private void loadCourses() {
            courseBox.removeAllItems();

            String path = "data/STUDENTS/" +
                    yearLevelField.getText().trim() + "/" +
                    yearLevelField.getText().trim() + "/" +
                    programField.getText().trim().toUpperCase() + "/" +
                    sectionField.getText().trim();

            File scheduleFile = new File(path + "/" + sectionField.getText().trim() + "_schedule.txt");

            if (!scheduleFile.exists()) {
                JOptionPane.showMessageDialog(this, "Schedule not found.");
                return;
            }

            List<String> lines = Utils.readFile(scheduleFile.getPath());
            for (String line : lines) {
                String courseName = line.split(",")[0].trim();
                if (subjects.contains(courseName)) {
                    courseBox.addItem(courseName);
                }
            }

            if (courseBox.getItemCount() == 0)
                JOptionPane.showMessageDialog(this, "No courses available.");
        }

        // ===========================================================
        // SAVE ASSIGNMENT (SAME AS YOUR CODE)
        // ===========================================================
        private void saveAssignment() {
            String course = (String) courseBox.getSelectedItem();
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            String deadline = deadlineField.getText().trim();

            if (course == null || title.isEmpty() || desc.isEmpty() || deadline.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required.");
                return;
            }

            String folder = "data/STUDENTS/" +
                    yearLevelField.getText().trim() + "/" +
                    yearLevelField.getText().trim() + "/" +
                    programField.getText().trim().toUpperCase() + "/" +
                    sectionField.getText().trim();

            Utils.ensureDir(folder);

            String line = fullName + "|" + course + "|" + title + "|" + desc + "|" + deadline
                    + "|UPCOMING|PAST DUE|COMPLETED";

            Utils.appendToFile(folder + "/" +
                    sectionField.getText().trim() + "_assignments.txt",
                    Utils.encrypt(line));

            JOptionPane.showMessageDialog(this, "Assignment saved successfully.");
        }

        // ===========================================================
        // UI HELPERS
        // ===========================================================
        private JTextField createField() {
            JTextField tf = new JTextField();
            tf.setBackground(new Color(242, 234, 218));
            tf.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            return tf;
        }

        class RoundedPanel extends JPanel {
            private int radius;

            public RoundedPanel(int radius) {
                this.radius = radius;
                setOpaque(false);
            }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
            }
        }

        class RoundedButton extends JButton {
            public RoundedButton(String text) {
                super(text);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
            }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2.dispose();
            }
        }
    }

    private class ResignationPanel extends JPanel {
    
        private JTextArea infoArea;
        private JTextField reasonField;
        private JTextArea reasonArea;
        private JButton submitBtn;
        private RoundedPanel mainBox;  

        public ResignationPanel() {

        setLayout(null);
        setBackground(Color.WHITE);

        // === OUTER BEIGE PANEL 
        mainBox = new RoundedPanel(30);
        mainBox.setBackground(new Color(248, 239, 224));  
        mainBox.setLayout(null);
        add(mainBox);

        Font titleFont = new Font("Segoe UI", Font.BOLD, 25);

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("Reason for Resignation");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(10, 20, 60));
        mainBox.add(lblTitle);

        // ===== INNER LIGHT BEIGE TEXTAREA PANEL =====
        RoundedPanel textBox = new RoundedPanel(20);
        textBox.setLayout(null);
        textBox.setBackground(new Color(242, 234, 218)); 
        mainBox.add(textBox);

        // TEXTAREA
        reasonArea = new JTextArea();
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        reasonArea.setBackground(new Color(242, 234, 218));
        reasonArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane sp = new JScrollPane(reasonArea);
        sp.setBorder(null);
        textBox.add(sp);

        // ===== SUBMIT BUTTON =====
        submitBtn = new JButton("Submit");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        submitBtn.setForeground(new Color(10, 20, 60));
        submitBtn.setBackground(new Color(210, 190, 150)); 
        submitBtn.setBorder(BorderFactory.createEmptyBorder());
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainBox.add(submitBtn);

        submitBtn.addActionListener(e -> submitResignation());

        // ===== RESPONSIVE AUTO-RESIZE =====
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int width = getWidth() - 80;

                mainBox.setBounds(40, 40, width, getHeight() - 100);

                int insideW = width - 80;

                lblTitle.setBounds(40, 25, 600, 30);

                textBox.setBounds(40, 70, insideW, 250);
                sp.setBounds(10, 10, insideW - 20, 220);

                submitBtn.setBounds(insideW - 200, 400, 150, 35);
            }
        });
    }

        private void loadExisting() {
            String filePath = "data/FACULTY/RESIGNATIONS/" + profId + "_resignation.txt";
            File f = new File(filePath);
            if (!f.exists())
                return;
            List<String> info = Utils.readFile(filePath);
            if (info.isEmpty())
                return;
            String decrypted = Utils.decryptEachField(info.get(0));
            infoArea.setText(decrypted.replace("|", "\n"));
        }

        private void submitResignation() {
            String reason = reasonArea.getText().trim();
            if (reason.isEmpty())
                return;
            Utils.ensureDir("data/FACULTY/RESIGNATIONS");
            String filePath = "data/FACULTY/RESIGNATIONS/" + profId + "_resignation.txt";
            String line = profId + "|" + fullName + "|" + reason + "|Pending";
            Utils.writeFile(filePath, List.of(line));
            JOptionPane.showMessageDialog(this, "Resignation submitted successfully.");
            loadExisting();
        }
    }

    // ---------------- HELPER CLASSES ---------------- //

    private class BackgroundPanel extends JPanel {
        Image img;

        BackgroundPanel(String path) {
            img = new ImageIcon(path).getImage();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null)
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private class RoundedPanel extends JPanel {
        int arc;

        RoundedPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
