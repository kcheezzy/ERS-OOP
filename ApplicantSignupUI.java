import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ApplicantSignupUI extends JFrame {

    private static final String BG_IMAGE = "src/back.png";

    public ApplicantSignupUI() {
        setTitle("Applicant Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel bg = new BackgroundPanel(BG_IMAGE);
        bg.setLayout(new GridBagLayout());
        setContentPane(bg);

        // Glass pane for back button
        JPanel glass = new JPanel(null);
        glass.setOpaque(false);
        setGlassPane(glass);
        glass.setVisible(true);

        JLabel backLabel = new JLabel("<HTML><U>Back</U></HTML>");
        backLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        backLabel.setForeground(Color.BLACK);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setBounds(getWidth() - 220, 50, 150, 40);
        glass.add(backLabel);

        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new MainUI().setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                backLabel.setForeground(new Color(200, 200, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backLabel.setForeground(Color.BLACK);
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                backLabel.setBounds(getWidth() - 220, 50, 150, 40);
            }
        });

        // Center card
        RoundedPanel card = new RoundedPanel(25);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int cw = (int)(screen.width * 0.82);   
        int ch = (int)(screen.height * 0.73);  
        card.setPreferredSize(new Dimension(cw, ch));
        card.setBackground(Color.WHITE);
        card.setLayout(null);

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(120, 0, 0, 0); 
        gbc.anchor = GridBagConstraints.NORTH;
        bg.add(scroll, gbc);

        // Fonts
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 22);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 24);

        int x1 = 60, x2 = 420, x3 = 780, x4 = 1150;
        int w = 300, h = 40;
        int y = 100, gap = 90;

        java.util.function.BiFunction<Integer, Integer, JTextField> field =
                (xx, yy) -> { JTextField f = new JTextField(); f.setFont(fieldFont); f.setBounds(xx, yy, w, h); return f; };

        // ROW 1 - Name fields
        JTextField lastField = createLabeledField(card, "Last Name", x1, y, labelFont, field);
        JTextField firstField = createLabeledField(card, "First Name", x2, y, labelFont, field);
        JTextField midField = createLabeledField(card, "Middle Name", x3, y, labelFont, field);
        JTextField extField = createLabeledField(card, "Extension Name", x4, y, labelFont, field);

        // ROW 2 - Age / Birthdate / Phone
        y += gap;
        JTextField ageField = createLabeledField(card, "Age", x1, y, labelFont, field);
        // Birthdate Label
        JLabel birthLbl = new JLabel("Birthdate (MM/DD/YYYY)");
        birthLbl.setFont(labelFont);
        birthLbl.setBounds(x2, y, 300, 30);
        card.add(birthLbl);

        // ComboBox: MONTH
        String[] months = {
            "01","02","03","04","05","06","07","08","09","10","11","12"
        };
        JComboBox<String> monthBox = new JComboBox<>(months);
        monthBox.setFont(fieldFont);
        monthBox.setBounds(x2, y + 40, 90, 40);
        card.add(monthBox);

        // ComboBox: DAY
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) days[i] = String.format("%02d", i + 1);

        JComboBox<String> dayBox = new JComboBox<>(days);
        dayBox.setFont(fieldFont);
        dayBox.setBounds(x2 + 100, y + 40, 90, 40);
        card.add(dayBox);

        // ComboBox: YEAR (2000–2010 example)
        String[] years = new String[40];
        int startYear = 1985; // adjust mo if needed
        for (int i = 0; i < 40; i++) years[i] = String.valueOf(startYear + i);

        JComboBox<String> yearBox = new JComboBox<>(years);
        yearBox.setFont(fieldFont);
        yearBox.setBounds(x2 + 200, y + 40, 120, 40);
        card.add(yearBox);

        JTextField phoneField = createLabeledField(card, "Phone Number", x3, y, labelFont, field, 300);

        // ROW 3 - Email
        y += gap;
        JTextField schoolField = createLabeledField(card, "Previous School", x3, y, labelFont, field, 620);
        JTextField emailField = createLabeledField(card, "Email", x1, y, labelFont, field, 620);

        // ROW 4 - Parents
        y += gap;
        JTextField fathersField = createLabeledField(card, "Father's Name", x1, y, labelFont, field, 620);
        JTextField mothersField = createLabeledField(card, "Mother's Name", x3, y, labelFont, field, 620);

        // ROW 5 - Address
        y += gap;
        JTextField addressField = createLabeledField(card, "Address", x1, y, labelFont, field, 1020);

        // ROW 6 - School Year Year Level / College / Program
        y += gap;
         String[] yearOptions = {"1stYear", "2ndYear", "3rdYear", "4thYear"};
        JComboBox<String> yearLevelBox = new JComboBox<>(yearOptions);
        yearLevelBox.setFont(fieldFont); yearLevelBox.setBounds(x2, y + 35, 200, h);
        JLabel yearLevelLbl = new JLabel("Year Level"); yearLevelLbl.setFont(labelFont); yearLevelLbl.setBounds(x2, y, 200, 30);
        card.add(yearLevelLbl); card.add(yearLevelBox);

        String[] collegeOptions = {"COE","CIE","CLA","COS","CAFA","CIT"};
        JComboBox<String> collegeBox = new JComboBox<>(collegeOptions);
        collegeBox.setFont(fieldFont); collegeBox.setBounds(x3, y + 35, 200, h);
        JLabel collegeLbl = new JLabel("College"); collegeLbl.setFont(labelFont); collegeLbl.setBounds(x3, y, 200, 30);
        card.add(collegeLbl); card.add(collegeBox);

        Map<String, String[]> programsMap = new HashMap<>();
        programsMap.put("COE", new String[] {"BSCE", "BSEE", "BSME"});
        programsMap.put("CIE", new String[] {"BSIE", "BTVTE"});
        programsMap.put("CLA", new String[] {"BSBM", "BSE", "BSHM"});
        programsMap.put("COS", new String[] {"BSIT", "BSCS", "BSIS", "BSES", "BASLT"});
        programsMap.put("CAFA", new String[] {"BSA", "BFA", "BGT"});
        programsMap.put("CIT", new String[] {"BSFT", "BET"});

        JComboBox<String> programBox = new JComboBox<>(programsMap.get(collegeOptions[0]));
        programBox.setFont(fieldFont); programBox.setBounds(x4, y + 35, 200, h);
        JLabel programLbl = new JLabel("Program"); programLbl.setFont(labelFont); programLbl.setBounds(x4, y, 200, 30);
        card.add(programLbl); card.add(programBox);

        collegeBox.addActionListener(e -> {
            String selectedCollege = collegeBox.getSelectedItem().toString();
            programBox.setModel(new DefaultComboBoxModel<>(programsMap.get(selectedCollege)));
        });

        // Label
        JLabel syLbl = new JLabel("School Year");
        syLbl.setFont(labelFont);
        syLbl.setBounds(x1, y, 200, 30);
        card.add(syLbl);

        // Generate options automatically (current year + next 4)
        int currentYear = java.time.Year.now().getValue();
        String[] syOptions = new String[5];
        for (int i = 0; i < 5; i++) {
            syOptions[i] = (currentYear + i) + "-" + (currentYear + i + 1);
        }

        // ComboBox
        JComboBox<String> syBox = new JComboBox<>(syOptions);
        syBox.setFont(fieldFont);
        syBox.setBounds(x1, y + 35, 200, h);
        card.add(syBox);

        

        // ROW 7 - Password
        y += gap;
        JPasswordField passField = new JPasswordField();
        JLabel passLbl = new JLabel("Enter your password"); passLbl.setFont(labelFont); passLbl.setBounds(x1, y, 200, h);
        passField.setFont(fieldFont); passField.setBounds(x1, y + 50, w, h);
        card.add(passLbl); card.add(passField);

       

        // SUBMIT BUTTON
        JButton submitBtn = new JButton("Submit Application");
        submitBtn.setBounds(1200, 700, 280, 60);
        submitBtn.setBackground(new Color(173, 131, 48));
        submitBtn.setForeground(Color.BLACK);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 26));
        submitBtn.setFocusPainted(false);
        submitBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        submitBtn.addActionListener(e -> {
            
             System.out.println("SUBMIT CLICKED!");

            // Validation
            if (lastField.getText().isEmpty() || firstField.getText().isEmpty() || passField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields.",
                        "Missing Fields",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int age = 0;
            try { age = Integer.parseInt(ageField.getText().trim()); } 
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid age input.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String birthdate =
            monthBox.getSelectedItem() + "/" +
            dayBox.getSelectedItem()   + "/" +
            yearBox.getSelectedItem();

            String schoolYear = syBox.getSelectedItem().toString();


            // Register
            String applicantId = Applicant.registerGUI(
            lastField.getText(),
            firstField.getText(),
            midField.getText(),
            extField.getText(),
            Integer.parseInt(ageField.getText()),
            birthdate,
            schoolField.getText(),
            phoneField.getText(),
            emailField.getText(),
            fathersField.getText(),
            mothersField.getText(),
            addressField.getText(),
            schoolYear,
            yearLevelBox.getSelectedItem().toString(),
            collegeBox.getSelectedItem().toString(),
            programBox.getSelectedItem().toString(),
            new String(passField.getPassword())
        );



            // Load and open dashboard
            Applicant a = Applicant.getApplicantById(applicantId);
            a.loadOrAssignExam();
           new ApplicantDashboardUI(
                a.getId(),
                a.getFullName(),
                a.getBirthdate(),
                a.getAge(),     
                a.getAddress(),
                a.getEmail(),
                a.getPhone(),
                a.getStatus(),
                a.getExamDate(),
                a.getExamTime(),
                a.getExamRoom(),
                a.getSchool(),
                a.getFathersName(),
                a.getMothersName(),
                a.getSchoolYear(),
                a.getYearLevel(),
                a.getCollege(),
                a.getProgram()       // if this is SHS/College program
        ).setVisible(true);
            dispose();
        });

        card.add(submitBtn);
    }

    // Helper to create labeled JTextField
    private JTextField createLabeledField(JPanel card, String labelText, int x, int y, Font labelFont,
                                          java.util.function.BiFunction<Integer,Integer,JTextField> fieldFunc) {
        return createLabeledField(card, labelText, x, y, labelFont, fieldFunc, 300);
    }

    private JTextField createLabeledField(JPanel card, String labelText, int x, int y, Font labelFont,
                                          java.util.function.BiFunction<Integer,Integer,JTextField> fieldFunc, int width) {
        JLabel lbl = new JLabel(labelText); lbl.setFont(labelFont); lbl.setBounds(x, y, 200, 30);
        JTextField f = fieldFunc.apply(x, y + 35); f.setBounds(x, y + 35, width, f.getHeight());
        card.add(lbl); card.add(f);
        return f;
    }

    // Rounded panel
    class RoundedPanel extends JPanel {
        int arc;
        RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Background panel
    class BackgroundPanel extends JPanel {
        private BufferedImage img;
        BackgroundPanel(String path) { try { File f = new File(path); img = f.exists()? ImageIO.read(f) : null; } catch (Exception ignored) {} }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), null); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApplicantSignupUI().setVisible(true));
    }
}
