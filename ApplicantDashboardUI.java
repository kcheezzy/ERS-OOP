import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;


public class ApplicantDashboardUI extends JFrame {

    private static final String BG_IMAGE = "src/dashboard.png";

    private String id, fullName, birthdate, address, email, phone, status;
    private String school, fathersName, mothersName, schoolYear, yearLevel;
    private String college, program, password;
    private String examDate, examTime, examRoom;
    private String applicantId, defaultPass;
    private int age;  
    private File programFolder;

    // Main content panel switching
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Underline Indicator
    JPanel underline;

   public ApplicantDashboardUI(
        String id,
        String fullName,
        String birthdate,
        int age,
        String address,
        String email,
        String phone,
        String status,
        String examDate,
        String examTime,
        String examRoom,
        String school,
        String fathersName,
        String mothersName,
        String schoolYear,
        String yearLevel,
        String college,
        String program
) {

    this.id = id;
    this.fullName = fullName;
    this.birthdate = birthdate;
    this.age = age;
    this.address = address;
    this.email = email;
    this.phone = phone;
    this.status = status;
    this.examDate = examDate;
    this.examTime = examTime;
    this.examRoom = examRoom;
    this.school = school;
    this.fathersName = fathersName;
    this.mothersName = mothersName;
    this.schoolYear = schoolYear;
    this.yearLevel = yearLevel;
    this.college = college;
    this.program = program;

    // Extract last name for default password
    String[] parts = fullName.split("\\s+");
    String surname = parts[parts.length - 1].toUpperCase();

    // Get MMDD from birthdate
    String digits = birthdate.replaceAll("[^0-9]", ""); 
    String bday = "0000";
    if (digits.length() >= 4) {
        bday = digits.substring(0, 4);  // MMDD
    }

    this.defaultPass = surname + bday;

    // Generate Student ID for PASSED status
    File programFolder = new File("data/APPLICANTS/" + yearLevel + "/"  + college + "/"  + program.replaceAll("\\s+", "_"));
        this.applicantId = generateStudentId(); // e.g., STU0001

    loadApplicantStatusFromFile();
    initUI();
}


    private String getApplicantFilePath() {
    // Build full path to applicant file
    return "data" + File.separator + "APPLICANTS" + File.separator
            + yearLevel + File.separator
            + college + File.separator
            + program.replaceAll("\\s+", "_") + "_APPLICANTS.txt";
}



    private String decrypt(String s) {
    StringBuilder result = new StringBuilder();
    for (char c : s.toCharArray()) {
        result.append((char)(c - 3)); // adjust sa actual encryption
    }
    return result.toString();
}


    private void loadApplicantStatusFromFile() {
    String filePath = getApplicantFilePath();
    File file = new File(filePath);

    // Default status
    status = "Pending";

    if (!file.exists()) {
        System.out.println("Applicant file not found: " + filePath);
        return;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        boolean found = false;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isBlank()) continue;

            String[] parts = line.split("\\|");

            // Decrypt ID from file
            String fileId = (parts.length > 0) ? decrypt(parts[0].trim()) : "N/A";

            System.out.println("Checking file line: " + line);
            System.out.println("File ID (decrypted): '" + fileId + "' vs Param ID: '" + id.trim() + "'");

            if (parts.length >= 19 && fileId.equalsIgnoreCase(id.trim())) {
                // Decrypt status as well
                status = decrypt(parts[18].trim());
                found = true;
                System.out.println("Matched ID! Status set to: " + status);
                break;
            }
        }

        if (!found) {
            System.out.println("No matching applicant found in file. Status remains Pending.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        status = "Pending";
    }
}


    private void initUI() {

        setTitle("Applicant Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ===== BACKGROUND =====
        BackgroundPanel bg = new BackgroundPanel(BG_IMAGE);
        bg.setLayout(null);
        setContentPane(bg);

        JLabel welcome = new JLabel("Welcome, " + fullName + " (" + id + ")");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 23));
        welcome.setForeground(new Color(40, 40, 40));
        welcome.setBounds(205, 80, 600, 30);
        bg.add(welcome);

