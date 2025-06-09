import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MotorPHUI extends JFrame {
    private static final String CSV_FILE = "employees.csv";

    // Card identifiers
    private static final String CARD_WELCOME    = "welcome";
    private static final String CARD_LOGIN      = "login";
    private static final String CARD_ADD        = "add";
    private static final String CARD_MANAGE     = "manage";
    private static final String CARD_PAYROLL    = "payroll";
    private static final String CARD_ATTENDANCE = "attendance";

    private CardLayout cardLayout;
    private JPanel     cards;

    // --- Login fields ---
    private JTextField    loginUser;
    private JPasswordField loginPass;

    // --- Add fields ---
    private JTextField    addId, addLast, addFirst, addPosition, addSalary, addType;
    private JTextField    addSSS, addPH, addTIN, addPag, addUser;
    private JPasswordField addPass;

    // --- Manage fields ---
    private DefaultTableModel manageModel;
    private JTable             manageTable;
    private JTextField         mLast, mFirst, mPosition, mSalary, mType;
    private JTextField         mSSS, mPH, mTIN, mPag, mUser;
    private JButton            btnUpdate, btnDelete;

    // --- Payroll labels ---
    private JLabel lblName, lblPositionVal, lblSalaryVal;
    private JLabel lblSSS, lblPHVal, lblPagVal, lblTax, lblOther, lblDeduct, lblNet;

    // --- Attendance fields ---
    private JTextField timeInField, timeOutField;

    private List<Employee> employees = new ArrayList<>();
    private Employee       currentUser;

    public MotorPHUI() {
        super("MotorPH Employee App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        employees = loadEmployees();

        cardLayout = new CardLayout();
        cards      = new JPanel(cardLayout);
        cards.add(makeWelcomePanel(),    CARD_WELCOME);
        cards.add(makeLoginPanel(),      CARD_LOGIN);
        cards.add(makeAddPanel(),        CARD_ADD);
        cards.add(makeManagePanel(),     CARD_MANAGE);
        cards.add(makePayrollPanel(),    CARD_PAYROLL);
        cards.add(makeAttendancePanel(), CARD_ATTENDANCE);

        add(cards);
        cardLayout.show(cards, CARD_WELCOME);
        setVisible(true);
    }

    private JPanel makeWelcomePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridy  = 0;
        p.add(new JLabel("Welcome to MotorPH Employee System"), gbc);

        String[] labels = { "Login", "Add Employee", "Manage Employees" };
        String[] cardsArr  = { CARD_LOGIN, CARD_ADD, CARD_MANAGE };
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i + 1;
            JButton b = new JButton(labels[i]);
            final String dest = cardsArr[i];
            b.addActionListener(e -> cardLayout.show(cards, dest));
            p.add(b, gbc);
        }
        return p;
    }

    private JPanel makeLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx=0; gbc.gridy=0; p.add(new JLabel("Username:"), gbc);
        gbc.gridx=1; loginUser = new JTextField(15); p.add(loginUser, gbc);

        gbc.gridx=0; gbc.gridy=1; p.add(new JLabel("Password:"), gbc);
        gbc.gridx=1; loginPass = new JPasswordField(15); p.add(loginPass, gbc);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2;
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> handleLogin());
        p.add(loginBtn, gbc);

        gbc.gridy=3;
        JButton back = new JButton("Back");
        back.addActionListener(e -> cardLayout.show(cards, CARD_WELCOME));
        p.add(back, gbc);

        return p;
    }

    private void handleLogin() {
        String u = loginUser.getText().trim();
        String p = new String(loginPass.getPassword()).trim();
        for (Employee e : employees) {
            if (e.getLogin().getUsername().equals(u) &&
                e.getLogin().authenticate(p)) {
                currentUser = e;
                populatePayroll(e);
                cardLayout.show(cards, CARD_PAYROLL);
                return;
            }
        }
        JOptionPane.showMessageDialog(this,
            "Invalid credentials", "Login Failed",
            JOptionPane.ERROR_MESSAGE);
    }

    private JPanel makeAddPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        String[] labels = {
          "ID","Last Name","First Name","Position","Salary",
          "Type","SSS#","PhilHealth#","TIN","Pag-IBIG#","Username","Password"
        };
        for (int i=0; i<labels.length; i++) {
            gbc.gridx=0; gbc.gridy=i;
            p.add(new JLabel(labels[i]+":"), gbc);
            gbc.gridx=1;
            if (i == labels.length-1) {
                addPass = new JPasswordField(10);
                p.add(addPass, gbc);
            } else {
                JTextField f = new JTextField(10);
                p.add(f, gbc);
                switch(i){
                    case 0: addId       = f; break;
                    case 1: addLast     = f; break;
                    case 2: addFirst    = f; break;
                    case 3: addPosition = f; break;
                    case 4: addSalary   = f; break;
                    case 5: addType     = f; break;
                    case 6: addSSS      = f; break;
                    case 7: addPH       = f; break;
                    case 8: addTIN      = f; break;
                    case 9: addPag      = f; break;
                    case 10:addUser     = f; break;
                }
            }
        }

        gbc.gridx=0; gbc.gridy=labels.length; gbc.gridwidth=2;
        JButton sub = new JButton("Submit");
        sub.addActionListener(e->{
            try {
                String pwHash = Integer.toString(
                    new String(addPass.getPassword()).hashCode()
                );
                Employee emp = new Employee(
                    Integer.parseInt(addId.getText().trim()),
                    addLast.getText().trim(),
                    addFirst.getText().trim(),
                    addPosition.getText().trim(),
                    Double.parseDouble(addSalary.getText().trim()),
                    addType.getText().trim(),
                    addSSS.getText().trim(),
                    addPH.getText().trim(),
                    addTIN.getText().trim(),
                    addPag.getText().trim(),
                    new LoginSession(addUser.getText().trim(), pwHash)
                );
                employees.add(emp);
                saveEmployees();
                reloadManageTable();
                JOptionPane.showMessageDialog(this, "Employee added.");
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Add Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(sub, gbc);

        gbc.gridy++;
        JButton back = new JButton("Back");
        back.addActionListener(e->cardLayout.show(cards,CARD_WELCOME));
        p.add(back, gbc);

        return p;
    }

    private JPanel makeManagePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        String[] cols = {
          "ID","Last","First","Position","Salary",
          "Type","SSS","Phil","TIN","Pag","User"
        };
        manageModel = new DefaultTableModel(cols, 0);
        manageTable = new JTable(manageModel);
        manageTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        manageTable.getSelectionModel().addListSelectionListener(e->{
            if (!manageTable.getSelectionModel().getValueIsAdjusting()) {
                int r = manageTable.getSelectedRow();
                if (r >= 0) {
                    Employee em = employees.get(r);
                    mLast     .setText(em.getLastName());
                    mFirst    .setText(em.getFirstName());
                    mPosition .setText(em.getPosition());
                    mSalary   .setText(String.valueOf(em.getSalary()));
                    mType     .setText(em.getEmploymentType());
                    mSSS      .setText(em.getSssNumber());
                    lblPHVal  .setText(em.getPhilHealthNumber());
                    mTIN      .setText(em.getTin());
                    mPag      .setText(em.getPagIbigNumber());
                    mUser     .setText(em.getLogin().getUsername());
                    btnUpdate .setEnabled(true);
                    btnDelete .setEnabled(true);
                }
            }
        });

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=4;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.weightx=1; gbc.weighty=0.5;
        p.add(new JScrollPane(manageTable), gbc);

        // detail fields
        gbc.fill=GridBagConstraints.NONE;
        gbc.weightx=0; gbc.weighty=0;
        gbc.gridwidth=1;
        String[] labels = {
            "Last","First","Position","Salary","Type",
            "SSS#","PhilHealth#","TIN","Pag-IBIG#","Username"
        };
        JTextField[] fields = new JTextField[labels.length];
        for (int i=0; i<labels.length; i++) {
            fields[i] = new JTextField(10);
            gbc.gridx=0; gbc.gridy=i+1;
            p.add(new JLabel(labels[i]+":"), gbc);
            gbc.gridx=1; p.add(fields[i], gbc);
        }
        mLast     = fields[0];
        mFirst    = fields[1];
        mPosition = fields[2];
        mSalary   = fields[3];
        mType     = fields[4];
        mSSS      = fields[5];
        lblPHVal  = new JLabel();     // use a JLabel for PhilHealth field
        fields[6] = null;              // not used
        gbc.gridx=1; gbc.gridy=6;
        p.add(lblPHVal, gbc);
        mTIN      = fields[7];
        mPag      = fields[8];
        mUser     = fields[9];

        btnUpdate = new JButton("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e->handleUpdate());
        btnDelete = new JButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e->handleDelete());

        gbc.gridx=2; gbc.gridy=1;
        p.add(btnUpdate, gbc);
        gbc.gridx=3;
        p.add(btnDelete, gbc);

        // Back button (using fields.length, not a nonexistent tflds)
        gbc.gridx     = 0;
        gbc.gridy     = labels.length + 1;
        gbc.gridwidth = 4;
        JButton back = new JButton("Back");
        back.addActionListener(e->cardLayout.show(cards, CARD_WELCOME));
        p.add(back, gbc);

        reloadManageTable();
        return p;
    }

    private JPanel makePayrollPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,6,4,6);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labs = {
          "Name:","Position:","Salary:",
          "SSS:","PhilHealth:","Pag-IBIG:",
          "Tax:","Other:","Total Deduct:","Net Pay:"
        };
        JLabel[] vals = new JLabel[labs.length];
        for (int i=0; i<labs.length; i++) {
            gbc.gridx=0; gbc.gridy=i;
            p.add(new JLabel(labs[i]), gbc);
            vals[i] = new JLabel("-");
            gbc.gridx=1; p.add(vals[i], gbc);
        }
        lblName        = vals[0];
        lblPositionVal = vals[1];
        lblSalaryVal   = vals[2];
        lblSSS         = vals[3];
        lblPHVal       = vals[4];
        lblPagVal      = vals[5];
        lblTax         = vals[6];
        lblOther       = vals[7];
        lblDeduct      = vals[8];
        lblNet         = vals[9];

        gbc.gridx=0; gbc.gridy=labs.length; gbc.gridwidth=2; 
        JButton toAtt = new JButton("Go to Attendance");
        toAtt.addActionListener(e->cardLayout.show(cards, CARD_ATTENDANCE));
        p.add(toAtt, gbc);

        gbc.gridy++;
        JButton logout = new JButton("Log Out");
        logout.addActionListener(e->cardLayout.show(cards, CARD_WELCOME));
        p.add(logout, gbc);

        return p;
    }

    private JPanel makeAttendancePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx=0; gbc.gridy=0; p.add(new JLabel("Time In (HH:MM):"), gbc);
        gbc.gridx=1; timeInField = new JTextField(10); p.add(timeInField, gbc);

        gbc.gridx=0; gbc.gridy=1; p.add(new JLabel("Time Out (HH:MM):"), gbc);
        gbc.gridx=1; timeOutField = new JTextField(10); p.add(timeOutField, gbc);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2;
        JButton calc = new JButton("Compute Hours");
        calc.addActionListener(e->{
            try {
                Attendance att = new Attendance(
                    LocalTime.parse(timeInField.getText().trim()),
                    LocalTime.parse(timeOutField.getText().trim())
                );
                JOptionPane.showMessageDialog(this,
                  "Hours worked: " + att.calculateWorkedHours());
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this,
                  "Invalid format: HH:MM", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(calc, gbc);

        gbc.gridy++;
        JButton back = new JButton("Back to Payroll");
        back.addActionListener(e->cardLayout.show(cards, CARD_PAYROLL));
        p.add(back, gbc);

        return p;
    }

    // ==================  Helpers  ==================

    private void populatePayroll(Employee e) {
        Payroll pr = new Payroll(e);
        pr.computePayroll();
        NumberFormat cf = NumberFormat.getCurrencyInstance(Locale.getDefault());

        lblName       .setText(e.getName());
        lblPositionVal.setText(e.getPosition());
        lblSalaryVal  .setText(cf.format(e.getSalary()));
        lblSSS        .setText(cf.format(pr.getSSS()));
        lblPHVal      .setText(cf.format(pr.getPhilHealth()));
        lblPagVal     .setText(cf.format(pr.getPagIbig()));
        lblTax        .setText(cf.format(pr.getTax()));
        lblOther      .setText(cf.format(pr.getOther()));
        lblDeduct     .setText(cf.format(pr.getDeductions()));
        lblNet        .setText(cf.format(pr.getNetPay()));
    }

    private void handleUpdate() {
        int r = manageTable.getSelectedRow();
        if (r < 0) return;
        try {
            Employee e = employees.get(r);
            e.setLastName(mLast.getText().trim());
            e.setFirstName(mFirst.getText().trim());
            e.setPosition(mPosition.getText().trim());
            e.setSalary(Double.parseDouble(mSalary.getText().trim()));
            e.setEmploymentType(mType.getText().trim());
            e.setSssNumber(mSSS.getText().trim());
            e.setPhilHealthNumber(lblPHVal.getText().trim());
            e.setTin(mTIN.getText().trim());
            e.setPagIbigNumber(mPag.getText().trim());
            e.getLogin().setUsername(mUser.getText().trim());

            saveEmployees();
            reloadManageTable();
            JOptionPane.showMessageDialog(this, "Record updated.");
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this,
              ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int r = manageTable.getSelectedRow();
        if (r < 0) return;
        if (JOptionPane.showConfirmDialog(this,
            "Delete this record?", "Confirm", JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION) {
            employees.remove(r);
            saveEmployees();
            reloadManageTable();
            JOptionPane.showMessageDialog(this, "Record deleted.");
        }
    }

    private void reloadManageTable() {
        manageModel.setRowCount(0);
        for (Employee e : employees) {
            manageModel.addRow(new Object[]{
                e.getId(),
                e.getLastName(),
                e.getFirstName(),
                e.getPosition(),
                e.getSalary(),
                e.getEmploymentType(),
                e.getSssNumber(),
                e.getPhilHealthNumber(),
                e.getTin(),
                e.getPagIbigNumber(),
                e.getLogin().getUsername()
            });
        }
    }

    private List<Employee> loadEmployees() {
        List<Employee> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] t = line.split(",", -1);
                if (t.length < 12) continue;
                list.add(new Employee(
                    Integer.parseInt(t[0].trim()),
                    t[1].trim(), t[2].trim(), t[3].trim(),
                    Double.parseDouble(t[4].trim()), t[5].trim(),
                    t[6].trim(), t[7].trim(), t[8].trim(), t[9].trim(),
                    new LoginSession(t[10].trim(), t[11].trim())
                ));
            }
        } catch (IOException ignored) {}
        return list;
    }

    private void saveEmployees() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            for (Employee e : employees) {
                pw.printf(
                  "%d,%s,%s,%s,%.2f,%s,%s,%s,%s,%s,%s,%s%n",
                  e.getId(),
                  e.getLastName(),
                  e.getFirstName(),
                  e.getPosition(),
                  e.getSalary(),
                  e.getEmploymentType(),
                  e.getSssNumber(),
                  e.getPhilHealthNumber(),
                  e.getTin(),
                  e.getPagIbigNumber(),
                  e.getLogin().getUsername(),
                  e.getLogin().getPasswordHash()
                );
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
              ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MotorPHUI::new);
    }
}
