import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        cardPanel.add(new GradeManagementPanel(profId, fullName), "GRADES");
        cardPanel.add(new AssignmentsPanel(fullName, subjects, profId), "ASSIGNMENTS");
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

    private JComboBox<String> yearCombo, programCombo, sectionCombo, subjectCombo;
    private JTextField studentIdField, gradeField;
    private JTable table;
    private DefaultTableModel tableModel;
    
    private String profId;
    private String fullName;
    private List<StudentEntry> allStudents = new ArrayList<>();
    private Map<String, String> scheduleSubjects = new HashMap<>();


    public GradeManagementPanel(String profId, String fullName) {
        this.profId = profId;
        this.fullName = fullName;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        
        initTopFilters();     
        initListPanel();     
        initBottomPanel();   

        loadFacultySchedule();     
        loadStudentsFromSchedule();
        filterAndUpdateList();
    }

    // ============ TOP FILTERS ============
    private void initTopFilters() {
        JPanel topFilters = new JPanel(new GridBagLayout());
        topFilters.setBackground(new Color(247, 240, 230));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(15, 25, 15, 25);
        c.anchor = GridBagConstraints.WEST;

        JLabel lblYear = label("Year Level:");
        JLabel lblProg = label("Program:");
        JLabel lblSec = label("Section:");
        JLabel lblSubj = label("Subject:");

        yearCombo = dropdown(new String[]{"All", "1stYear", "2ndYear", "3rdYear", "4thYear"});
        programCombo = dropdown(new String[]{"All", "BSCE", "BSEE", "BSME", "BSIE", "BTVTE", "BSBM", "BSE", "BSHM", "BSIT", "BSCS", "BSIS", "BSES", "BASLT", "BSA", "BFA", "BGT"});
        sectionCombo = dropdown(new String[]{"All", "A", "B", "C", "D"});
        subjectCombo = dropdown(new String[]{"All"});

        c.gridx = 0; c.gridy = 0; topFilters.add(lblYear, c);
        c.gridx = 1; topFilters.add(yearCombo, c);
        c.gridx = 2; topFilters.add(lblProg, c);
        c.gridx = 3; topFilters.add(programCombo, c);
        c.gridx = 0; c.gridy = 1; topFilters.add(lblSec, c);
        c.gridx = 1; topFilters.add(sectionCombo, c);
        c.gridx = 2; topFilters.add(lblSubj, c);
        c.gridx = 3; topFilters.add(subjectCombo, c);

        yearCombo.addActionListener(e -> filterAndUpdateList());
        programCombo.addActionListener(e -> filterAndUpdateList());
        sectionCombo.addActionListener(e -> filterAndUpdateList());
        subjectCombo.addActionListener(e -> filterAndUpdateList());

        add(topFilters, BorderLayout.NORTH);
    }

    // ============ TABLE PANEL ============
    private void initListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(new Color(247, 240, 230));
        listPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        String[] columns = {"Student ID", "Year Level", "Program", "Section", "Last Name", "Subject", "Grade"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setForeground(new Color(10, 20, 70));
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(225, 210, 160));
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String studentId = (String) table.getValueAt(row, 0);
                    String subject = (String) table.getValueAt(row, 5);
                    studentIdField.setText(studentId);
                    
                    for (int i = 0; i < subjectCombo.getItemCount(); i++) {
                        if (subjectCombo.getItemAt(i).equals(subject)) {
                            subjectCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        listPanel.add(scroll, BorderLayout.CENTER);

        add(listPanel, BorderLayout.CENTER);
    }

    // ============ BOTTOM PANEL (INPUT FIELDS) ============
    private void initBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        bottom.setBackground(Color.WHITE);

        JLabel lblId = label("Student ID:");
        studentIdField = roundedInputField();
        studentIdField.addActionListener(e -> filterAndUpdateList());

        JLabel lblGrade = label("Grade:");
        gradeField = roundedInputField();

        JButton submitBtn = new JButton("Submit Grade");
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

    // ============ STYLED COMPONENTS ============
    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(new Color(10, 20, 70));
        return lbl;
    }

    private JTextField roundedInputField() {
        JTextField tf = new JTextField(12);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setBackground(new Color(245, 235, 220));
        tf.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return tf;
    }

    private JComboBox<String> dropdown(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cb.setBackground(new Color(245, 235, 220));
        cb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cb.setPreferredSize(new Dimension(140, 35));
        return cb;
    }

    // ============ DATA LOADING ============
    private void loadFacultySchedule() {
        File schedFile = new File("data/FACULTY/FACULTY_MEMBERS/" + profId + "/" + profId + "_Schedule.txt");
        if (!schedFile.exists()) {
            System.out.println("No schedule file found for professor: " + profId);
            return;
        }

        List<String> lines = Utils.readFile(schedFile.getPath());
        Set<String> subjects = new HashSet<>();
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length < 8)continue;
            
            String year = parts[0];
            String program = parts[1];
            String section = parts[2];
            String subject = parts[3];
            
            String key = year + "|" + program + "|" + section;
            scheduleSubjects.put(key, subject);
            subjects.add(subject);
        }
        
        subjectCombo.removeAllItems();
        subjectCombo.addItem("All");
        for (String subj : subjects) {
            subjectCombo.addItem(subj);
        }
    }

    private void loadStudentsFromSchedule() {
        allStudents.clear();
        
        File schedFile = new File("data/FACULTY/FACULTY_MEMBERS/" + profId + "/" + profId + "_Schedule.txt");
        if (!schedFile.exists()) return;

        List<String> schedLines = Utils.readFile(schedFile.getPath());
        File studentsRoot = new File("data/STUDENTS");

        for (String line : schedLines) {
            String[] p = line.split("\\|");
            if (p.length < 8) continue;

            String year = p[0];
            String program = p[1];
            String section = p[2];
            String subject = p[3];
            String schoolYear = p[6];
            String college = p[7];

            File sectionDir = new File(studentsRoot,
                    schoolYear + "/" + year + "/" + college + "/" + program + "/" + section);
            
            if (!sectionDir.exists()) continue;

            File[] studentDirs = sectionDir.listFiles(f -> f.isDirectory());
            if (studentDirs == null) continue;

            for (File studentDir : studentDirs) {
                File infoFile = new File(studentDir, studentDir.getName() + "_info.txt");
                File gradesFile = new File(studentDir, studentDir.getName() + "_grades.txt");
                
                if (!infoFile.exists()) continue;

                List<String> infoLines = Utils.readFile(infoFile.getPath());
                if (infoLines.isEmpty()) continue;

                String decrypted = Utils.decryptEachField(infoLines.get(0));
                String[] studentInfo = decrypted.split("\\|", -1);
                
                if (studentInfo.length < 2) continue;

                String studentId = studentInfo[0];
                String lastName = studentInfo[1];

                Float grade = getProfGrade(gradesFile, subject);

                allStudents.add(new StudentEntry(studentId, year, program, section, lastName, subject, gradesFile, grade));
            }
        }
    }

    private Float getProfGrade(File gradesFile, String subject) {
    if (!gradesFile.exists()) return null;

    String encProfId = Utils.encryptEachField(profId);
    String encSubject = Utils.encryptEachField(subject);

    List<String> lines = Utils.readFile(gradesFile.getPath());
    for (String line : lines) {
        if (line.trim().isEmpty()) continue;
        
        String[] parts = line.split("~", -1);  // ← Change ~ to |
        if (parts.length < 4) continue;

        if (parts[0].equals(encProfId) && parts[2].equals(encSubject)) {
            try {
                return Float.parseFloat(parts[3]);  // ← Grade is plain text
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
    return null;
}

    // ============ FILTER & UPDATE TABLE ============
    private void filterAndUpdateList() {
         if (studentIdField == null || subjectCombo == null) {
            return;
        }

        String yearFilter = (String) yearCombo.getSelectedItem();
        String programFilter = (String) programCombo.getSelectedItem();
        String sectionFilter = (String) sectionCombo.getSelectedItem();
        String subjectFilter = (String) subjectCombo.getSelectedItem();
        String searchId = studentIdField.getText().trim().toLowerCase();

        List<StudentEntry> filtered = new ArrayList<>();
        
        for (StudentEntry s : allStudents) {
            boolean matches = true;
            
            if (!yearFilter.equals("All") && !s.year.equalsIgnoreCase(yearFilter)) matches = false;
            if (!programFilter.equals("All") && !s.program.equalsIgnoreCase(programFilter)) matches = false;
            if (!sectionFilter.equals("All") && !s.section.equalsIgnoreCase(sectionFilter)) matches = false;
            if (!subjectFilter.equals("All") && !s.subject.equalsIgnoreCase(subjectFilter)) matches = false;
            if (!searchId.isEmpty() && !s.studentId.toLowerCase().contains(searchId)) matches = false;
            
            if (matches) filtered.add(s);
        }

        tableModel.setRowCount(0);

        for (StudentEntry s : filtered) {
            String gradeDisplay = s.grade == null ? "" : String.format("%.2f", s.grade);
            tableModel.addRow(new Object[]{
                s.studentId, 
                s.year, 
                s.program, 
                s.section, 
                s.lastName,
                s.subject,
                gradeDisplay
            });
        }
    }

    // ============ SUBMIT GRADE ============
    private void submitGrade() {
        String studentId = studentIdField.getText().trim();
        String gradeText = gradeField.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gradeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Grade.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        float grade;
        try {
            grade = Float.parseFloat(gradeText);
            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(this, "Grade must be between 0 and 100.", "Invalid Grade", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid grade format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StudentEntry target = null;
        String selectedSubject = (String) subjectCombo.getSelectedItem();
        
        for (StudentEntry s : allStudents) {
            if (s.studentId.equalsIgnoreCase(studentId)) {
                if (selectedSubject.equals("All") || s.subject.equals(selectedSubject)) {
                    target = s;
                    break;
                }
            }
        }

        if (target == null) {
            JOptionPane.showMessageDialog(this, 
                "Student not found in your handled classes.", 
                "Student Not Found", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (target.grade != null) {
            int choice = JOptionPane.showConfirmDialog(this,
                "This student is already graded with " + target.grade + ".\nDo you want to update the grade?",
                "Already Graded",
                JOptionPane.YES_NO_OPTION);
            
            if (choice != JOptionPane.YES_OPTION) return;
        }

        saveGrade(target, grade);
        
        JOptionPane.showMessageDialog(this, 
            "Grade successfully recorded for " + studentId + " in " + target.subject, 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);

        gradeField.setText("");
        
        loadStudentsFromSchedule();
        filterAndUpdateList();
    }

    private void saveGrade(StudentEntry s, float grade) {
        try {
            List<String> lines = new ArrayList<>();
            boolean found = false;

            if (s.gradesFile.exists()) {
                lines = Utils.readFile(s.gradesFile.getPath());
            }

            String encProfId = Utils.encryptEachField(profId);
            String encFullName = Utils.encryptEachField(fullName);
            String encSubject = Utils.encryptEachField(s.subject);
            String newLine = encProfId + "~" + encFullName + "~" + encSubject + "~" + grade;
        
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("~", -1);
                if (parts.length < 4) continue;

                if (parts[0].equals(encProfId) && parts[2].equals(encSubject)) {
                    lines.set(i, newLine);
                    found = true;
                    break;
                }
            }

            if (!found) {
                lines.add(newLine);
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(s.gradesFile))) {
                for (String line : lines) {
                    pw.println(line);
                }
            }

            s.grade = grade;

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error saving grade: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============ STUDENT ENTRY CLASS ============
    class StudentEntry {
        String studentId;
        String year;
        String program;
        String section;
        String lastName;
        String subject;
        File gradesFile;
        Float grade;

        StudentEntry(String id, String yr, String prog, String sec, String ln, String subj, File gf, Float gr) {
            studentId = id;
            year = yr;
            program = prog;
            section = sec;
            lastName = ln;
            subject = subj;
            gradesFile = gf;
            grade = gr;
        }
    }
}


    public class AssignmentsPanel extends JPanel {
    
        private JComboBox<String> yearCombo, programCombo, sectionCombo, subjectCombo;
        private JTextField titleField, deadlineField;
        private JTextArea descArea;
        private JButton postBtn;
        
        private String fullName;
        private List<String> subjects;
        private String profId;

        public AssignmentsPanel(String fullName, List<String> subjects, String profId) {
            this.fullName = fullName;
            this.subjects = subjects;
            this.profId = profId;
            
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            initTopSection();
            initFormSection();
        }

        private void initTopSection() {
            JPanel topPanel = new JPanel(new GridBagLayout());
            topPanel.setBackground(new Color(247, 240, 230));
            topPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(10, 15, 10, 15);
            c.anchor = GridBagConstraints.WEST;

            JLabel lblYear = label("Year Level:");
            JLabel lblProg = label("Program:");
            JLabel lblSec = label("Section:");
            JLabel lblSubj = label("Subject:");

            yearCombo = dropdown(new String[]{"1stYear", "2ndYear", "3rdYear", "4thYear"});
            programCombo = dropdown(new String[]{"BSCE", "BSEE", "BSME", "BSIE", "BTVTE", "BSBM", "BSE", "BSHM", "BSIT", "BSCS", "BSIS", "BSES", "BASLT", "BSA", "BFA", "BGT"});
            sectionCombo = dropdown(new String[]{"A", "B", "C", "D"});
            
            // Populate subjects from professor's subjects
            String[] subjectArray = subjects.toArray(new String[0]);
            subjectCombo = dropdown(subjectArray);

            c.gridx = 0; c.gridy = 0; topPanel.add(lblYear, c);
            c.gridx = 1; topPanel.add(yearCombo, c);
            c.gridx = 2; topPanel.add(lblProg, c);
            c.gridx = 3; topPanel.add(programCombo, c);
            
            c.gridx = 0; c.gridy = 1; topPanel.add(lblSec, c);
            c.gridx = 1; topPanel.add(sectionCombo, c);
            c.gridx = 2; topPanel.add(lblSubj, c);
            c.gridx = 3; topPanel.add(subjectCombo, c);

            add(topPanel, BorderLayout.NORTH);
        }

        private void initFormSection() {
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(new Color(247, 240, 230));
            formPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

            // Title
            JLabel lblTitle = label("Assignment Title:");
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            formPanel.add(lblTitle);
            formPanel.add(Box.createVerticalStrut(10));

            titleField = roundedInputField();
            titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            titleField.setAlignmentX(Component.LEFT_ALIGNMENT);
            formPanel.add(titleField);
            formPanel.add(Box.createVerticalStrut(20));

            // Description
            JLabel lblDesc = label("Description:");
            lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
            formPanel.add(lblDesc);
            formPanel.add(Box.createVerticalStrut(10));

            descArea = new JTextArea(6, 40);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            descArea.setBackground(Color.WHITE);
            descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane descScroll = new JScrollPane(descArea);
            descScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            formPanel.add(descScroll);
            formPanel.add(Box.createVerticalStrut(20));

            // Deadline
            JLabel lblDeadline = label("Deadline (YYYY-MM-DD):");
            lblDeadline.setAlignmentX(Component.LEFT_ALIGNMENT);
            formPanel.add(lblDeadline);
            formPanel.add(Box.createVerticalStrut(10));

            deadlineField = roundedInputField();
            deadlineField.setMaximumSize(new Dimension(300, 35));
            deadlineField.setAlignmentX(Component.LEFT_ALIGNMENT);
            formPanel.add(deadlineField);
            formPanel.add(Box.createVerticalStrut(30));

            // Post Button
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.setOpaque(false);
            btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            postBtn = new JButton("POST ASSIGNMENT");
            postBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            postBtn.setBackground(new Color(225, 210, 160));
            postBtn.setForeground(new Color(10, 20, 70));
            postBtn.setFocusPainted(false);
            postBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            postBtn.addActionListener(e -> postAssignment());

            btnPanel.add(postBtn);
            formPanel.add(btnPanel);
            formPanel.add(Box.createVerticalGlue());

            JScrollPane mainScroll = new JScrollPane(formPanel);
            mainScroll.setBorder(null);
            mainScroll.getVerticalScrollBar().setUnitIncrement(16);
            add(mainScroll, BorderLayout.CENTER);
        }

     private void postAssignment() {
    String year = (String) yearCombo.getSelectedItem();
    String program = (String) programCombo.getSelectedItem();
    String section = (String) sectionCombo.getSelectedItem();
    String subject = (String) subjectCombo.getSelectedItem();
    String title = titleField.getText().trim();
    String desc = descArea.getText().trim();
    String deadline = deadlineField.getText().trim();

    if (title.isEmpty() || desc.isEmpty() || deadline.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Find school year and college from professor's schedule
    String profSchedulePath = "data/FACULTY/FACULTY_MEMBERS/" + profId + "/" + profId + "_Schedule.txt";
    List<String> schedLines = Utils.readFile(profSchedulePath);
    
    String schoolYear = "";
    String college = "";
    boolean found = false;

    // Normalize inputs
    String targetYear = year.replace(" ", "").trim();
    String targetProg = program.trim();
    String targetSec = section.trim();

    for (String line : schedLines) {
        if(line.trim().isEmpty()) continue;
        
        String[] parts = line.split("\\|");
        
        if (parts.length >= 8) {
            String fileYear = parts[0].replace(" ", "").trim();
            String fileProg = parts[1].trim();
            String fileSec  = parts[2].trim();
            // Note: parts[3] is Subject, but we ignore it for directory lookup
            
            // CHECK ONLY: Year, Program, and Section
            if (fileYear.equalsIgnoreCase(targetYear) && 
                fileProg.equalsIgnoreCase(targetProg) && 
                fileSec.equalsIgnoreCase(targetSec)) {
                
                schoolYear = parts[6].trim(); 
                college = parts[7].trim();    
                found = true;
                break; // Found the section details, stop searching
            }
        }
    }

    if (!found || schoolYear.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Could not find matching section in schedule.\nPlease check if Year, Program, and Section are correct.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Construct path
    String yearDir = year.replace(" ", ""); 
    String syDir = schoolYear.replace(" ", "");

    String assignmentPath = "data/STUDENTS/" + syDir + "/" + yearDir + "/" + 
                        college.toUpperCase() + "/" + program.toUpperCase() + "/" + section + "/" + 
                        program.toUpperCase() + section + "_assignments.txt";

    Utils.ensureDir(new File(assignmentPath).getParent());

    // Write the assignment with the Subject name selected in the dropdown (e.g., "MAJOR")
    String line = fullName + "|" + subject + "|" + title + "|" + desc + "|" + deadline;
    Utils.appendToFile(assignmentPath, line);

    JOptionPane.showMessageDialog(this, "Assignment posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    
    // Clear fields
    titleField.setText("");
    descArea.setText("");
    deadlineField.setText("");
}
        private JLabel label(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lbl.setForeground(new Color(10, 20, 70));
            return lbl;
        }

        private JTextField roundedInputField() {
            JTextField tf = new JTextField();
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            tf.setBackground(Color.WHITE);
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            return tf;
        }

        private JComboBox<String> dropdown(String[] items) {
            JComboBox<String> cb = new JComboBox<>(items);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            cb.setBackground(new Color(245, 235, 220));
            cb.setPreferredSize(new Dimension(140, 35));
            return cb;
        }
    }


    private class ResignationPanel extends JPanel {
    
        private JTextArea reasonArea;
        private JButton submitBtn;

        public ResignationPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            initContent();
        }

        private void initContent() {
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(new Color(247, 240, 230));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            // Title
            JLabel titleLabel = new JLabel("Reason for Resignation");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(new Color(10, 20, 70));
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(titleLabel);
            contentPanel.add(Box.createVerticalStrut(20));

            // Text area
            reasonArea = new JTextArea(10, 50);
            reasonArea.setLineWrap(true);
            reasonArea.setWrapStyleWord(true);
            reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            reasonArea.setBackground(Color.WHITE);
            reasonArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JScrollPane scrollPane = new JScrollPane(reasonArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
            contentPanel.add(scrollPane);
            contentPanel.add(Box.createVerticalStrut(30));

            // Submit button
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.setOpaque(false);
            btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            submitBtn = new JButton("SUBMIT RESIGNATION");
            submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            submitBtn.setBackground(new Color(225, 210, 160));
            submitBtn.setForeground(new Color(10, 20, 70));
            submitBtn.setFocusPainted(false);
            submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            submitBtn.addActionListener(e -> submitResignation());

            btnPanel.add(submitBtn);
            contentPanel.add(btnPanel);
            contentPanel.add(Box.createVerticalGlue());

            JScrollPane mainScroll = new JScrollPane(contentPanel);
            mainScroll.setBorder(null);
            mainScroll.getVerticalScrollBar().setUnitIncrement(16);
            add(mainScroll, BorderLayout.CENTER);

            loadExisting();
        }

        private void loadExisting() {
            String filePath = "data/FACULTY/RESIGNATIONS/" + profId + "_resignation.txt";
            File f = new File(filePath);
            if (!f.exists()) return;
            
            List<String> lines = Utils.readFile(filePath);
            if (lines.isEmpty()) return;
            
            String[] parts = lines.get(0).split("\\|");
            if (parts.length >= 3) {
                reasonArea.setText(parts[2]);
                submitBtn.setEnabled(false);
                submitBtn.setText("ALREADY SUBMITTED");
            }
        }

        private void submitResignation() {
            String reason = reasonArea.getText().trim();
            
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a reason for resignation.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Utils.ensureDir("data/FACULTY/RESIGNATIONS");
            String filePath = "data/FACULTY/RESIGNATIONS/" + profId + "_resignation.txt";
            String line = profId + "|" + fullName + "|" + reason + "|Pending";
            
            Utils.writeFile(filePath, List.of(line));
            
            JOptionPane.showMessageDialog(this, "Resignation submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            submitBtn.setEnabled(false);
            submitBtn.setText("ALREADY SUBMITTED");
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