        // Logout Button
        JLabel logout = new JLabel("LOG OUT");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logout.setForeground(Color.BLACK);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logout.setText("<html><u>LOG OUT</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logout.setText("LOG OUT");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new MainUI().setVisible(true);
            }
        });
        logout.setBounds(1650, 50, 120, 40);
        bg.add(logout);


        // ===== CENTER WHITE CARD =====
        RoundedPanel card = new RoundedPanel(25);
        card.setBounds(160, 200, 1585, 700);
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        bg.add(card);

        // ===== TABS PANEL =====
        JPanel tabPanel = new JPanel(null);
        tabPanel.setOpaque(false);
        tabPanel.setBounds(100, 20, 800, 50);
        card.add(tabPanel);

        Font tabFont = new Font("Segoe UI", Font.BOLD, 22);
        JLabel tab1 = new JLabel("Applicant Information");
        JLabel tab2 = new JLabel("Permit");
        JLabel tab3 = new JLabel("Status");
        JLabel[] tabs = {tab1, tab2, tab3};

        tab1.setFont(tabFont);
        tab2.setFont(tabFont);
        tab3.setFont(tabFont);

        tab1.setForeground(new Color(15, 36, 96)); // selected
        tab2.setForeground(Color.GRAY);
        tab3.setForeground(Color.GRAY);

        tab1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab3.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tab1.setBounds(0, 0, 330, 40);
        tab2.setBounds(350, 0, 200, 40);
        tab3.setBounds(550, 0, 200, 40);

        tabPanel.add(tab1);
        tabPanel.add(tab2);
        tabPanel.add(tab3);

        // ===== BLUE UNDERLINE =====
        underline = new JPanel();
        underline.setBackground(new Color(15, 36, 96)); 
        underline.setBounds(0, 42, 330, 4);  // Under tab1 by default
        tabPanel.add(underline);

        // Separator line
        JSeparator sep = new JSeparator();
        sep.setBounds(30, 80, 1520, 2);
        card.add(sep);

        // ===== CARD PANEL =====
        cardLayout = new CardLayout(); 
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBounds(30, 90, 1520, 580);
        cardPanel.setOpaque(false);

        // Add main panels
        cardPanel.add(applicantInfoPanel(), "INFO");
        generateExamPermit(); 
        cardPanel.add(permitPanel(), "PERMIT");

        // Only **one status panel** that adapts to the status
        JPanel statusPnl = getStatusPanel();
        cardPanel.add(statusPnl, "STATUS");

        // ===== AUTOMATIC SHOW BASED ON STATUS =====
        String key = (status == null || status.isBlank() || status.equalsIgnoreCase("Pending")) ? "INFO" : "STATUS";
        cardLayout.show(cardPanel, key);


        // Move underline if showing Status initially
        if (key.equals("STATUS")) {
            resetTabs(tabs);
            tab3.setForeground(new Color(15, 36, 96));
            moveUnderline(550, 70);
        }

        card.add(cardPanel);

        tab1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab1.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "INFO"); // Show applicant info
                moveUnderline(0, 230);
            }
        });

        tab2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab2.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "PERMIT");
                moveUnderline(350, 70);
            }
        });

        tab3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab3.setForeground(new Color(15, 36, 96));

                // Always show the only status panel
                cardLayout.show(cardPanel, "STATUS");

                moveUnderline(550, 70);
            }
        });


    setVisible(true);
}

    // ===== MOVE UNDERLINE =====
    private void moveUnderline(int x, int width) {
        underline.setBounds(x, 42, width, 4);
        underline.repaint();
    }

    private void resetTabs(JLabel[] tabs) {
        for (JLabel t : tabs) t.setForeground(Color.GRAY);
    }

    // ========== PANEL: INFO ================
    private JPanel applicantInfoPanel() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 28);
        Font infoFont = new Font("Segoe UI", Font.BOLD, 25);

        int cardWidth = 1585;

        // padding inside the white card
        int paddingLeft = 80;
        int paddingRight = 80;

        // desired number of columns
        int cols = 4;

        // compute usable width
        int usableWidth = cardWidth - paddingLeft - paddingRight;

        // compute width per column
        int colWidth = usableWidth / cols;

        // column X positions
        int lx1 = paddingLeft;
        int lx2 = paddingLeft + colWidth;
        int lx3 = paddingLeft + colWidth * 2;
        int lx4 = paddingLeft + colWidth * 3;

        // vertical positioning
        int y = 30;
        int gapRow = 85;  // adjust later if needed

        // Row 1
        p.add(createLabel("Full Name", lx1, y, labelFont));
        p.add(createInfo(fullName, lx1, y + 35, infoFont));

        // Row 2 
        y += gapRow;
        p.add(createLabel("Age", lx1, y, labelFont));
        p.add(createInfo(String.valueOf(age), lx1, y + 35, infoFont));

        p.add(createLabel("Birthdate", lx2, y, labelFont));
        p.add(createInfo(birthdate, lx2, y + 35, infoFont));

        p.add(createLabel("School", lx3, y, labelFont));
        p.add(createInfo(school, lx3, y + 35, infoFont));

        // Row 3
        y += gapRow;
        p.add(createLabel("Phone Number", lx1, y, labelFont));
        p.add(createInfo(phone, lx1, y + 35, infoFont));

        p.add(createLabel("Email Address", lx2, y, labelFont));
        p.add(createInfo(email, lx2, y + 35, infoFont));

        // Row 4
        y += gapRow;
        p.add(createLabel("Father's Name", lx1, y, labelFont));
        p.add(createInfo(fathersName, lx1, y + 35, infoFont));

        p.add(createLabel("Mother's Name", lx2, y, labelFont));
        p.add(createInfo(mothersName, lx2, y + 35, infoFont));

        // Row 5
        y += gapRow;
        p.add(createLabel("Address", lx1, y, labelFont));

        JTextArea addressArea = new JTextArea(address);
        addressArea.setFont(infoFont);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setEditable(false);
        addressArea.setOpaque(false);
        addressArea.setForeground(new Color(10, 20, 70)); 
        addressArea.setBounds(lx1, y + 35, 1200, 60); // adjust width/height
        p.add(addressArea);

        // Row 6
        y += gapRow;
        p.add(createLabel("School Year", lx1, y, labelFont));
        p.add(createInfo(schoolYear, lx1, y + 35, infoFont));

        p.add(createLabel("Year Level", lx2, y, labelFont));
        p.add(createInfo(yearLevel, lx2, y + 35, infoFont));

        p.add(createLabel("College", lx3, y, labelFont));
        p.add(createInfo(college, lx3, y + 35, infoFont));

        p.add(createLabel("Program", lx4, y, labelFont));
        p.add(createInfo(program, lx4, y + 35, infoFont));

        return p;
    }

