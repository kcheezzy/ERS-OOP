import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class AdminDashboardUI extends JFrame{

    private String adminID;
    private String adminName;

    private static final String BG_IMAGE = "src/dashboard_admin.png";
    private CardLayout cardLayout;
    private JPanel cardPanel;
    JPanel underline;
    private static final String FACULTY_DIR = "data/FACULTY";
    private static final String FACULTY_MASTER = FACULTY_DIR + File.separator + "ProfessorsMasterList.txt";
    private static String[] courses = {"BASALT","BFA", "BGT","BSA","BSBM","BSCE","BSCS","BSE","BSES",
                                        "BSEE","BSFT", "BSHM", "BSIE","BSIS","BSIT", "BSME","BTVTE"};
    private static String[] years = {"1stYear", "2ndYear", "3rdYear", "4thYear"};
    private static String[] school_year_global = {"2025-2026", "2026-2027", "2027-2028", "2028-2029", "2029-2030", "2030-2031", "2031-2032"};
    private static String[] department_global = {"CAFA", "CIE", "CIT", "CLA", "COE", "COS"};
    private static String[] section_global = {"A", "B"};
    Color lightBeige = new Color(234, 223, 207);
    Color dark= new Color(191, 175, 155);

    public AdminDashboardUI(String adminID, String adminName) {
        this.adminID = adminID;
        this.adminName = adminName;
        Admin.processResignation(); // keep whatever logic you need
        initAdminDashboardUI();     // your UI setup
    }
     public AdminDashboardUI() {
        Admin.processResignation();
        initAdminDashboardUI();
    }

    public void initAdminDashboardUI(){
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ===== BACKGROUND =====
        BackgroundPanel bg = new BackgroundPanel(BG_IMAGE);
        bg.setLayout(null);
        setContentPane(bg);

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
        card.setBounds(40, 180, 1850, 760);
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        bg.add(card);

        // ===== TABS PANEL =====
        JPanel tabPanel = new JPanel(null);
        tabPanel.setOpaque(false);
        tabPanel.setBounds(30, 20, 1900, 60);
        card.add(tabPanel);

        Font tabFont = new Font("Segoe UI", Font.BOLD, 20);
        JLabel tab1 = new JLabel("Applicants Management");
        JLabel tab2 = new JLabel("Resignation Approvals");
        JLabel tab3 = new JLabel("Course Schedule Generator");
        JLabel tab4 = new JLabel("Promotion");
        JLabel[] tabs = {tab1, tab2, tab3, tab4};

        tab1.setFont(tabFont);
        tab2.setFont(tabFont);
        tab3.setFont(tabFont);
        tab4.setFont(tabFont);

        tab1.setForeground(new Color(15, 36, 96)); // selected
        tab2.setForeground(Color.GRAY);
        tab3.setForeground(Color.GRAY);
        tab4.setForeground(Color.GRAY);

        tab1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab4.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tab1.setBounds(20, 10, 350, 40);
        tab2.setBounds(320, 10, 250, 40);
        tab3.setBounds(600, 10, 360, 40);
        tab4.setBounds(950, 10, 200, 40);

        tabPanel.add(tab1);
        tabPanel.add(tab2);
        tabPanel.add(tab3);
        tabPanel.add(tab4);
       
        underline = new JPanel();
        underline.setBackground(new Color(15, 36, 96)); 
        underline.setBounds(20, 52, 220, 4);  // Under tab1 by default
        tabPanel.add(underline);

        // Separator line
        JSeparator sep = new JSeparator();
        sep.setBounds(20, 80, 1810, 2);
        card.add(sep);
        
        // CardPanel setup
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBounds(20, 90, 1810, 640);
        cardPanel.setOpaque(false);

        cardPanel.add(AppManagement(), "Applicants");
        cardPanel.add(ReApp(), "Approvals");
        cardPanel.add(SchedManagement(), "Schedule");
        cardPanel.add(Promotion(), "Promotion");

        // Show Applicants panel by default
        cardLayout.show(cardPanel, "Applicants");

        card.add(cardPanel);
        
        tab1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab1.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "Applicants");
                Admin.setGuiCh("1");
                moveUnderline(20, 235);
            }
        });
        tab2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab2.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "Approvals");
                Admin.setGuiCh("2");
                moveUnderline(320, 215);
            }
        });
        tab3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab3.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "Schedule");
                Admin.setGuiCh("3");
                moveUnderline(600, 255);
            }
        });
        tab4.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab4.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "Promotion");
                Admin.setGuiCh("4");
                moveUnderline(950, 100);
            }
        });

        setVisible(true);
    }


    // ==================== APP MANAGEMENT PANEL ======================================
