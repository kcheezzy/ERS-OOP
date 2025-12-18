import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StudentGUI {

    private JPanel contentPanel;
    private Student student;

    // ---------- PROFILE ----------
    public static class ProfilePanel extends JPanel {
        public ProfilePanel(Student student) {
            setLayout(null);
            setOpaque(false);

            RoundedPanel card = new RoundedPanel(20);
            card.setBackground(Color.WHITE);
            card.setBounds(20, 10, 1770, 610);
            card.setLayout(null);
            add(card);

            Font labelFont = new Font("Segoe UI", Font.PLAIN, 24);
            Font infoFont = new Font("Segoe UI", Font.BOLD, 26);

            int paddingLeft = 40;
            int col1 = paddingLeft;
            int col2 = paddingLeft + 560;
            int col3 = paddingLeft + 1120;

            int y = 30;
            int gap = 85;

            card.add(labeledInfo("Full Name",
                    student.getFirstName() + " " + student.getLastName(),
                    col1, y, labelFont, infoFont));

            card.add(labeledInfo("Age", student.getAge(), col1, y + gap, labelFont, infoFont));
            card.add(labeledInfo("Birthdate", student.getBirthdate(), col2, y + gap, labelFont, infoFont));
            card.add(labeledInfo("School", student.getSchool(), col3, y + gap, labelFont, infoFont));

            card.add(labeledInfo("Phone Number", student.getPhone(), col1, y + gap * 2, labelFont, infoFont));
            card.add(labeledInfo("Email Address", student.getEmail(), col2, y + gap * 2, labelFont, infoFont));
            card.add(labeledInfo("Program", student.getProgram(), col3, y + gap * 2, labelFont, infoFont));

            card.add(labeledInfo("Father's Name", student.getFather(), col1, y + gap * 3, labelFont, infoFont));
            card.add(labeledInfo("Mother's Name", student.getMother(), col2, y + gap * 3, labelFont, infoFont));

            card.add(labeledInfo("Address", student.getAddress(), col1, y + gap * 4, labelFont, infoFont));

            card.add(labeledInfo("School Year", student.getSchoolYear(), col1, y + gap * 5, labelFont, infoFont));
            card.add(labeledInfo("Year Level", student.getYearLevel(), col2, y + gap * 5, labelFont, infoFont));
            card.add(labeledInfo("College", student.getCollege(), col3, y + gap * 5, labelFont, infoFont));
        }

        private JPanel labeledInfo(String label, String value, int x, int y, Font lf, Font vf) {
            JPanel p = new JPanel(null);
            p.setOpaque(false);
            p.setBounds(x, y, 520, 78);

            JLabel lbl = new JLabel(label);
            lbl.setFont(lf);
            lbl.setForeground(new Color(80, 80, 80));
            lbl.setBounds(0, 0, 520, 24);
            p.add(lbl);

            JLabel val = new JLabel(value == null ? "" : value);
            val.setFont(vf);
            val.setForeground(new Color(10, 20, 70));
            val.setBounds(0, 24, 520, 46);
            p.add(val);

            return p;
        }

        static class RoundedPanel extends JPanel {
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


    // ---------- MESSAGE ----------
    public static class MessagePanel extends JPanel {
        private final Student student;
        private JPanel rightContent;

        public MessagePanel(Student student) {
            this.student = student;
            setLayout(null);
            setOpaque(false);

            RoundedPanel card = new RoundedPanel(20);
            card.setBackground(new Color(250, 245, 240));
            card.setBounds(20, 10, 1770, 610);
            card.setLayout(null);
            add(card);

            RoundedPanel left = new RoundedPanel(20);
            left.setBackground(new Color(240, 235, 225));
            left.setBounds(20, 20, 280, 560);
            left.setLayout(null);
            card.add(left);

            JButton inboxBtn = new JButton("Inbox");
            inboxBtn.setBounds(30, 40, 220, 60);
            inboxBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
            inboxBtn.setBackground(new Color(215, 210, 205));
            inboxBtn.setForeground(new Color(5, 15, 50));
            inboxBtn.setBorderPainted(false);
            inboxBtn.setFocusPainted(false);
            left.add(inboxBtn);

            JButton composeBtn = new JButton("Compose");
            composeBtn.setBounds(30, 120, 220, 60);
            composeBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
            composeBtn.setBackground(new Color(230, 225, 220));
            composeBtn.setForeground(new Color(150, 150, 150));
            composeBtn.setBorderPainted(false);
            composeBtn.setFocusPainted(false);
            left.add(composeBtn);

            rightContent = new JPanel(null);
            rightContent.setOpaque(false);
            rightContent.setBounds(330, 20, 1420, 560);
            card.add(rightContent);

            showInbox();

            inboxBtn.addActionListener(e -> {
                inboxBtn.setBackground(new Color(215, 210, 205));
                composeBtn.setBackground(new Color(230, 225, 220));
                inboxBtn.setForeground(new Color(5, 15, 50));
                composeBtn.setForeground(new Color(150, 150, 150));
                showInbox();
            });

            composeBtn.addActionListener(e -> {
                composeBtn.setBackground(new Color(215, 210, 205));
                inboxBtn.setBackground(new Color(230, 225, 220));
                composeBtn.setForeground(new Color(5, 15, 50));
                inboxBtn.setForeground(new Color(150, 150, 150));
                showCompose();
            });
        }

        private void showInbox() {
            rightContent.removeAll();

            java.util.List<String> lines = loadInbox();
            int y = 20;

            if (lines == null || lines.isEmpty()) {
                JLabel empty = new JLabel("No messages available.");
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 22));
                empty.setForeground(new Color(5, 15, 50));
                empty.setBounds(20, 20, 600, 40);
                rightContent.add(empty);
            } else {
                for (String line : lines) {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length < 4) continue;

                    String senderName = parts[2];
                    String date = parts[1];
                    String msg = Utils.decrypt(parts[3]);

                    JPanel msgBox = createMessageBox(senderName, date, msg);
                    msgBox.setBounds(20, y, 1380, 150);
                    rightContent.add(msgBox);

                    y += 170;
                }
            }

            rightContent.revalidate();
            rightContent.repaint();
        }

        private JPanel createMessageBox(String sender, String date, String msg) {
            RoundedPanel p = new RoundedPanel(25);
            p.setBackground(new Color(234, 223, 207));
            p.setLayout(null);

            JLabel head = new JLabel("From: " + sender + "   |   " + date);
            head.setFont(new Font("Segoe UI", Font.BOLD, 22));
            head.setForeground(new Color(5, 15, 50));
            head.setBounds(30, 20, 1200, 30);
            p.add(head);

            JTextArea ta = new JTextArea(msg);
            ta.setEditable(false);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            ta.setBackground(new Color(234, 223, 207));
            ta.setForeground(new Color(5, 15, 50));

            JScrollPane sp = new JScrollPane(ta);
            sp.setBounds(30, 60, 1300, 70);
            sp.setBorder(null);
            p.add(sp);

            return p;
        }

        private void showCompose() {
            rightContent.removeAll();

            JLabel toLbl = new JLabel("To :");
            toLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
            toLbl.setForeground(new Color(5, 15, 50));
            toLbl.setBounds(20, 10, 200, 40);
            rightContent.add(toLbl);

            RoundedPanel toBox = new RoundedPanel(20);
            toBox.setBackground(new Color(234, 223, 207));
            toBox.setBounds(20, 55, 1380, 55);
            toBox.setLayout(null);
            rightContent.add(toBox);

            JTextField toField = new JTextField();
            toField.setBounds(20, 12, 1340, 32);
            toField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            toField.setBorder(null);
            toField.setBackground(new Color(234, 223, 207));
            toBox.add(toField);

            JLabel msgLbl = new JLabel("Message :");
            msgLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
            msgLbl.setForeground(new Color(5, 15, 50));
            msgLbl.setBounds(20, 130, 200, 40);
            rightContent.add(msgLbl);

            RoundedPanel msgBox = new RoundedPanel(20);
            msgBox.setLayout(null);
            msgBox.setBackground(new Color(234, 223, 207));
            msgBox.setBounds(20, 175, 1380, 300);
            rightContent.add(msgBox);

            JTextArea msgArea = new JTextArea();
            msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            msgArea.setBorder(null);
            msgArea.setBackground(new Color(234, 223, 207));
            msgArea.setLineWrap(true);
            msgArea.setWrapStyleWord(true);

            JScrollPane sp = new JScrollPane(msgArea);
            sp.setBounds(20, 20, 1340, 260);
            sp.setBorder(null);
            msgBox.add(sp);

            JButton send = new JButton("Send");
            send.setFont(new Font("Segoe UI", Font.BOLD, 20));
            send.setForeground(new Color(5, 15, 50));
            send.setBackground(new Color(234, 223, 207));
            send.setBorderPainted(false);
            send.setFocusPainted(false);
            send.setBounds(1220, 490, 180, 55);
            rightContent.add(send);

            send.addActionListener(e -> {
                String toId = toField.getText().trim();
                String message = msgArea.getText().trim();

                if (toId.isEmpty() || message.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                Student recipient = Student.findById(toId);
                if (recipient == null) {
                    JOptionPane.showMessageDialog(this, "Recipient not found.");
                    return;
                }

                String inboxPath = String.format("data/STUDENTS/%s/%s/%s/%s/%s/%s_inbox.txt",
                        recipient.getSchoolYear().replace(" ", ""),
                        recipient.getYearLevel().replace(" ", ""),
                        recipient.getCollege().toUpperCase(),
                        recipient.getProgram().toUpperCase(),
                        recipient.getSection(),
                        recipient.getId()
                );

                List<String> inbox = Utils.readFile(inboxPath);
                if (inbox == null) inbox = new ArrayList<>();

                String date = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date());
                String raw = student.getId() + "|" + date + "|" +
                        (student.getFirstName() + " " + student.getLastName()) +
                        "|" + Utils.encrypt(message);

                inbox.add(raw);
                Utils.writeRawFile(inboxPath, inbox);
                JOptionPane.showMessageDialog(this, "Message sent!");

                showCompose();
            });

            rightContent.revalidate();
            rightContent.repaint();
        }

        private List<String> loadInbox() {
            String path = String.format(
                    "data/STUDENTS/%s/%s/%s/%s/%s/%s_inbox.txt",
                    student.getSchoolYear().replace(" ", ""),
                    student.getYearLevel().replace(" ", ""),
                    student.getCollege().toUpperCase(),
                    student.getProgram().toUpperCase(),
                    student.getSection(),
                    student.getId()
            );
            return Utils.readFile(path);
        }

        static class RoundedPanel extends JPanel {
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


// ---------- SCHEDULE ----------
public static class SchedulePanel extends JPanel {

    public SchedulePanel(Student student) {
        setLayout(null);
        setOpaque(false);

        // ===== MAIN CARD =====
        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(Color.WHITE);
        card.setBounds(20, 10, 1770, 610);
        card.setLayout(null);
        add(card);

        Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
        Font rowFont = new Font("Segoe UI", Font.PLAIN, 16);

        // Adjusted columns to span the full width (1770 - margins)
        int leftMargin = 30;
        int rightMargin = 30;
        int totalWidth = 1770 - leftMargin - rightMargin; // 1710
        
        int col1X = leftMargin;
        int col1W = 160;  // Professor's ID
        
        int col2X = col1X + col1W + 15;
        int col2W = 350;  // Professor's Name
        
        int col3X = col2X + col2W + 15;
        int col3W = 600;  // Subject (largest)
        
        int col4X = col3X + col3W + 15;
        int col4W = 220;  // Day
        
        int col5X = col4X + col4W + 15;
        int col5W = 300;  // Time

        int headerY = 20;
        int headerHeight = 45;

        // ------- HEADERS -------
        card.add(makeHeader("[Professor's ID]", col1X, headerY, col1W, headerHeight, headerFont));
        card.add(makeHeader("[Professor's Name]", col2X, headerY, col2W, headerHeight, headerFont));
        card.add(makeHeader("[Subject]", col3X, headerY, col3W, headerHeight, headerFont));
        card.add(makeHeader("[Day]", col4X, headerY, col4W, headerHeight, headerFont));
        card.add(makeHeader("[Time]", col5X, headerY, col5W, headerHeight, headerFont));

        // ------ LOAD SCHEDULE ------
        String sy = student.getSchoolYear();
        String yearLevel = student.getYearLevel();
        String course = student.getProgram();
        String section = student.getSection();
        String dept = student.getCollege();

        String crsAndSec = course + section;

        File schedFile = new File(System.getProperty("user.dir"), "data/STUDENTS/"
                + sy.replace(" ", "") + "/"
                + yearLevel.replace(" ", "") + "/"
                + dept.toUpperCase() + "/"
                + course.toUpperCase() + "/"
                + section + "/" + crsAndSec.toUpperCase() + "_Schedule.txt");

        List<String[]> rows = new ArrayList<>();

        try {
            if (schedFile.exists()) {
                List<String> lines = Files.readAllLines(schedFile.toPath());
                for (String line : lines) {
                    String[] p = line.split("\\|");
                    if (p.length >= 5) {
                        rows.add(new String[]{
                                p[0], // Professor's ID
                                p[1], // Professor's Name
                                p[2], // Subject
                                p[3], // Day
                                p[4]  // Time
                        });
                    }
                }
            } else {
                System.out.println("Schedule file not found: " + schedFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Schedule file path: " + schedFile.getAbsolutePath());
        System.out.println("Rows loaded: " + rows.size());

        // ------- SCROLLABLE ROWS -------
        JPanel rowPanel = new JPanel(null);
        rowPanel.setOpaque(true);
        rowPanel.setBackground(Color.WHITE);

        int rowHeight = 50;
        int rowGap = 10;
        int y = 0;

        for (String[] r : rows) {
            rowPanel.add(makeRowCell(r[0], col1X, y, col1W, rowHeight, rowFont));
            rowPanel.add(makeRowCell(r[1], col2X, y, col2W, rowHeight, rowFont));
            rowPanel.add(makeRowCell(r[2], col3X, y, col3W, rowHeight, rowFont));
            rowPanel.add(makeRowCell(r[3], col4X, y, col4W, rowHeight, rowFont));
            rowPanel.add(makeRowCell(r[4], col5X, y, col5W, rowHeight, rowFont));
            y += rowHeight + rowGap;
        }

        rowPanel.setPreferredSize(new Dimension(1770, Math.max(y, 500)));

        JScrollPane scroll = new JScrollPane(rowPanel);
        scroll.setBounds(0, headerY + headerHeight + 15, 1770, 520);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        card.add(scroll);

        // Ensure UI updates
        rowPanel.revalidate();
        rowPanel.repaint();
        scroll.revalidate();
        scroll.repaint();
    }

    // HEADER LABEL
    private JLabel makeHeader(String text, int x, int y, int width, int height, Font f) {
        RoundedLabel h = new RoundedLabel(text);
        h.setFont(f);
        h.setForeground(new Color(60, 60, 60));
        h.setBackground(new Color(230, 220, 210));
        h.setBounds(x, y, width, height);
        return h;
    }

    // ROW CELL
    private RoundedLabel makeRowCell(String text, int x, int y, int width, int height, Font f) {
        RoundedLabel cell = new RoundedLabel(text);
        cell.setFont(f);
        cell.setForeground(new Color(40, 40, 40));
        cell.setBackground(new Color(240, 235, 230));
        cell.setBounds(x, y, width, height);
        return cell;
    }

    // Rounded Label for both headers and cells
    static class RoundedLabel extends JLabel {
        RoundedLabel(String text) {
            super(text);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); 
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // Rounded Panel
    static class RoundedPanel extends JPanel {
        int arc;
        RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }

        @Override
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

// ---------- GRADES ----------
public static class GradesPanel extends JPanel {

    public GradesPanel(Student student) {
        setLayout(null);
        setOpaque(false);

        // ===== MAIN CARD =====
        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(Color.WHITE);
        card.setBounds(20, 10, 1770, 610);
        card.setLayout(null);
        add(card);

        // ===== COLORS (match Schedule palette) =====
        Color headerPillBg = new Color(230, 220, 210);
        Color rowBg = new Color(240, 235, 230);
        Color textColor = new Color(40, 40, 40);

        Font headerFont = new Font("Segoe UI", Font.BOLD, 22);
        Font pillFont = new Font("Segoe UI", Font.BOLD, 22);
        Font emptyFont = new Font("Segoe UI", Font.PLAIN, 24);

        // ===== FILE PATH (dependent on student data) =====
        String sy = student.getSchoolYear();
        String yearLevel = student.getYearLevel();
        String course = student.getProgram();
        String section = student.getSection();
        String dept = student.getCollege();

        String gradesPath =
                "data/STUDENTS/"
                        + sy.replace(" ", "") + "/"
                        + yearLevel.replace(" ", "") + "/"
                        + dept.toUpperCase() + "/"
                        + course.toUpperCase() + "/"
                        + section + "/"
                        + student.getId() + "/"
                        + student.getId() + "_grades.txt";

        java.util.List<String[]> rows = new ArrayList<>();

        // ===== READ (DEPENDENT ON WHOLE SOURCE CODE: matches Faculty.saveGrade encryption) =====
        try {
            File f = new File(gradesPath);
            if (f.exists()) {
                List<String> lines = Files.readAllLines(f.toPath());
                for (String line : lines) {
                    if (line == null || line.trim().isEmpty()) continue;

                    // Faculty writes: encProfId~encFullName~encSubject~grade
                    String[] p = line.split("~", -1);
                    if (p.length < 4) continue;

                    // decrypt first 3 fields (grade is plain number/string)
                    String profId = safeDecryptEachField(p[0].trim());
                    String profName = safeDecryptEachField(p[1].trim());
                    String subject = safeDecryptEachField(p[2].trim());
                    String grade = p[3].trim();

                    rows.add(new String[]{profId, profName, subject, grade});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // ===== EMPTY: DO NOT DISPLAY THE HEADER/ROWS =====
        if (rows.isEmpty()) {
            JLabel empty = new JLabel("No grades recorded.", SwingConstants.CENTER);
            empty.setFont(emptyFont);
            empty.setForeground(new Color(15, 36, 96));
            empty.setBounds(0, 260, 1770, 60);
            card.add(empty);
            return;
        }

        // ===== LAYOUT (like your screenshot: wide rounded row + inner rounded pills) =====
        int rowX = 30;
        int rowW = 1710;

        int startY = 110;
        int headerY = 30;
        int rowH = 95;
        int gapY = 25;

        int pad = 22;
        int pillY = 22;
        int pillH = 52;

        int idW = 260;
        int nameW = 430;
        int subjW = 650;
        int gradeW = 260;
        int gapX = 25;

        int x1 = pad;
        int x2 = x1 + idW + gapX;
        int x3 = x2 + nameW + gapX;
        int x4 = x3 + subjW + gapX;

        // ===== HEADER ROW =====
        RoundedPanel headerRow = new RoundedPanel(25);
        headerRow.setBackground(rowBg);
        headerRow.setLayout(null);
        headerRow.setBounds(rowX, headerY, rowW, rowH);

        headerRow.add(makePill("[Professor’s ID]", x1, pillY, idW, pillH, headerPillBg, textColor, headerFont));
        headerRow.add(makePill("[Professor’s Name]", x2, pillY, nameW, pillH, headerPillBg, textColor, headerFont));
        headerRow.add(makePill("[Subject]", x3, pillY, subjW, pillH, headerPillBg, textColor, headerFont));
        headerRow.add(makePill("[Grade]", x4, pillY, gradeW, pillH, headerPillBg, textColor, headerFont));

        card.add(headerRow);

        // ===== DATA LIST (only show existing subjects; no extra blank rows) =====
        JPanel list = new JPanel(null);
        list.setOpaque(true);
        list.setBackground(Color.WHITE);

        int y = 0;
        for (String[] r : rows) {
            RoundedPanel row = new RoundedPanel(25);
            row.setBackground(rowBg);
            row.setLayout(null);
            row.setBounds(0, y, rowW, rowH);

            row.add(makePill(r[0], x1, pillY, idW, pillH, headerPillBg, textColor, pillFont));
            row.add(makePill(r[1], x2, pillY, nameW, pillH, headerPillBg, textColor, pillFont));
            row.add(makePill(r[2], x3, pillY, subjW, pillH, headerPillBg, textColor, pillFont));
            row.add(makePill(r[3], x4, pillY, gradeW, pillH, headerPillBg, textColor, pillFont));

            list.add(row);
            y += rowH + gapY;
        }

        list.setPreferredSize(new Dimension(rowW, Math.max(y, 420)));

        JScrollPane sp = new JScrollPane(list);
        sp.setBounds(rowX, startY, rowW, 470);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(20);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        card.add(sp);
    }

    // decrypt helper: uses your existing Utils.decryptEachField used in Faculty.java
    private String safeDecryptEachField(String s) {
        try {
            // If encrypted, this returns plaintext.
            // If not encrypted / already plaintext, catch and just return original.
            return Utils.decryptEachField(s);
        } catch (Exception ignored) {
            return s;
        }
    }

    private RoundedPill makePill(String text, int x, int y, int w, int h,
                                 Color bg, Color fg, Font font) {
        RoundedPill p = new RoundedPill(text, 20);
        p.setBackground(bg);
        p.setForeground(fg);
        p.setFont(font);
        p.setBounds(x, y, w, h);
        return p;
    }

    static class RoundedPanel extends JPanel {
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

    static class RoundedPill extends JLabel {
        private final int arc;
        RoundedPill(String t, int arc) {
            super(t == null ? "" : t);
            this.arc = arc;
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}

    
    // ---------- ACADEMIC PANEL ----------
// ---------- ACADEMIC PANEL ----------
public static class AcademicPanel extends JPanel {

    private final Student student;
    private final JPanel rightContent;
    private final CardLayout rightLayout;

    private final JButton calendarBtn;
    private final JButton activityBtn;
    private final JButton assignmentsBtn;
    private final JButton notesBtn;

    private Calendar cal;
    private JLabel monthYearLabel;
    private CircleDayLabel[] dayCells;

    private JComboBox<String> monthBox;
    private JComboBox<Integer> yearBox;

    // assignments -> used by calendar + activity + view assignments
    private java.util.List<AssignmentRecord> assignmentsCache = new ArrayList<>();
    private Set<String> assignmentDueDays = new HashSet<>();

    // Pastel highlight colors (updated)
    private final Color PASTEL_TODAY = new Color(210, 210, 210);
    private final Color PASTEL_DUE = new Color(255, 120, 120);

    public AcademicPanel(Student student) {
        this.student = student;
        setLayout(null);
        setOpaque(false);

        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(new Color(250, 245, 240));
        card.setBounds(20, 10, 1770, 610);
        card.setLayout(null);
        add(card);

        RoundedPanel left = new RoundedPanel(20);
        left.setBackground(new Color(244, 238, 228));
        left.setBounds(20, 20, 280, 560);
        left.setLayout(null);
        card.add(left);

        Font navFont = new Font("Segoe UI", Font.BOLD, 22);

        calendarBtn = createNavButton("Calendar", 40, navFont);
        activityBtn = createNavButton("Activity", 120, navFont);
        assignmentsBtn = createNavButton("View Assignments", 200, navFont);
        notesBtn = createNavButton("Notes", 280, navFont);

        left.add(calendarBtn);
        left.add(activityBtn);
        left.add(assignmentsBtn);
        left.add(notesBtn);

        rightLayout = new CardLayout();
        rightContent = new JPanel(rightLayout);
        rightContent.setOpaque(false);
        rightContent.setBounds(330, 20, 1420, 560);
        card.add(rightContent);

        refreshAssignmentsCache();

        JPanel calendarView = buildCalendarView();
        JPanel activityView = buildActivityView();
        JPanel assignmentsView = buildAssignmentsView();
        JPanel notesView = buildNotesView();

        rightContent.add(calendarView, "CAL");
        rightContent.add(activityView, "ACT");
        rightContent.add(assignmentsView, "ASSIGN");
        rightContent.add(notesView, "NOTES");

        selectNav("CAL");

        calendarBtn.addActionListener(e -> {
            refreshAssignmentsCache();
            rebuildRightCard("CAL", buildCalendarView());
            selectNav("CAL");
        });

        activityBtn.addActionListener(e -> {
            refreshAssignmentsCache();
            rebuildRightCard("ACT", buildActivityView());
            selectNav("ACT");
        });

        assignmentsBtn.addActionListener(e -> {
            refreshAssignmentsCache();
            rebuildRightCard("ASSIGN", buildAssignmentsView());
            selectNav("ASSIGN");
        });

        notesBtn.addActionListener(e -> selectNav("NOTES"));
    }

    private void rebuildRightCard(String key, JPanel newPanel) {
        rightContent.add(newPanel, key);
        rightLayout.show(rightContent, key);
        rightContent.revalidate();
        rightContent.repaint();
    }

    private JButton createNavButton(String text, int y, Font font) {
        JButton b = new JButton(text);
        b.setBounds(30, y, 220, 60);
        b.setFont(font);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setMargin(new Insets(0, 0, 0, 0));
        return b;
    }

    private void selectNav(String key) {
        Color selectedBg = new Color(215, 210, 205);
        Color unselectedBg = new Color(230, 225, 220);
        Color selectedFg = new Color(5, 15, 50);
        Color unselectedFg = new Color(150, 150, 150);

        JButton[] all = {calendarBtn, activityBtn, assignmentsBtn, notesBtn};
        for (JButton b : all) {
            b.setBackground(unselectedBg);
            b.setForeground(unselectedFg);
        }

        JButton active =
                "CAL".equals(key) ? calendarBtn :
                        "ACT".equals(key) ? activityBtn :
                                "ASSIGN".equals(key) ? assignmentsBtn : notesBtn;

        active.setBackground(selectedBg);
        active.setForeground(selectedFg);

        rightLayout.show(rightContent, key);
    }

    // ----------------------- CALENDAR -----------------------
    private JPanel buildCalendarView() {
        JPanel root = new JPanel(null);
        root.setOpaque(false);

        RoundedPanel calBox = new RoundedPanel(25);
        calBox.setBackground(new Color(245, 236, 222));
        calBox.setLayout(null);
        calBox.setBounds(40, 30, 900, 500);
        root.add(calBox);

        String[] months = {
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        };
        monthBox = new JComboBox<>(months);
        monthBox.setFont(new Font("Segoe UI", Font.BOLD, 20));
        monthBox.setBounds(260, 20, 220, 40);
        monthBox.setBackground(new Color(245, 236, 222));
        monthBox.setFocusable(false);
        calBox.add(monthBox);

        int currentYear = 2030;
        Integer[] years = new Integer[currentYear - 1982 + 1];
        int yi = 0;
        for (int y = 1982; y <= currentYear; y++) years[yi++] = y;

        yearBox = new JComboBox<>(years);
        yearBox.setFont(new Font("Segoe UI", Font.BOLD, 20));
        yearBox.setBounds(500, 20, 140, 40);
        yearBox.setBackground(new Color(245, 236, 222));
        yearBox.setFocusable(false);
        calBox.add(yearBox);

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        monthYearLabel.setForeground(new Color(8, 20, 72));
        monthYearLabel.setBounds(0, 20, 900, 40);
        monthYearLabel.setVisible(false);
        calBox.add(monthYearLabel);

        JButton prev = new JButton("▲");
        JButton next = new JButton("▼");
        prev.setBounds(650, 18, 60, 44);
        next.setBounds(720, 18, 60, 44);
        for (JButton b : new JButton[]{prev, next}) {
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setFont(new Font("Segoe UI", Font.BOLD, 22));
            b.setBackground(new Color(235, 224, 205));
        }
        calBox.add(prev);
        calBox.add(next);

        JPanel grid = new JPanel(null);
        grid.setOpaque(false);
        grid.setBounds(60, 80, 780, 380);
        calBox.add(grid);

        Font dayHeaderFont = new Font("Segoe UI", Font.BOLD, 16);
        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        int cellW = 780 / 7;
        int cellH = 55;

        for (int i = 0; i < 7; i++) {
            JLabel h = new JLabel(days[i], SwingConstants.CENTER);
            h.setFont(dayHeaderFont);
            h.setForeground(new Color(25, 30, 60));
            h.setBounds(i * cellW, 0, cellW, cellH);
            grid.add(h);
        }

        dayCells = new CircleDayLabel[42];
        Font dateFont = new Font("Segoe UI", Font.PLAIN, 18);

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                int idx = row * 7 + col;

                CircleDayLabel cell = new CircleDayLabel();
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setFont(dateFont);
                cell.setOpaque(false);
                cell.setForeground(new Color(5, 15, 50));
                cell.setBounds(col * cellW, (row + 1) * cellH, cellW, cellH);
                cell.setBorder(BorderFactory.createLineBorder(new Color(220, 210, 200)));

                dayCells[idx] = cell;
                grid.add(cell);
            }
        }

        JPanel legend = new JPanel(null);
        legend.setOpaque(false);
        legend.setBounds(60, 465, 780, 30);
        calBox.add(legend);

        Font legFont = new Font("Segoe UI", Font.PLAIN, 16);

        JPanel todayDot = new JPanel();
        todayDot.setBackground(PASTEL_TODAY);
        todayDot.setBounds(0, 7, 16, 16);
        legend.add(todayDot);

        JLabel todayTxt = new JLabel("Today");
        todayTxt.setFont(legFont);
        todayTxt.setForeground(new Color(5, 15, 50));
        todayTxt.setBounds(22, 2, 120, 25);
        legend.add(todayTxt);

        JPanel dueDot = new JPanel();
        dueDot.setBackground(PASTEL_DUE);
        dueDot.setBounds(150, 7, 16, 16);
        legend.add(dueDot);

        JLabel dueTxt = new JLabel("Assignment due");
        dueTxt.setFont(legFont);
        dueTxt.setForeground(new Color(5, 15, 50));
        dueTxt.setBounds(172, 2, 180, 25);
        legend.add(dueTxt);

        cal = Calendar.getInstance();
        updateCalendarGrid();

        monthBox.addActionListener(e -> {
            if (cal == null) return;
            cal.set(Calendar.MONTH, monthBox.getSelectedIndex());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            updateCalendarGrid();
        });

        yearBox.addActionListener(e -> {
            if (cal == null) return;
            Object val = yearBox.getSelectedItem();
            if (val == null) return;
            cal.set(Calendar.YEAR, (Integer) val);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            updateCalendarGrid();
        });

        prev.addActionListener(e -> {
            cal.add(Calendar.MONTH, -1);
            updateCalendarGrid();
        });
        next.addActionListener(e -> {
            cal.add(Calendar.MONTH, 1);
            updateCalendarGrid();
        });

        RoundedPanel summaryBox = new RoundedPanel(25);
        summaryBox.setBackground(new Color(245, 236, 222));
        summaryBox.setLayout(null);
        summaryBox.setBounds(980, 60, 380, 410);
        root.add(summaryBox);

        JLabel sTitle = new JLabel("Summary:");
        sTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        sTitle.setForeground(new Color(8, 20, 72));
        sTitle.setBounds(30, 25, 300, 30);
        summaryBox.add(sTitle);

        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        summaryArea.setBackground(new Color(245, 236, 222));
        summaryArea.setForeground(new Color(5, 15, 50));
        summaryArea.setText(buildMonthSummaryText());

        JScrollPane sp = new JScrollPane(summaryArea);
        sp.setBorder(null);
        sp.setBounds(30, 70, 320, 300);
        summaryBox.add(sp);

        return root;
    }

    private String buildMonthSummaryText() {
        if (assignmentsCache == null || assignmentsCache.isEmpty()) {
            return "• No recorded academic events for this month.";
        }

        Calendar base = (Calendar) cal.clone();
        int y = base.get(Calendar.YEAR);
        int m = base.get(Calendar.MONTH);

        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (AssignmentRecord ar : assignmentsCache) {
            if (ar.deadlineDate == null) continue;
            Calendar c = Calendar.getInstance();
            c.setTime(ar.deadlineDate);
            if (c.get(Calendar.YEAR) == y && c.get(Calendar.MONTH) == m) {
                sb.append("• ").append(ar.course == null ? "" : ar.course)
                        .append(" - ").append(ar.title == null ? "Assignment" : ar.title)
                        .append(" (Due: ").append(ar.deadlineRaw == null ? "" : ar.deadlineRaw).append(")")
                        .append("\n");
                count++;
                if (count >= 6) break;
            }
        }

        if (count == 0) return "• No recorded academic events for this month.";
        return sb.toString().trim();
    }

    private void updateCalendarGrid() {
        SimpleDateFormat fmt = new SimpleDateFormat("MMMM yyyy");
        monthYearLabel.setText(fmt.format(cal.getTime()).toUpperCase());

        if (monthBox != null) monthBox.setSelectedIndex(cal.get(Calendar.MONTH));
        if (yearBox != null) yearBox.setSelectedItem(cal.get(Calendar.YEAR));

        Calendar temp = (Calendar) cal.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int firstDay = temp.get(Calendar.DAY_OF_WEEK) - 1;
        int maxDay = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (CircleDayLabel cell : dayCells) {
            cell.setText("");
            cell.setCircleColor(null);
            cell.setForeground(new Color(5, 15, 50));
        }

        Calendar today = Calendar.getInstance();

        for (int d = 1; d <= maxDay; d++) {
            int index = firstDay + d - 1;
            if (index < 0 || index >= dayCells.length) continue;

            CircleDayLabel c = dayCells[index];
            c.setText(String.valueOf(d));

            Calendar day = (Calendar) cal.clone();
            day.set(Calendar.DAY_OF_MONTH, d);

            String key = ymdKey(day);

            boolean isToday =
                    today.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
                            today.get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
                            today.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH);

            boolean isAssignmentDue = assignmentDueDays != null && assignmentDueDays.contains(key);

            if (isAssignmentDue) {
                c.setCircleColor(PASTEL_DUE);
                c.setForeground(new Color(5, 15, 50));
            } else if (isToday) {
                c.setCircleColor(PASTEL_TODAY);
                c.setForeground(new Color(5, 15, 50));
            } else {
                c.setCircleColor(null);
                c.setForeground(new Color(5, 15, 50));
            }
        }
    }

    private String ymdKey(Calendar c) {
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d%02d%02d", y, m, d);
    }

    static class CircleDayLabel extends JLabel {
        private Color circleColor;

        public void setCircleColor(Color c) {
            circleColor = c;
            repaint();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (circleColor != null && getText() != null && !getText().trim().isEmpty()) {
                int d = Math.min(getWidth(), getHeight()) - 16;
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;
                g2.setColor(circleColor);
                g2.fillOval(x, y, d, d);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ----------------------- ACTIVITY -----------------------
    private JPanel buildActivityView() {
        JPanel root = new JPanel(null);
        root.setOpaque(false);

        RoundedPanel listBox = new RoundedPanel(25);
        listBox.setBackground(new Color(245, 236, 222));
        listBox.setBounds(60, 40, 1280, 460);
        listBox.setLayout(null);
        root.add(listBox);

        java.util.List<ActivityItem> items = new ArrayList<>();

        java.util.List<String> inbox = loadInboxForActivity();
        if (inbox != null) {
            for (String line : inbox) {
                try {
                    String[] p = line.split("\\|", -1);
                    if (p.length < 4) continue;
                    String sender = p[2];
                    String date = p[1];
                    Date dt = tryParseActivityDate(date);
                    items.add(new ActivityItem(dt, "• " + sender + " sent you a message on " + date));
                } catch (Exception ignored) {}
            }
        }

        if (assignmentsCache != null) {
            for (AssignmentRecord ar : assignmentsCache) {
                String when = (ar.deadlineRaw == null ? "" : ar.deadlineRaw);
                String course = (ar.course == null ? "" : ar.course);
                String title = (ar.title == null ? "Assignment" : ar.title);
                String text = "• " + course + " assignment: " + title + " (Due: " + when + ")";
                items.add(new ActivityItem(ar.deadlineDate, text));
            }
        }

        items.sort((a, b) -> {
            if (a.date == null && b.date == null) return 0;
            if (a.date == null) return 1;
            if (b.date == null) return -1;
            return b.date.compareTo(a.date);
        });

        int y = 40;
        int gap = 55;

        if (items.isEmpty()) {
            JLabel none = new JLabel("No recent activities.", SwingConstants.CENTER);
            none.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            none.setForeground(new Color(5, 15, 50));
            none.setBounds(0, 40, 1280, 40);
            listBox.add(none);
        } else {
            int max = Math.min(7, items.size());
            for (int i = 0; i < max; i++) {
                String text = items.get(i).text;

                RoundedPanel bar = new RoundedPanel(25);
                bar.setBackground(new Color(234, 223, 207));
                bar.setBounds(40, y, 1200, 40);
                bar.setLayout(null);

                JLabel lbl = new JLabel(text, SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                lbl.setForeground(new Color(5, 15, 50));
                lbl.setBounds(0, 5, 1200, 30);
                bar.add(lbl);

                listBox.add(bar);
                y += gap;
            }
        }

        return root;
    }

    private Date tryParseActivityDate(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            return new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(t);
        } catch (Exception ignored) {}
        return null;
    }

    private java.util.List<String> loadInboxForActivity() {
        String path = String.format(
                "data/STUDENTS/%s/%s/%s/%s/%s/%s_inbox.txt",
                student.getSchoolYear().replace(" ", ""),
                student.getYearLevel().replace(" ", ""),
                student.getCollege().toUpperCase(),
                student.getProgram().toUpperCase(),
                student.getSection(),
                student.getId()
        );
        return Utils.readFile(path);
    }

    static class ActivityItem {
        Date date;
        String text;
        ActivityItem(Date d, String t) { date = d; text = t; }
    }

    // ----------------------- VIEW ASSIGNMENTS -----------------------
    private JPanel buildAssignmentsView() {
        JPanel root = new JPanel(null);
        root.setOpaque(false);

        refreshAssignmentsCache();

        JPanel tabs = new JPanel(null);
        tabs.setOpaque(false);
        tabs.setBounds(40, 0, 1200, 80);
        root.add(tabs);

        Font tabFont = new Font("Segoe UI", Font.BOLD, 22);

        JButton upcoming = new JButton("Upcoming");
        JButton pastDue = new JButton("Past Due");
        JButton completed = new JButton("Completed");

        JButton[] btns = {upcoming, pastDue, completed};
        String[] keys = {"UP", "PAST", "DONE"};

        int x = 40;
        for (JButton b : btns) {
            b.setBounds(x, 30, 200, 40);
            b.setFont(tabFont);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setHorizontalAlignment(SwingConstants.CENTER);
            x += 220;
            tabs.add(b);
        }

        JPanel fixedLine = new JPanel();
        fixedLine.setBackground(new Color(200, 200, 200));
        fixedLine.setBounds(40, 72, 660, 2);
        tabs.add(fixedLine);

        JPanel tabUnderline = new JPanel();
        tabUnderline.setBackground(new Color(8, 20, 72));
        tabUnderline.setBounds(40, 68, 80, 3);
        tabs.add(tabUnderline);

        JPanel content = new JPanel(new CardLayout());
        content.setOpaque(false);
        content.setBounds(40, 80, 1320, 440);
        root.add(content);

        JPanel upPanel   = buildAssignmentsListPanel(filterAssignments("UP"),   "No upcoming assignments.");
        JPanel pastPanel = buildAssignmentsListPanel(filterAssignments("PAST"), "No past-due assignments.");
        JPanel donePanel = buildAssignmentsListPanel(filterAssignments("DONE"), "No completed assignments.");

        content.add(upPanel, "UP");
        content.add(pastPanel, "PAST");
        content.add(donePanel, "DONE");

        CardLayout layout = (CardLayout) content.getLayout();

        Runnable refreshTabs = () -> {
            Font normal = new Font("Segoe UI", Font.PLAIN, 22);
            Color light = new Color(170, 170, 170);

            upcoming.setFont(normal);
            pastDue.setFont(normal);
            completed.setFont(normal);

            upcoming.setForeground(light);
            pastDue.setForeground(light);
            completed.setForeground(light);
        };

        ActionListener listener = e -> {
            refreshTabs.run();

            String cmd = e.getActionCommand();
            layout.show(content, cmd);

            JButton src = (JButton) e.getSource();
            src.setFont(new Font("Segoe UI", Font.BOLD, 22));
            src.setForeground(new Color(8, 20, 72));

            FontMetrics fm = src.getFontMetrics(src.getFont());
            int textW = fm.stringWidth(src.getText());
            int xUnder = src.getX() + (src.getWidth() - textW) / 2;

            tabUnderline.setBounds(xUnder, 68, textW, 3);
            tabUnderline.repaint();
        };

        for (int i = 0; i < btns.length; i++) {
            btns[i].setActionCommand(keys[i]);
            btns[i].addActionListener(listener);
        }

        refreshTabs.run();
        upcoming.doClick();

        return root;
    }

    private JPanel buildAssignmentsListPanel(java.util.List<AssignmentRecord> list, String emptyText) {
        JPanel root = new JPanel(null);
        root.setOpaque(false);

        if (list == null || list.isEmpty()) {
            RoundedPanel card1 = new RoundedPanel(25);
            card1.setBackground(new Color(245, 236, 222));
            card1.setBounds(40, 40, 1240, 160);
            card1.setLayout(null);
            root.add(card1);

            JLabel lbl = new JLabel(emptyText, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            lbl.setForeground(new Color(5, 15, 50));
            lbl.setBounds(0, 60, 1240, 30);
            card1.add(lbl);

            return root;
        }

        JPanel listPanel = new JPanel(null);
        listPanel.setOpaque(false);

        int y = 0;
        int rowH = 90;
        int gap = 18;

        Font f1 = new Font("Segoe UI", Font.BOLD, 20);
        Font f2 = new Font("Segoe UI", Font.PLAIN, 18);

        for (AssignmentRecord ar : list) {
            RoundedPanel row = new RoundedPanel(25);
            row.setBackground(new Color(234, 223, 207));
            row.setBounds(0, y, 1240, rowH);
            row.setLayout(null);

            String top = (ar.course == null ? "" : ar.course) + "  |  " + (ar.professor == null ? "" : ar.professor);
            JLabel topLbl = new JLabel(top, SwingConstants.CENTER);
            topLbl.setFont(f1);
            topLbl.setForeground(new Color(5, 15, 50));
            topLbl.setBounds(0, 10, 1240, 28);
            row.add(topLbl);

            String mid = (ar.title == null ? "Assignment" : ar.title) +
                    "  (Due: " + (ar.deadlineRaw == null ? "" : ar.deadlineRaw) + ")";
            JLabel midLbl = new JLabel(mid, SwingConstants.CENTER);
            midLbl.setFont(f2);
            midLbl.setForeground(new Color(5, 15, 50));
            midLbl.setBounds(0, 42, 1240, 24);
            row.add(midLbl);

            String desc = (ar.description == null ? "" : ar.description.trim());
            if (desc.length() > 90) desc = desc.substring(0, 90) + "...";
            JLabel botLbl = new JLabel(desc, SwingConstants.CENTER);
            botLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            botLbl.setForeground(new Color(5, 15, 50));
            botLbl.setBounds(0, 64, 1240, 22);
            row.add(botLbl);

            listPanel.add(row);
            y += rowH + gap;
        }

        listPanel.setPreferredSize(new Dimension(1240, y));

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBounds(40, 40, 1240, 400);
        sp.getVerticalScrollBar().setUnitIncrement(20);

        root.add(sp);
        return root;
    }

    private java.util.List<AssignmentRecord> filterAssignments(String mode) {
        java.util.List<AssignmentRecord> out = new ArrayList<>();
        Date now = new Date();

        for (AssignmentRecord ar : assignmentsCache) {
            boolean isDone = ar.completed;
            boolean hasDate = ar.deadlineDate != null;
            boolean isPast = hasDate && ar.deadlineDate.before(now);

            if ("DONE".equals(mode)) {
                if (isDone) out.add(ar);
            } else if ("PAST".equals(mode)) {
                if (!isDone && isPast) out.add(ar);
            } else {
                if (!isDone && (!hasDate || !isPast)) out.add(ar);
            }
        }

        out.sort((a, b) -> {
            if (a.deadlineDate == null && b.deadlineDate == null) return 0;
            if (a.deadlineDate == null) return 1;
            if (b.deadlineDate == null) return -1;
            if ("PAST".equals(mode)) return b.deadlineDate.compareTo(a.deadlineDate);
            return a.deadlineDate.compareTo(b.deadlineDate);
        });

        return out;
    }

    // ----------------------- NOTES -----------------------
    private JPanel buildNotesView() {
        JPanel root = new JPanel(null);
        root.setOpaque(false);

        RoundedPanel box = new RoundedPanel(25);
        box.setBackground(new Color(245, 236, 222));
        box.setBounds(60, 40, 1280, 440);
        box.setLayout(null);
        root.add(box);

        JTextArea notesArea = new JTextArea();
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(new Color(245, 236, 222));
        notesArea.setForeground(new Color(5, 15, 50));
        notesArea.setBorder(null);

        String path = notesPath();
        List<String> lines = Utils.readFile(path);
        if (lines != null && !lines.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String line : lines) sb.append(line).append("\n");
            notesArea.setText(sb.toString());
        } else {
            notesArea.setText("");
        }

        JScrollPane sp = new JScrollPane(notesArea);
        sp.setBorder(null);
        sp.setBounds(30, 30, 1210, 340);
        box.add(sp);

        JButton save = new JButton("Save Notes");
        save.setFont(new Font("Segoe UI", Font.BOLD, 18));
        save.setForeground(new Color(5, 15, 50));
        save.setBackground(new Color(234, 223, 207));
        save.setBorderPainted(false);
        save.setFocusPainted(false);
        save.setBounds(1080, 380, 150, 40);
        box.add(save);

        save.addActionListener(e -> {
            String[] arr = notesArea.getText().split("\\R");
            Utils.writeRawFile(path, Arrays.asList(arr));
            JOptionPane.showMessageDialog(this, "Notes saved.");
        });

        return root;
    }

    private String notesPath() {
        return String.format(
                "data/STUDENTS/%s/%s/%s/%s/%s/%s_notes.txt",
                student.getSchoolYear().replace(" ", ""),
                student.getYearLevel().replace(" ", ""),
                student.getCollege().toUpperCase(),
                student.getProgram().toUpperCase(),
                student.getSection(),
                student.getId()
        );
    }

    // ----------------------- ASSIGNMENTS LOADING (AUTO-SCAN + NO ENCRYPTED UI) -----------------------
    private void refreshAssignmentsCache() {
        assignmentsCache = loadAssignmentsFromAutoScan();
        assignmentDueDays = new HashSet<>();

        if (assignmentsCache != null) {
            for (AssignmentRecord ar : assignmentsCache) {
                if (ar.deadlineDate == null) continue;
                Calendar c = Calendar.getInstance();
                c.setTime(ar.deadlineDate);
                assignmentDueDays.add(ymdKey(c));
            }
        }
    }

    private java.util.List<AssignmentRecord> loadAssignmentsFromAutoScan() {
        java.util.List<AssignmentRecord> out = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        String sy = student.getSchoolYear();
        String yl = student.getYearLevel();
        String col = student.getCollege();
        String prog = student.getProgram();
        String sec = student.getSection();
        String id = student.getId();

        File root = new File(System.getProperty("user.dir"), "data/STUDENTS");

        File secDir1 = new File(root, sy + "/" + yl + "/" + prog.toUpperCase() + "/" + sec);
        File secDir2 = new File(root, sy.replace(" ", "") + "/" + yl.replace(" ", "") + "/" + prog.toUpperCase() + "/" + sec);
        File secDir3 = new File(root, sy.replace(" ", "") + "/" + yl.replace(" ", "") + "/" + col.toUpperCase() + "/" +
                prog.toUpperCase() + "/" + sec);

        File studDir = new File(root,
                sy.replace(" ", "") + "/" + yl.replace(" ", "") + "/" + col.toUpperCase() + "/" +
                        prog.toUpperCase() + "/" + sec + "/" + id);

        java.util.List<File> dirs = Arrays.asList(secDir1, secDir2, secDir3, studDir);

        for (File dir : dirs) {
            if (dir == null || !dir.exists() || !dir.isDirectory()) continue;

            File[] files = dir.listFiles((d, name) -> name != null && name.toLowerCase().endsWith("_assignments.txt"));
            if (files == null) continue;

            for (File f : files) {
                java.util.List<String> lines = Utils.readFile(f.getPath());
                if (lines == null || lines.isEmpty()) continue;

                for (String raw : lines) {
                    if (raw == null) continue;
                    String ln = raw.trim();
                    if (ln.isEmpty()) continue;

                    // IMPORTANT: return null if decrypt fails -> so encrypted text never shows in UI
                    String dec = tryDecryptAssignmentLine(ln);
                    if (dec == null || dec.trim().isEmpty()) continue;

                    AssignmentRecord ar = parseAssignment(dec.trim());
                    if (ar == null) continue;

                    String key = (ar.course + "|" + ar.title + "|" + ar.deadlineRaw).toLowerCase();
                    if (seen.contains(key)) continue;
                    seen.add(key);

                    out.add(ar);
                }
            }
        }

        return out;
    }

    // DO NOT allow encrypted garbage into UI:
    // - if not plaintext AND decrypt() fails -> return null (skip)
   private String tryDecryptAssignmentLine(String s) {
    if (s == null) return null;
    String raw = s.trim();
    if (raw.isEmpty()) return null;

    // CHECK 1: Kung Raw na siya at kumpleto ang parts (5 parts), ibalik na agad ang raw text.
    // HINDI na ito dadaan sa decryption.
    String[] plainCheck = raw.split("\\|", -1);
    if (plainCheck.length >= 5) {                                                                                                                                      
        return raw; 
    }

    // CHECK 2: Kung kulang ang parts, baka encrypted. Try i-decrypt.
    try {
        String dec = Utils.decrypt(raw);
        if (dec != null) {
            dec = dec.trim();
            String[] parts = dec.split("\\|", -1);
            if (parts.length >= 5) return dec;
        }
    } catch (Exception ignored) {}

    // Kung hindi valid na raw at hindi rin valid na encrypted, return null (wag i-display para iwas garbage).
    return null; 
}

    private AssignmentRecord parseAssignment(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) return null;

        AssignmentRecord ar = new AssignmentRecord();
        ar.professor = safe(p, 0);
        ar.course = safe(p, 1);
        ar.title = safe(p, 2);
        ar.description = safe(p, 3);
        ar.deadlineRaw = safe(p, 4);

        ar.completed = false;
        for (int i = 5; i < p.length; i++) {
            String t = (p[i] == null ? "" : p[i].trim());
            if (t.equalsIgnoreCase("DONE") || t.equalsIgnoreCase("COMPLETED") || t.equalsIgnoreCase("TRUE")) {
                ar.completed = true;
                break;
            }
        }

        ar.deadlineDate = tryParseDeadline(ar.deadlineRaw);
        return ar;
    }

    private String safe(String[] p, int i) {
        if (p == null || i < 0 || i >= p.length) return "";
        return p[i] == null ? "" : p[i].trim();
    }

    private Date tryParseDeadline(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;

        DateFormat[] fmts = new DateFormat[] {
                new SimpleDateFormat("MM/dd/yyyy"),
                new SimpleDateFormat("yyyy-MM-dd"),
                new SimpleDateFormat("MM-dd-yyyy")
        };
        for (DateFormat f : fmts) f.setLenient(false);

        for (DateFormat f : fmts) {
            try { return f.parse(t); } catch (Exception ignored) {}
        }

        String[] tokens = t.split("[\\s,]+");
        for (String tok : tokens) {
            for (DateFormat f : fmts) {
                try { return f.parse(tok.trim()); } catch (Exception ignored) {}
            }
        }

        return null;
    }

    static class AssignmentRecord {
        String professor;
        String course;
        String title;
        String description;
        String deadlineRaw;
        Date deadlineDate;
        boolean completed;
    }

    static class RoundedPanel extends JPanel {
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


    private static void addTable(JPanel card, String[] columns, List<String[]> dataList, int x, int y, int w, int h) {
        String[][] data = dataList.toArray(new String[0][]);
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(x, y, w, h);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        card.add(scroll);
    }

    static class RoundedPanel extends JPanel {
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

    public StudentGUI(Student student) {
        this.student = student;
    }
}