private String generateStudentId() {
    int maxId = 0;
    
    // Get year from schoolYear (e.g., "2025-2026" -> "25")
    String yearPrefix = "";
    if (schoolYear != null && schoolYear.length() >= 4) {
        yearPrefix = schoolYear.substring(2, 4);
    }
    
    // Build prefix: NW-25
    String prefix = "NW-" + yearPrefix;
    
    System.out.println("🔍 Scanning for existing IDs with prefix: " + prefix);
    
    // 1. Check APPLICANT file
    String filePath = getApplicantFilePath();
    File file = new File(filePath);
    
    if (file.exists()) {
        List<String> lines = Utils.readFile(file.getPath());
        if (lines != null) {
            for (String enc : lines) {
                String dec = Utils.decryptEachField(enc);
                String[] p = dec.split("\\|", -1);
                if (p.length < 1) continue;
                
                String id = p[0].trim();
                if (id.startsWith(prefix)) {
                    try {
                        String numPart = id.substring(prefix.length() + 1);
                        int n = Integer.parseInt(numPart);
                        if (n > maxId) {
                            maxId = n;
                            System.out.println("  Found in applicants: " + id + " -> max = " + maxId);
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
    }
    
    // 2. Check ALL STUDENT folders recursively
    File studentsDir = new File("data/STUDENTS");
    if (studentsDir.exists()) {
        scanStudentFolders(studentsDir, prefix, maxId);
    }
    
    int next = maxId + 1;
    String newID = String.format("%s-%04d", prefix, next);
    System.out.println("✅ Generated new ID: " + newID);
    return newID;
}

// Helper method for recursive scanning
private int scanStudentFolders(File dir, String prefix, int currentMax) {
    int maxFound = currentMax;
    
    File[] files = dir.listFiles();
    if (files == null) return maxFound;
    
    for (File f : files) {
        if (f.isDirectory()) {
            String name = f.getName();
            
            // Check if folder name matches our prefix
            if (name.startsWith(prefix)) {
                try {
                    String numPart = name.substring(prefix.length() + 1);
                    int n = Integer.parseInt(numPart);
                    if (n > maxFound) {
                        maxFound = n;
                        System.out.println("  Found in folders: " + name + " -> max = " + maxFound);
                    }
                } catch (Exception ignored) {}
            }
            
            // Recursively scan subfolders
            maxFound = scanStudentFolders(f, prefix, maxFound);
        }
    }
    
    return maxFound;
}


    private JPanel statusPassedPanel() {

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(120, 0, 40, 0));

        // ==== Header ====
        JPanel passHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        passHeader.setOpaque(false);

        JLabel checkIcon = new JLabel("✔");
        checkIcon.setFont(new Font("SansSerif", Font.BOLD, 55));
        checkIcon.setForeground(new Color(0, 180, 80));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel passedLabel = new JLabel("Passed");
        passedLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        passedLabel.setForeground(new Color(0, 50, 90));

        JLabel statusText = new JLabel("Application Status");
        statusText.setFont(new Font("SansSerif", Font.PLAIN, 18));
        statusText.setForeground(new Color(70, 70, 70));

        textPanel.add(passedLabel);
        textPanel.add(statusText);

        passHeader.add(checkIcon);
        passHeader.add(textPanel);

        // ==== Message Banner ====
        JLabel msg = new JLabel("Congratulations! You have passed the evaluation.", SwingConstants.CENTER);
        msg.setOpaque(true);
        msg.setBackground(new Color(160, 235, 190));
        msg.setForeground(new Color(0, 70, 40));
        msg.setFont(new Font("SansSerif", Font.PLAIN, 24));
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setPreferredSize(new Dimension(600, 40));
        msg.setMaximumSize(new Dimension(600, 40));

        // ==== Question ====
        JLabel question = new JLabel("Would you like to proceed with your enrollment?");
        question.setFont(new Font("SansSerif", Font.PLAIN, 20));
        question.setForeground(new Color(20, 30, 60));
        question.setAlignmentX(Component.CENTER_ALIGNMENT);
        question.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        // ==== Buttons ====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);

        JButton proceedBtn = new JButton("Proceed with Enrollment");
        proceedBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        proceedBtn.setBackground(new Color(110, 220, 110));
        proceedBtn.setForeground(Color.WHITE);
        proceedBtn.setPreferredSize(new Dimension(260, 50));

        JButton declineBtn = new JButton("Decline the Offer");
        declineBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        declineBtn.setBackground(new Color(230, 60, 60));
        declineBtn.setForeground(Color.WHITE);
        declineBtn.setPreferredSize(new Dimension(260, 50));

        buttonPanel.add(proceedBtn);
        buttonPanel.add(declineBtn);

        proceedBtn.addActionListener(e -> {
            // CLEAR the question + buttons
            statusPanel.remove(question);
            statusPanel.remove(buttonPanel);

            // ===== Generate New Student ID =====
            String newStudentId = generateStudentId();

            // ✅ CRITICAL: SAVE STUDENT DATA IMMEDIATELY!
            boolean saved = saveStudentRecord(newStudentId);

            if (!saved) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error saving student record. Please contact admin.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // ==== NEW GREEN BOX ====
            JPanel greenBox = new JPanel();
            greenBox.setLayout(new BoxLayout(greenBox, BoxLayout.Y_AXIS));
            greenBox.setBackground(new Color(120, 220, 170));
            greenBox.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
            greenBox.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel thanks = new JLabel("✓ Thank you for accepting!");
            thanks.setFont(new Font("SansSerif", Font.PLAIN, 20));
            thanks.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sid = new JLabel("Student ID :   " + newStudentId);
            sid.setFont(new Font("SansSerif", Font.BOLD, 20));
            sid.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel pwd = new JLabel("Default Password :   " + defaultPass);
            pwd.setFont(new Font("SansSerif", Font.BOLD, 20));
            pwd.setAlignmentX(Component.CENTER_ALIGNMENT);

            greenBox.add(thanks);
            greenBox.add(Box.createVerticalStrut(15));
            greenBox.add(sid);
            greenBox.add(Box.createVerticalStrut(5));
            greenBox.add(pwd);

            statusPanel.add(Box.createVerticalStrut(20));
            statusPanel.add(greenBox);

            statusPanel.revalidate();
            statusPanel.repaint();
        });

        declineBtn.addActionListener(e -> {

            // Alisin yung question + buttons
            statusPanel.remove(question);
            statusPanel.remove(buttonPanel);

            // ==== RED BOX ====
            JPanel declineBox = new JPanel();
            declineBox.setLayout(new BoxLayout(declineBox, BoxLayout.Y_AXIS));
            declineBox.setBackground(new Color(255, 160, 160));  // soft red
            declineBox.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
            declineBox.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel declineMsg = new JLabel("We respect your decision. You may reapply in the next admission period.");
            declineMsg.setFont(new Font("SansSerif", Font.PLAIN, 20));
            declineMsg.setForeground(new Color(80, 20, 20));
            declineMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

            declineBox.add(declineMsg);

            statusPanel.add(Box.createVerticalStrut(20));
            statusPanel.add(declineBox);

            statusPanel.revalidate();
            statusPanel.repaint();
        });
        // ==== Add everything ====
        statusPanel.add(passHeader);
        statusPanel.add(Box.createVerticalStrut(25));
        statusPanel.add(msg);
        statusPanel.add(question);
        statusPanel.add(buttonPanel);

        return statusPanel;
    }

    private JPanel statusRejectedPanel() {

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(120, 0, 40, 0)); // SAME AS PASSED

        // ==== HEADER (X + Rejected Text) ====
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setOpaque(false);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel xIcon = new JLabel("✖");
        xIcon.setFont(new Font("SansSerif", Font.BOLD, 70));
        xIcon.setForeground(new Color(230, 60, 60));

        JLabel title = new JLabel("Rejected");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(20, 30, 60));
        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        header.add(xIcon);
        header.add(title);

        // Subtitle
        JLabel subtitle = new JLabel("Application Status");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(new Color(80, 80, 80));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 30, 0)); // same spacing style as PASSED

        // ===== PINK MESSAGE BOX =====
        JPanel msgPanel = new JPanel();
        msgPanel.setOpaque(true);
        msgPanel.setBackground(new Color(255, 150, 150));
        msgPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        msgPanel.setPreferredSize(new Dimension(900, 55));
        msgPanel.setMaximumSize(new Dimension(900, 55));
        msgPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel msg = new JLabel("<html><div style='text-align:center;'>"
                + "We regret to inform you that your application was not successful this time."
                + "</div></html>");

        msg.setFont(new Font("SansSerif", Font.PLAIN, 20));
        msg.setForeground(new Color(60, 20, 20));

        msgPanel.add(msg);

        // ==== ADD TO PANEL ====
        statusPanel.add(header);
        statusPanel.add(subtitle);
        statusPanel.add(msgPanel);

        return statusPanel;
    }

    private boolean saveStudentRecord(String studentID) {
    try {
        // Create student folder structure
        String folderPath = "data/STUDENTS/" + 
                          schoolYear + "/" +
                          yearLevel + "/" +
                          college + "/" +
                          program + "/" +
                          "A" + "/" +  // Default section A
                          studentID;
        
        File studentFolder = new File(folderPath);
        studentFolder.mkdirs();

        
        String[] nameParts = fullName.trim().split("\\s+");
        String lastName = nameParts.length > 0 ? nameParts[nameParts.length - 1] : "";
        String firstName = nameParts.length > 1 ? nameParts[0] : "";
        String middleInitial = nameParts.length > 2 ? nameParts[1].substring(0, 1) : "";

        String studentInfo = String.join("|",
            studentID,              // 0
            lastName,               // 1
            firstName,              // 2
            middleInitial,          // 3
            address,                // 4
            birthdate,              // 5
            String.valueOf(age),    // 6
            email,                  // 7
            phone,                  // 8
            fathersName,            // 9
            mothersName,            // 10
            school,                 // 11
            defaultPass,            // 12 - password
            schoolYear,             // 13
            yearLevel,              // 14
            college,                // 15
            program,                // 16
            "ENROLLED",             // 17 - status
            "A",                    // 18 - section (default A)
            "",                     // 19 - reserved
            "A"                     // 20 - section again (for compatibility)
        );

        // Encrypt the info
        String encrypted = Utils.encryptEachField(studentInfo);

        // Save to _info.txt
        File infoFile = new File(studentFolder, studentID + "_info.txt");
        Utils.writeRawFile(infoFile.getPath(), java.util.Collections.singletonList(encrypted));

        // Create empty grades file
        File gradesFile = new File(studentFolder, studentID + "_grades.txt");
        Utils.writeRawFile(gradesFile.getPath(), new java.util.ArrayList<>());

        // Create empty inbox file
        File inboxFile = new File(studentFolder, studentID + "_inbox.txt");
        Utils.writeRawFile(inboxFile.getPath(), new java.util.ArrayList<>());

        // Create empty notes file
        File notesFile = new File(studentFolder, studentID + "_notes.txt");
        Utils.writeRawFile(notesFile.getPath(), new java.util.ArrayList<>());

        // Create empty assignments file
        File assignmentsFile = new File(studentFolder, studentID + "_assignments.txt");
        Utils.writeRawFile(assignmentsFile.getPath(), new java.util.ArrayList<>());

        System.out.println("✅ Student saved: " + studentID + " at " + folderPath);
        return true;

    } catch (Exception ex) {
        ex.printStackTrace();
        return false;
    }
}

   private JPanel getStatusPanel() {
    if (status == null || status.isBlank() || status.equalsIgnoreCase("Pending")) {
        return createPendingPanel();
    }

    switch (status.toUpperCase()) {
        case "PASSED":
            return statusPassedPanel();
        case "REJECTED":
            return statusRejectedPanel();
        default:
            return createPendingPanel();
    }
}