private JPanel AppManagement() {
    JPanel p = new JPanel(null);
    p.setOpaque(false);

    // ===== Fonts and Colors =====
    Font labelFont = new Font("Segoe UI", Font.PLAIN, 22);
    Font infoFont = new Font("Segoe UI", Font.BOLD, 24);
    Color lightBeige = new Color(245, 245, 220); // background for combo
    Color dark = new Color(0, 36, 96); // header color

    // ===== Labels =====
    JLabel lblProgram = createLabel("Program: ", 50, 30, infoFont);
    JLabel lblYear = createLabel("Year Level: ", 50, 80, infoFont);
    p.add(lblProgram);
    p.add(lblYear);

    // ===== Course Label =====
    final JLabel courseLabel = new JLabel("No selected course ");
    courseLabel.setForeground(new Color(70, 70, 70));
    courseLabel.setBounds(50, 130, 1750, 30); // width = card width - padding
    courseLabel.setFont(labelFont);
    p.add(courseLabel);

    // ===== Selected values =====
    final String[] selectedCourse = {""};
    final String[] selectedYear = {""};

    // ===== ComboBoxes =====
    String[] coursesWithPlaceholder = new String[courses.length + 1];
    coursesWithPlaceholder[0] = "Choose program";
    System.arraycopy(courses, 0, coursesWithPlaceholder, 1, courses.length);
    JComboBox<String> courseCombo = new JComboBox<>(coursesWithPlaceholder);
    courseCombo.setFont(new Font("Segoe UI", Font.ITALIC, 20));
    courseCombo.setBounds(200, 30, 200, 40);
    courseCombo.setBackground(lightBeige);
    p.add(courseCombo);

    String[] yearsWithPlaceholder = new String[years.length + 1];
    yearsWithPlaceholder[0] = "Choose year level";
    System.arraycopy(years, 0, yearsWithPlaceholder, 1, years.length);
    JComboBox<String> yearCombo = new JComboBox<>(yearsWithPlaceholder);
    yearCombo.setFont(new Font("Segoe UI", Font.ITALIC, 20));
    yearCombo.setBounds(200, 80, 200, 40);
    yearCombo.setBackground(lightBeige);
    p.add(yearCombo);

    // ===== Show All Button =====
    JButton showAllBtn = new JButton("Show All");
    showAllBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
    showAllBtn.setBounds(450, 55, 200, 40);
    showAllBtn.putClientProperty("JButton.buttonType", "roundRect");  
    showAllBtn.putClientProperty("JButton.style", "toolBar");
    showAllBtn.setBackground(new Color(0xd6ae5f));
    p.add(showAllBtn);

    // ===== Table Holder =====
    JPanel tableHolder = new JPanel(new BorderLayout());
    tableHolder.setBounds(50, 180, 1750, 400);
    tableHolder.setOpaque(false);
    p.add(tableHolder);

    // ===== Fetch Applicant Data =====
    List<File> applicantFiles = new ArrayList<>();
    Admin.findApplicantFiles(new File("data/APPLICANTS"), applicantFiles);

    List<Admin.ApplicantLine> guiList = new ArrayList<>();

    for (File f : applicantFiles) {
        List<String> lines = Utils.readFile(f.getPath());
        if (lines == null) continue;

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String dec = Utils.decryptEachField(line);
            String[] parts = dec.split("\\|", -1);
            if (parts.length < 19) continue;
            if (!parts[18].equalsIgnoreCase("PENDING")) continue;

            Admin.ApplicantLine a = new Admin.ApplicantLine();
            a.source = f;
            a.raw = line;
            a.p = parts;
            guiList.add(a);
        }
    }

    Admin.setGuiAppList(guiList);

    // ===== Initial Table =====
    JScrollPane scrollPane = ApplicantsCourseTable(guiList);
    tableHolder.add(scrollPane, BorderLayout.CENTER);

    JTable table = (JTable) scrollPane.getViewport().getView();
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    table.setRowSorter(sorter);

    // ===== Filter Logic =====
    Runnable updateFilter = () -> {
        List<RowFilter<Object,Object>> filters = new ArrayList<>();
        if (!selectedCourse[0].isEmpty()) filters.add(RowFilter.regexFilter(selectedCourse[0], 3));
        if (!selectedYear[0].isEmpty()) filters.add(RowFilter.regexFilter(selectedYear[0], 4));
        if (filters.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.andFilter(filters));
    };

    attachRowClickApproval(table, model, sorter, updateFilter);

    // ===== ComboBox ActionListeners =====
    courseCombo.addActionListener(e -> {
        String selected = (String) courseCombo.getSelectedItem();
        selectedCourse[0] = selected.equals("Choose program") ? "" : selected;
        courseLabel.setFont(selectedCourse[0].isEmpty() ? labelFont : infoFont);
        courseLabel.setText(selectedCourse[0].isEmpty() ? "No selected course" : "Applicants in " + selectedCourse[0] + ":");
        updateFilter.run();
    });

    yearCombo.addActionListener(e -> {
        String selected = (String) yearCombo.getSelectedItem();
        selectedYear[0] = selected.equals("Choose year level") ? "" : selected;
        updateFilter.run();
    });

    // ===== Show All Button Action =====
    showAllBtn.addActionListener(e -> {
        selectedCourse[0] = "";
        selectedYear[0] = "";
        courseCombo.setSelectedIndex(0);
        yearCombo.setSelectedIndex(0);
        courseLabel.setText("No selected course");
        courseLabel.setFont(labelFont);
        sorter.setRowFilter(null);
    });

    return p;
}


 

    //========================= DISPLAY TABLE PER COURSE ========================================
    static String getchfromui(){ return chGui;}
    private static void setchFromui(String id){ chGui = id;}
    private static String chGui;
    private static String IdFromUi = "";
    static void setIdFromUi(String x){ IdFromUi = x;}
    static String getIdfromui(){ return IdFromUi; }

    public JScrollPane ApplicantsCourseTable(List<Admin.ApplicantLine> applicantLines) {
        Color outlineColor = new Color(0xd6ae5f); 
        Color tableBg = lightBeige; 

        List<Admin.ApplicantLine> pendingApplicants = applicantLines.stream()
                .filter(a -> a.p[18].equalsIgnoreCase("PENDING"))
                .toList();

        String[] cols = {"ID", "Full Name", "College", "Program", "Year Level", "Status"};
        String[][] rows = new String[pendingApplicants.size()][cols.length];

        for (int i = 0; i < pendingApplicants.size(); i++) {
            Admin.ApplicantLine a = pendingApplicants.get(i);
            rows[i][0] = a.p[0]; 
            rows[i][1] = a.p[1] + " " + a.p[2] + " " + a.p[3]; 
            rows[i][2] = a.p[15]; 
            rows[i][3] = a.p[16]; 
            rows[i][4] = a.p[14]; 
            rows[i][5] = a.p[18]; 
        }

        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.getTableHeader().setBackground(dark);
        table.getTableHeader().setForeground(Color. WHITE);
        table.setBackground(tableBg);
        table.setShowGrid(true);           
        table.setGridColor(Color.BLACK);   
        table.setFillsViewportHeight(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus,
                                                        int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                if (status.equalsIgnoreCase("Passed")) c.setForeground(new Color(0,128,0));
                else if (status.equalsIgnoreCase("Failed")) c.setForeground(Color.RED);
                else c.setForeground(Color.BLACK); // PENDING
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(dark, 3, true));
        scroll.getViewport().setBackground(tableBg);
        
        return scroll;
    }

                
    private void attachRowClickApproval(JTable table, DefaultTableModel model, TableRowSorter<DefaultTableModel> sorter, Runnable refreshFilter) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    int modelRow = table.convertRowIndexToModel(row);
                    String id = model.getValueAt(modelRow, 0).toString();
                    String name = model.getValueAt(modelRow, 1).toString();
                    setIdFromUi(id);
                    Admin.settextGuiExist(true);
                    int choice = JOptionPane.showOptionDialog(
                        null,
                        "Do you want to Approve or Decline " + name + "?",
                        "Applicant Decision",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Approve", "Reject"},
                        "Approve"
                    );

                    if (choice == 0) {
                        setchFromui("A");
                        model.setValueAt("Passed", modelRow, 5);
                    } else if (choice == 1) {
                        setchFromui("R");
                        model.setValueAt("Failed", modelRow, 5);
                    }
                    
                    Admin.applicantsMenu();
                    // Refresh filter so updated status may remain visible
                    if (refreshFilter != null) refreshFilter.run();
                }
            }
        });
    }

    // ==================== RESIGNATION APPROVALS PANEL ==============================
private JPanel ReApp() {
    JPanel p = new JPanel(null);
    p.setOpaque(false);

    Font infoFont = new Font("Segoe UI", Font.BOLD, 22);
    Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
    Font headerFont = new Font("Segoe UI", Font.BOLD, 20);
    Font cellFont = new Font("Segoe UI", Font.PLAIN, 18);

    // ===== Table Container =====
    RoundedPanel tableContainer = new RoundedPanel(25);
    tableContainer.setBounds(50, 50, 1750, 490); // fit sa white card with padding
    tableContainer.setBackground(dark); // dark
    tableContainer.setLayout(null);
    p.add(tableContainer);

    // ===== Table =====
    String[] columnNames = {"Faculty ID", "Full Name", "Reason", "Status"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };

        JTable table = new JTable(model);
        table.setFont(cellFont);
        table.setRowHeight(30);
        table.getTableHeader().setFont(headerFont);
        table.setBackground(lightBeige); 
        table.setForeground(Color.BLACK);
        table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(dark, 1));
        table.getTableHeader().setBackground(dark);
        table.getTableHeader().setForeground(Color. WHITE);
        table.setShowGrid(true);           
        table.setGridColor(Color.BLACK);   

    // ===== CENTER ALL CELLS =====
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    
    // Apply to all columns
    for (int i = 0; i < columnNames.length; i++) {
        table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    // ===== Scroll Pane =====
    JScrollPane scroll = new JScrollPane(table);
    scroll.setBounds(12, 12, tableContainer.getWidth() - 24, tableContainer.getHeight() - 24); 
    scroll.setBorder(BorderFactory.createLineBorder(new Color(0,36,96), 1));
    tableContainer.add(scroll);

    // ===== Table Row Click =====
    table.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = table.getSelectedRow();
            if (row < 0) return;

            String id = model.getValueAt(row, 0).toString();
            String name = model.getValueAt(row, 1).toString();
            String reason = model.getValueAt(row, 2).toString();

            int choice = JOptionPane.showOptionDialog(
                null,
                "Faculty: " + name + "\nReason: " + reason + "\n\nApprove this resignation?",
                "Resignation Decision",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Approve", "Reject", "Cancel"},
                "Approve"
            );

            if (choice == 0) { 
                updateResignationStatus(id, "Accepted");
                model.setValueAt("Accepted", row, 3);
            } 
            else if (choice == 1) { 
                updateResignationStatus(id, "Rejected");
                model.setValueAt("Rejected", row, 3);
            }
        }
    });

    // ===== Load Data =====
    loadResignationTable(model);

    return p;
}


    private void loadResignationTable(DefaultTableModel model) {
        List<String> list = Admin.getResList();
        model.setRowCount(0);

        for (String enc : list) {
            if (enc.trim().isEmpty()) continue;

            String dec = Utils.decryptEachField(enc);
            String[] p = dec.split("\\|", -1);

            String id = p[0];
            String name = p[1];
            String reason = p[2];
            String status = p[3];

            model.addRow(new Object[]{ id, name, reason, status });
        }
    }

    private void updateResignationStatus(String id, String newStatus) {
        try {
            List<String> lines = Admin.getResList(); // your stored GUI list
            List<String> updated = new ArrayList<>();

            for (String enc : lines) {
                String dec = Utils.decryptEachField(enc);
                String[] p = dec.split("\\|", -1);

                if (p[0].equals(id)) {
                    p[3] = newStatus; // update status
                    updated.add(Utils.encryptEachField(String.join("|", p)));
                } else {
                    updated.add(enc);
                }
            }

            // Overwrite file
            File folder = new File(Admin.RESIGN_FOLDER);
            File f = new File(folder, id + "_resignation.txt");
            Utils.writeRawFile(f.getPath(), updated);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ======================= SCHEDULE MANAGEMENT ==============================
private JPanel SchedManagement() {
    JPanel p = new JPanel(null);
    p.setOpaque(false);

    Font infoFont = new Font("Segoe UI", Font.BOLD, 22);
    Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
    Font buttonFont = new Font("Segoe UI", Font.PLAIN, 22);
    Color colorClicked = new Color(69, 56, 42);
    Color defaultColor = Color.WHITE;

    // ===== Buttons Panel =====
    RoundedPanel buttonsPanel = new RoundedPanel(25);
    buttonsPanel.setBounds(50, 50, 300, 650); // full height with padding
    buttonsPanel.setBackground(lightBeige); // white card style
    p.add(buttonsPanel);

    // ===== Generate Panel =====
    RoundedPanel genPanel = new RoundedPanel(25);
    genPanel.setBounds(400, 50, 1450, 650); // occupy rest of card width
    genPanel.setBackground(lightBeige);
    genPanel.setLayout(null);
    genPanel.setVisible(true);
    p.add(genPanel);

    // ===== Check Request Panel =====
    RoundedPanel checkReqPanel = new RoundedPanel(25);
    checkReqPanel.setBounds(400, 50, 1450, 650);
    checkReqPanel.setBackground(lightBeige);
    checkReqPanel.setLayout(null);
    checkReqPanel.setVisible(false);
    p.add(checkReqPanel);

    // ===== Buttons =====
    JButton generateButton = new JButton("Generate schedule");
    generateButton.setFont(buttonFont);
    generateButton.setBounds(10, 30, 280, 100);
    generateButton.putClientProperty("JButton.buttonType", "roundRect");  
    generateButton.putClientProperty("JButton.style", "toolBar");
    generateButton.setBackground(defaultColor);
    buttonsPanel.add(generateButton);

    JButton checkButton = new JButton("Check Request");
    checkButton.setFont(buttonFont);
    checkButton.setBounds(10, 150, 280, 100);
    checkButton.putClientProperty("JButton.buttonType", "roundRect");  
    checkButton.putClientProperty("JButton.style", "toolBar");
    checkButton.setBackground(defaultColor);
    buttonsPanel.add(checkButton);

    // ===== Button Actions =====
    generateButton.addActionListener(e -> {
        genPanel.setVisible(true);
        generateButton.setBackground(colorClicked);
        checkButton.setBackground(defaultColor);
        checkReqPanel.setVisible(false);

        genPanel.removeAll();      
        JPanel p1 = genSchedule();
        genPanel.add(p1);          
        genPanel.repaint();
        genPanel.revalidate();
    });

    checkButton.addActionListener(e -> {
        genPanel.setVisible(false);
        checkButton.setBackground(colorClicked);
        generateButton.setBackground(defaultColor);
        checkReqPanel.setVisible(true);

        checkReqPanel.removeAll();
        JPanel p2 = checkReq();
        checkReqPanel.add(p2);
        checkReqPanel.repaint();
        checkReqPanel.revalidate();
    });

    return p;
}

    private static String year_Level = "";
    private static String program = "";
    private static String acadYear = "";
    private static String department = "";
    private static String section = "";

    private JPanel genSchedule(){
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        p.setBounds(0, 0, 1450, 600);
        p.setLayout(null);
        Font infoFont = new Font("Segoe UI", Font.BOLD, 28);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 22);

        // ===== FORM GRID CONSTANTS =====
        int leftLabelX  = 260;
        int leftFieldX  = 430;

        int rightLabelX = 720;
        int rightFieldX = 900;

        int row1Y = 80;
        int rowGap = 90;

        int row2Y = row1Y + rowGap;
        int row3Y = row2Y + rowGap;

        int fieldYOffset = -5;   // para centered ang button vs label text

       // LEFT COLUMN
        p.add(createLabel("Year Level:",  leftLabelX,  row1Y, infoFont));
        p.add(createLabel("Department:", leftLabelX,  row1Y + rowGap, infoFont));
        p.add(createLabel("Section:",    leftLabelX,  row1Y + rowGap * 2, infoFont));

        // RIGHT COLUMN
        p.add(createLabel("Program:",       rightLabelX, row1Y, infoFont));
        p.add(createLabel("Academic Year:", rightLabelX, row1Y + rowGap, infoFont));

        JButton courseButton = new JButton("Choose year level");
        courseButton.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        courseButton.setBounds(leftFieldX, row1Y + fieldYOffset, 200, 40);
        courseButton.putClientProperty("JButton.buttonType", "roundRect");  
        courseButton.putClientProperty("JButton.style", "toolBar");
        courseButton.setBackground(Color.WHITE);
        p.add(courseButton);

        JList<String> yearLevelList = new JList<>(years);
        yearLevelList.setBackground(Color.WHITE);
        yearLevelList.setForeground(Color.BLACK);
        yearLevelList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearLevelList.setSelectionBackground(new Color(15,36,96));
        yearLevelList.setSelectionForeground(Color.WHITE);
        yearLevelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu yearLevelPopup = new JPopupMenu();
        yearLevelPopup.add(new JScrollPane(yearLevelList));

        courseButton.addActionListener(e -> {
            yearLevelPopup.show(courseButton, 0, courseButton.getHeight());
        });

        yearLevelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = yearLevelList.getSelectedValue();
                if (selected != null) {
                    switch(selected){
                        case "1stYear": year_Level = "1"; break;
                        case "2ndYear": year_Level = "2"; break;
                        case "3rdYear": year_Level = "3"; break;
                        case "4thYear": year_Level = "4"; break;
                    }          
                    courseButton.setText(selected);  
                    yearLevelPopup.setVisible(false);
                }
            }
        });

        // ===================== PROGRAM =====================
        JButton progBtn = new JButton("Choose course");
        progBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        progBtn.setBounds(rightFieldX, row1Y + fieldYOffset, 220, 40);
        progBtn.putClientProperty("JButton.buttonType", "roundRect");  
        progBtn.putClientProperty("JButton.style", "toolBar");
        progBtn.setBackground(Color.WHITE);
        p.add(progBtn);

        JList<String> programList = new JList<>(courses);
        programList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPopupMenu programPopup = new JPopupMenu();
        programPopup.add(new JScrollPane(programList));

        progBtn.addActionListener(e -> programPopup.show(progBtn, 0, progBtn.getHeight()));
        programList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                program = programList.getSelectedValue();
                progBtn.setText(program);
                programPopup.setVisible(false);
            }
        });
        // ===================== ACADEMIC YEAR =====================
        JButton ayBtn = new JButton("Choose AY");
        ayBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        ayBtn.setBounds(rightFieldX, row1Y + rowGap + fieldYOffset, 220, 40);
        ayBtn.putClientProperty("JButton.buttonType", "roundRect");  
        ayBtn.putClientProperty("JButton.style", "toolBar");
        ayBtn.setBackground(Color.WHITE);
        p.add(ayBtn);

        JList<String> ayList = new JList<>(school_year_global);
        ayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPopupMenu ayPopup = new JPopupMenu();
        ayPopup.add(new JScrollPane(ayList));

        ayBtn.addActionListener(e -> ayPopup.show(ayBtn, 0, ayBtn.getHeight()));
        ayList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                acadYear = ayList.getSelectedValue();
                ayBtn.setText(acadYear);
                ayPopup.setVisible(false);
            }
        });

        // ===================== DEPARTMENT =====================
        JButton deptBtn = new JButton("Choose department");
        deptBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        deptBtn.setBounds(leftFieldX, row1Y + rowGap + fieldYOffset, 220, 40);
        deptBtn.putClientProperty("JButton.buttonType", "roundRect");  
        deptBtn.putClientProperty("JButton.style", "toolBar");
        deptBtn.setBackground(Color.WHITE);
        p.add(deptBtn);

        JList<String> deptList = new JList<>(department_global);
        deptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPopupMenu deptPopup = new JPopupMenu();
        deptPopup.add(new JScrollPane(deptList));

        deptBtn.addActionListener(e -> deptPopup.show(deptBtn, 0, deptBtn.getHeight()));
        deptList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                department = deptList.getSelectedValue();
                deptBtn.setText(department);
                deptPopup.setVisible(false);
            }
        });

        // ===================== SECTION =====================
        JButton secBtn = new JButton("Choose section");
        secBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        secBtn.setBounds(leftFieldX, row3Y + fieldYOffset, 220, 40);
        secBtn.putClientProperty("JButton.buttonType", "roundRect");  
        secBtn.putClientProperty("JButton.style", "toolBar");
        secBtn.setBackground(Color.WHITE);
        p.add(secBtn);;

        JList<String> secList = new JList<>(section_global);
        secList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPopupMenu secPopup = new JPopupMenu();
        secPopup.add(new JScrollPane(secList));

        secBtn.addActionListener(e -> secPopup.show(secBtn, 0, secBtn.getHeight()));
        secList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                section = secList.getSelectedValue();
                secBtn.setText(section);
                secPopup.setVisible(false);
            }
        });
        // ============== GENERATE BUTTON ================
        JButton generateBtn = new JButton("Generate Schedule");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        generateBtn.setBounds(520, row1Y + rowGap * 3 + 30, 420, 55);
        generateBtn.setBackground(new Color(69, 56, 42));
        generateBtn.setForeground(Color.WHITE);
        p.add(generateBtn);

        generateBtn.addActionListener(e -> {
            if (year_Level.isEmpty() || program.isEmpty() || department.isEmpty() || acadYear.isEmpty() || section.isEmpty()) {
                JOptionPane.showMessageDialog(p, "Please fill all fields before generating schedule.", "Incomplete Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                String msg = String.format("Generating schedule for:\nYear Level: %s\nProgram: %s\nDepartment: %s\nAcademic Year: %s\nSection: %s",
                        year_Level, program, department, acadYear, section);
                Admin admin = new Admin();
                admin.setFields(acadYear, year_Level, department, program, section);
                Admin.generateSchedules();

                JOptionPane.showMessageDialog(p, msg, "Schedule Generated", JOptionPane.INFORMATION_MESSAGE);
                
            }
        });

        return p;
    }

    private static int numToAdmin;
    private static int ch;
    private static void setNumToAdmin(int x){ numToAdmin = x; }
    private static void setCh(int x){ ch = x; }
    static int getNumToGui(){ return numToAdmin; }
    static int getCh(){ return ch; }
    private static final String COURSE_REQUESTS_DIR = FACULTY_DIR + File.separator + "COURSE_REQUESTS";
    
    private JPanel checkReq(){
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        p.setBounds(0, 0, 950, 450);
        p.setLayout(null);
        Font infoFont = new Font("Segoe UI", Font.BOLD, 20);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 22);
        
        String filePath = COURSE_REQUESTS_DIR + "/course_request.txt";
        List<String> list = Utils.readFile(filePath);
        List<String> profnamelist = Utils.readFile(FACULTY_MASTER);
        
        DefaultTableModel model = new DefaultTableModel(new String[]{"Faculty ID", "Name", "Status"}, 0);

        for (String s : list) {
            String[] parts = s.split("\\|");
            String profId = parts[0].trim();
            String reason = parts[2].trim();
            String status = "";
            String name = parts[1].trim();;
            if(reason.equals("APPROVED")){
                status = "APPROVED";
            }
            if(reason.equals("REJECTED")){
                status = "REJECTED";
            }
            else{
                status = "PENDING";
            }
            model.addRow(new Object[]{profId, name, status});
        }

        JTable table = new JTable(model);
        table.setFont(labelFont);
        table.setRowHeight(30);
        table.getTableHeader().setFont(infoFont);
        table.setBackground(lightBeige); 
        table.setForeground(Color.BLACK);
        table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(dark, 1));
        table.getTableHeader().setBackground(dark);
        table.getTableHeader().setForeground(Color. WHITE);
        table.setShowGrid(true);           
        table.setGridColor(Color.BLACK);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 20, 900, 400);  
        p.add(sp);   

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                String rawLine = list.get(row);
                String[] parts = rawLine.split("\\|");
                if (parts.length < 4) {
                    System.out.println("Malformed request.");
                    return;
                }

                String profId = parts[0].trim();
                String reason = "";
                List<String> reqlist = Utils.readFile(filePath);
                for(String l : reqlist){
                    String pr[] = l.split("\\|");
                    String n = pr[0];
                    if(profId.equals(n)){
                        reason = pr[2];
                    }
                }

                String[] options = {"Approve", "Reject"};

                int choice = JOptionPane.showOptionDialog(
                        null,
                        "Reason: " + reason,
                        "Requested by " + profId,
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );
                if (choice == 0) {
                    System.out.println("Approved: " + rawLine);
                    model.setValueAt("APPROVED", row, 2);
                    setCh(1);
                    boolean success = Admin.approveAndCreateSchedule(profId);

                    if (success) {
                        Admin.updateRequestStatus(profId, "APPROVED");
                        System.out.println("Request APPROVED. Schedule created.");
                    } else {
                        Admin.updateRequestStatus(profId, "REJECTED");
                        System.out.println("Request REJECTED. No students found or conflict detected.");
                    }
                    Admin.setChoiceFromGui(1);
                } else if (choice == 1) {
                    System.out.println("Rejected: " + rawLine);
                    model.setValueAt("REJECTED", row, 2);
                    setCh(0);
                    boolean success = Admin.approveAndCreateSchedule(profId);

                    if (success) {
                        Admin.updateRequestStatus(profId, "APPROVED");
                        System.out.println("Request APPROVED. Schedule created.");
                    } else {
                        Admin.updateRequestStatus(profId, "REJECTED");
                        System.out.println("Request REJECTED. No students found or conflict detected.");
                    }
                    Admin.setChoiceFromGui(1);
                }
                setNumToAdmin(row);
                //Admin.viewReq();
            }
        });


        return p;
    }

   private JPanel Promotion() {

    // ================= ROOT PANEL =================
    JPanel root = new JPanel(new BorderLayout(20, 20));
    root.setOpaque(false);
    root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    Font headerFont = new Font("Segoe UI", Font.BOLD, 20);
    Font cellFont = new Font("Segoe UI", Font.PLAIN, 18);

    // ================= TABLE MODEL =================
    String[] columnNames = {"Student ID", "Year Level", "Program", "Last Name", "GWA", "Remarks"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    JTable table = new JTable(model);
    table.setFont(cellFont);
    table.setRowHeight(32);
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    table.getTableHeader().setFont(headerFont);
    table.getTableHeader().setBackground(dark);
    table.getTableHeader().setForeground(Color.WHITE);

    TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
    table.setRowSorter(sorter);

    // ================= FILTER PANEL =================
    JPanel filterPanel = new JPanel(new GridBagLayout());
    filterPanel.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 10, 5, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;


    JCheckBox selectAll = new JCheckBox("Select All");
    selectAll.setFont(new Font("Segoe UI", Font.PLAIN, 24));
    selectAll.setOpaque(false);

    JComboBox<String> yrCombo = new JComboBox<>(years);
    JComboBox<String> progCombo = new JComboBox<>(courses);
    JComboBox<String> sectionCombo = new JComboBox<>(section_global);
    JComboBox<String> acadYearCombo = new JComboBox<>(school_year_global);

    gbc.gridx = 0; filterPanel.add(yrCombo, gbc);
    gbc.gridx = 1; filterPanel.add(progCombo, gbc);
    gbc.gridx = 2; filterPanel.add(sectionCombo, gbc);
    gbc.gridx = 3; filterPanel.add(acadYearCombo, gbc);
    gbc.gridx = 4; gbc.weightx = 0; filterPanel.add(selectAll, gbc);

    Font comboFont = new Font("Segoe UI", Font.PLAIN, 24);

    yrCombo.setFont(comboFont);
    progCombo.setFont(comboFont);
    sectionCombo.setFont(comboFont);
    acadYearCombo.setFont(comboFont);
    
    yrCombo.setBackground(dark); // kulay ng box
    yrCombo.setForeground(Color.WHITE);           // kulay ng text
    yrCombo.setFont(new Font("Segoe UI", Font.PLAIN, 24));

    progCombo.setBackground(dark);
    progCombo.setForeground(Color.WHITE);
    progCombo.setFont(new Font("Segoe UI", Font.PLAIN, 24));

    sectionCombo.setBackground(dark);
    sectionCombo.setForeground(Color.WHITE);
    sectionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 24));

    acadYearCombo.setBackground(dark);
    acadYearCombo.setForeground(Color.WHITE);
    acadYearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 24));


    selectAll.addActionListener(e -> {
        if (selectAll.isSelected()) table.selectAll();
        else table.clearSelection();
    });

   yrCombo.addActionListener(e -> {
    String value = (String) yrCombo.getSelectedItem();
    Admin.setSchoolYear(value);
    if (Admin.getAcademicYear() != null)
        loadStudentsIntoTable(model);
    });

    progCombo.addActionListener(e -> {
        String value = (String) progCombo.getSelectedItem();
        Admin.setPromotechoicefromgui(value);
        if (Admin.getAcademicYear() != null)
            loadStudentsIntoTable(model);
    });

    sectionCombo.addActionListener(e -> {
        String value = (String) sectionCombo.getSelectedItem();
        Admin.setPromotiontype(value);
        if (Admin.getAcademicYear() != null)
            loadStudentsIntoTable(model);
    });

    acadYearCombo.addActionListener(e -> {
        String value = (String) acadYearCombo.getSelectedItem();
        Admin.setAcademicYear(value);
        loadStudentsIntoTable(model);
    });


    root.add(filterPanel, BorderLayout.NORTH);

    // ================= TABLE CONTAINER =================
    RoundedPanel tableContainer = new RoundedPanel(25);
    tableContainer.setBackground(dark);
    tableContainer.setLayout(new BorderLayout(10, 10));
    tableContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JScrollPane scrollPane = new JScrollPane(table);
    tableContainer.add(scrollPane, BorderLayout.CENTER);

    root.add(tableContainer, BorderLayout.CENTER);

    // ================= ACTION BUTTONS =================
    JButton promoteBtn = new JButton("PROMOTE");
    promoteBtn.setBackground(new Color(80, 180, 80));
    promoteBtn.setForeground(Color.WHITE);
    promoteBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));

    JButton rejectBtn = new JButton("REJECT");
    rejectBtn.setBackground(new Color(200, 60, 60));
    rejectBtn.setForeground(Color.WHITE);
    rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
    actionPanel.setOpaque(false);
    actionPanel.add(promoteBtn);
    actionPanel.add(rejectBtn);

    root.add(actionPanel, BorderLayout.SOUTH);

    
    // ================= BUTTON LOGIC =================
    promoteBtn.addActionListener(e -> promoteStudents(table, model));
    rejectBtn.addActionListener(e -> rejectStudents(table, model));

    return root;
}

    private void promoteStudents(JTable table, DefaultTableModel model) {
    int[] rows = table.getSelectedRows();
    if (rows.length == 0) {
        JOptionPane.showMessageDialog(null, "Select student(s) to promote.");
        return;
    }

    String[] options = {"Regular Promotion", "Irregular Promotion"};
    int choice = JOptionPane.showOptionDialog(
            null,
            "Choose promotion type:",
            "Promotion Type",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
    );

    if (choice == -1) return;

    new Thread(() -> {
        String[] parts = Admin.getAcademicYear().split("-");
        String nextSY = (Integer.parseInt(parts[0]) + 1) + "-" + (Integer.parseInt(parts[1]) + 1);

        for (int r : rows) {
            String studentID = table.getValueAt(r, 0).toString();
            File studentFolder = new File("data/STUDENTS/" + Admin.getAcademicYear() + "/" + studentID);

            if (choice == 0) {
                Admin.regularPromote(studentFolder, Admin.getAcademicYear(), nextSY);
            } else {
                String targetSY = JOptionPane.showInputDialog("Enter target School Year:");
                String targetYear = JOptionPane.showInputDialog("Enter target Year Level:");
                if (targetSY != null && targetYear != null)
                    Admin.irregularPromote(
                            studentFolder,
                            Admin.getSchoolYear(),
                            targetSY.trim(),
                            targetYear.trim().replace(" ", "")
                    );
            }
        }

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Selected students promoted!");
            loadStudentsIntoTable(model);
        });
    }).start();
}

    private void rejectStudents(JTable table, DefaultTableModel model) {
    int[] rows = table.getSelectedRows();
    if (rows.length == 0) {
        JOptionPane.showMessageDialog(null, "Select student(s) to reject.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to reject the selected student(s)?",
            "Confirm Reject",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    new Thread(() -> {
        for (int r : rows) {
            String studentID = table.getValueAt(r, 0).toString();
            File studentFolder = new File("data/STUDENTS/" + Admin.getAcademicYear() + "/" + studentID);
            if (!studentFolder.exists()) continue;

            File dropFolder = new File("data/STUDENTS/ARCHIVES/DROPPED_STUDENTS/" + studentID);
            dropFolder.mkdirs();

            Admin.moveFiles(studentFolder, dropFolder);
            Admin.deleteFolder(studentFolder);
        }

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Selected student(s) rejected!");
            loadStudentsIntoTable(model);
        });
    }).start();
}


    private JButton createFilterButton(String text) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Segoe UI", Font.ITALIC, 18));
    btn.putClientProperty("JButton.buttonType", "roundRect");
    btn.putClientProperty("JButton.style", "toolBar");
    btn.setBackground(lightBeige);
    return btn;
}

    private void setupPopupList(JButton button, String[] data, Consumer<String> onSelect) {
    JList<String> list = new JList<>(data);
    list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    list.setSelectionBackground(new Color(15, 36, 96));
    list.setSelectionForeground(Color.WHITE);

    JPopupMenu popup = new JPopupMenu();
    popup.add(new JScrollPane(list));

    button.addActionListener(e ->
            popup.show(button, 0, button.getHeight())
    );

    list.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            String value = list.getSelectedValue();
            popup.setVisible(false);
            onSelect.accept(value);
        }
    });
}


    // ================= HELPER METHOD TO LOAD STUDENTS =================
    private void loadStudentsIntoTable(DefaultTableModel model) {
    model.setRowCount(0);

    if (Admin.getAcademicYear() == null) return;

    File baseDir = new File("data/STUDENTS/" + Admin.getAcademicYear());
    List<File> studentFolders = new ArrayList<>();
    List<File> gradesFolders = new ArrayList<>();
    Admin.findStudentFolders(baseDir, studentFolders); // collect all student 
    Admin.findStudentFolders(baseDir, gradesFolders);

    for (File studentFolder : studentFolders) {
        File studentInfo = new File(studentFolder, studentFolder.getName() + "_info.txt");
        File studentGrades = new File(studentFolder, studentFolder.getName() + "_grades.txt");

        System.out.println("Checking student folder: " + studentFolder.getAbsolutePath());
        System.out.println("Info file exists? " + studentInfo.exists());
        if (studentInfo.exists()) {
            List<String> enc = Utils.readFile(studentInfo.getPath());
            List<String> grd = Utils.readFile(studentGrades.getPath());
            List<Float> allgrades = new ArrayList<>();
            if (enc.isEmpty()) continue;
            String[] p = Utils.decryptEachField(enc.get(0)).split("\\|", -1);
            if(studentGrades.exists()){
                for (String grline : grd){
                    String gparts[] = grline.split("~", -1);
                    float grade = Float.parseFloat(gparts[3]);

                    allgrades.add(grade);
                }
            }
            float ave = 0;
            if (!allgrades.isEmpty()) {
                float sum = 0;
                for (float g : allgrades)
                    sum += g;
                ave = sum / allgrades.size();
            }
            float numericalave = Admin.convertAve(ave);

            String lastName = p[1]; // adjust index according to your fields
            float gwa = numericalave;     
            String remarks = (numericalave < 3.00) ? "PASSED" : "FAILED";
            String year = p[14];
            String program = p[16];
            String section = p[20];

            // optional: only show if matches dropdown selections
            if (program.equals(Admin.getPromotechoicefromgui()) && section.equals(Admin.getPromotiontype())) {
                model.addRow(new Object[]{
                    studentFolder.getName(),
                    year,
                    program,
                    lastName,
                    gwa,
                    remarks
                });
            }
        }
    }
}

    // ================= HELPER METHOD TO LOAD STUDENTS =================


    
    Font btnFont = new Font("Segoe UI", Font.ITALIC, 20);

    private JButton styledButton(String text, int x, int y, int w, int h, Color bg) {
        JButton b = new JButton(text);
        b.setFont(btnFont);
        b.setBounds(x, y, w, h);
        b.putClientProperty("JButton.buttonType", "roundRect");
        b.putClientProperty("JButton.style", "toolBar");
        b.setBackground(bg);
        return b;
    }

    // ==========================================================
    private void resetTabs(JLabel[] tabs) {
        for (JLabel t : tabs) t.setForeground(Color.GRAY);
    }
    
    private JLabel createLabel(String t, int x, int y, Font f) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setBounds(x, y, 800, 30);
        l.setForeground(new Color(70, 70, 70));
        return l;
    }

    private void moveUnderline(int x, int width) {
        underline.setBounds(x, 45, width, 4);
        underline.repaint();
    }
}

class BackgroundPanel extends JPanel {
        private BufferedImage img;

        BackgroundPanel(String path) {
            try {
                File f = new File(path);
                img = f.exists() ? ImageIO.read(f) : null;
            } catch (Exception ignored) {}
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (img != null) {
                int pw = getWidth();
                int ph = getHeight();

                double imgRatio = (double) img.getWidth() / img.getHeight();
                double panelRatio = (double) pw / ph;

                int w, h;
                if (panelRatio > imgRatio) {
                    w = pw;
                    h = (int)(pw / imgRatio);
                } else {
                    h = ph;
                    w = (int)(ph * imgRatio);
                }

                int x = (pw - w) / 2;
                int y = (ph - h) / 2;

                g.drawImage(img, x, y, w, h, this);
            }

            g.setColor(new Color(0,0,0,60));
            g.fillRect(0,0,getWidth(),getHeight());
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