private JPanel createPendingPanel() {
    JPanel p = new JPanel(null);
    p.setOpaque(false);

    JLabel pendingIcon = new JLabel("⏱");
    pendingIcon.setFont(new Font("SansSerif", Font.BOLD, 55));
    pendingIcon.setForeground(new Color(190, 160, 80));
    p.add(pendingIcon);
    pendingIcon.setBounds(600, 115, 100, 40);

    JLabel title = new JLabel("PENDING");
    title.setFont(new Font("Segoe UI", Font.BOLD, 32));
    title.setForeground(new Color(10, 20, 70));
    title.setBounds(660, 115, 400, 40);
    p.add(title);

    JLabel subtitle = new JLabel("Application Status");
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
    subtitle.setForeground(new Color(80, 80, 80));
    subtitle.setBounds(670, 165, 400, 30);
    p.add(subtitle);

    JPanel messageBox = new RoundedPanel(20);
    messageBox.setBackground(new Color(209, 188, 146));
    messageBox.setBounds(300, 300, 900, 50);
    messageBox.setLayout(new BorderLayout());

    JLabel msg = new JLabel("Your application is currently under review.", SwingConstants.CENTER);
    msg.setFont(new Font("Segoe UI", Font.PLAIN, 30));
    msg.setForeground(new Color(30, 30, 30));

    messageBox.add(msg);
    p.add(messageBox);

    return p;
}

    private void generateExamPermit() {
    if (examDate != null && !examDate.isBlank()) return; // already has permit

    Random rnd = new Random();
    examDate = "2025-12-" + (10 + rnd.nextInt(10));      // 10-19
    int hour = 8 + rnd.nextInt(3);                        // 8,9,10
    examTime = hour + ":00 AM";
    examRoom = "Room " + (100 + rnd.nextInt(50));
}


    // ---------- PERMIT PANEL ----------
    private JPanel permitPanel() {

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Color.WHITE);

        // Outer container
        JPanel permitContainer = new JPanel();
        permitContainer.setPreferredSize(new Dimension(900, 430));
        permitContainer.setBackground(new Color(250, 244, 235)); 
        permitContainer.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        permitContainer.setLayout(new BoxLayout(permitContainer, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("EXAM PERMIT");
        title.setFont(new Font("Poppins", Font.BOLD, 26));
        title.setForeground(new Color(6, 22, 59));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        permitContainer.add(title);

        permitContainer.add(Box.createVerticalStrut(25));

        // Fonts
        Font labelFont = new Font("Poppins", Font.PLAIN, 22);
        Font valueFont = new Font("Poppins", Font.BOLD, 20);

        // -------- GROUPED ID + NAME BLOCK --------
        JPanel idNameBlock = new JPanel();
        idNameBlock.setOpaque(false);
        idNameBlock.setLayout(new BoxLayout(idNameBlock, BoxLayout.Y_AXIS));
        idNameBlock.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblId = new JLabel("Applicant ID");
        lblId.setFont(labelFont);
        JLabel valId = new JLabel(applicantId);
        valId.setFont(valueFont);

        JLabel lblName = new JLabel("Applicant Name");
        lblName.setFont(labelFont);
        JLabel valName = new JLabel(fullName);
        valName.setFont(valueFont);

        idNameBlock.add(lblId);
        idNameBlock.add(valId);
        idNameBlock.add(Box.createVerticalStrut(10));
        idNameBlock.add(lblName);
        idNameBlock.add(valName);

        permitContainer.add(idNameBlock);

        // SMALL GAP BEFORE DATE/TIME/ROOM
        permitContainer.add(Box.createVerticalStrut(30));

        // -------- DATE / TIME / ROOM BLOCK (3 columns) --------
        JPanel bottomBlock = new JPanel(new GridLayout(1, 3, 40, 5));
        bottomBlock.setOpaque(false);

        bottomBlock.add(createLabelValueCell("Exam Date", examDate, labelFont, valueFont));
        bottomBlock.add(createLabelValueCell("Time", examTime, labelFont, valueFont));
        bottomBlock.add(createLabelValueCell("Room", examRoom, labelFont, valueFont));


        permitContainer.add(bottomBlock);

        // Center in main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        main.add(permitContainer, gbc);

        return main;
    }

    private JPanel createLabelValueCell(String labelText, String valueText, Font labelFont, Font valueFont) {
    JPanel cell = new JPanel();
    cell.setOpaque(false);
    cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
    
    JLabel lbl = new JLabel(labelText);
    lbl.setFont(labelFont);
    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    JLabel val = new JLabel(valueText);
    val.setFont(valueFont);
    val.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    cell.add(lbl);
    cell.add(val);
    return cell;
}


    private JLabel createLabel(String t, int x, int y, Font f) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setBounds(x, y, 400, 30);
        l.setForeground(new Color(70, 70, 70));
        return l;
    }

    private JLabel createInfo(String t, int x, int y, Font f) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setBounds(x, y, 700, 30);
        l.setForeground(new Color(10, 20, 70));
        return l;
    }

    class BackgroundPanel extends JPanel {
        BufferedImage img;
        BackgroundPanel(String path) {
            try { img = ImageIO.read(new File(path)); } catch (Exception ignored) {}
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        }
    }

    class RoundedPanel extends JPanel {
        int arc;
        RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }

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
