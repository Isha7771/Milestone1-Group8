import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.Month;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

public class MotorPHUI extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtTimeIn, txtTimeOut;
    private JLabel lblNameVal, lblPosVal, lblSalaryVal, lblSSSVal, lblPhilHealthVal,
                   lblPagIbigVal, lblTaxVal, lblOtherVal, lblTotalVal, lblNetVal;

    private JTable employeesTable;
    private DefaultTableModel tableModel;
    private JPanel loginPanel, employeesPanel, payrollPanel, attendancePanel;
    private JTabbedPane tabbedPane;
    private List<Employee> employees;
    private final String CSV_FILE = "employees.csv";
    private Employee currentUser;

    public MotorPHUI() {
        setTitle("MotorPH Employee App");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // load existing records
        employees = readEmployeesFromCSV(CSV_FILE);

        tabbedPane = new JTabbedPane();
        createEmployeesTab();
        createLoginTab();
        createPayrollTab();
        createAttendanceTab();

        // lock everything except login until we authenticate
        tabbedPane.setEnabledAt(0, false);
        tabbedPane.setEnabledAt(2, false);
        tabbedPane.setEnabledAt(3, false);

        add(tabbedPane);
        setVisible(true);
    }

    private void createEmployeesTab() {
        employeesPanel = new JPanel(new BorderLayout());
        String[] cols = {"ID","Last Name","First Name","SSS","PhilHealth","TIN","Pag-IBIG"};
        tableModel = new DefaultTableModel(cols, 0);
        for (Employee e : employees) {
            tableModel.addRow(new Object[]{
                e.getId(), e.getLastName(), e.getFirstName(),
                e.getSssNumber(), e.getPhilHealthNumber(),
                e.getTin(), e.getPagIbigNumber()
            });
        }
        employeesTable = new JTable(tableModel);
        employeesPanel.add(new JScrollPane(employeesTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnView = new JButton("View Employee");
        JButton btnNew  = new JButton("New Employee");
        btnPanel.add(btnView);
        btnPanel.add(btnNew);
        employeesPanel.add(btnPanel, BorderLayout.SOUTH);

        btnView.addActionListener(e -> openEmployeeDetail());
        btnNew .addActionListener(e -> openNewEmployeeForm());

        tabbedPane.addTab("Employees", employeesPanel);
    }

    private void openEmployeeDetail() {
        int row = employeesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }
        Employee emp = employees.get(row);

        JDialog dlg = new JDialog(this, "Employee Details & Payroll", true);
        dlg.setSize(400,400);
        dlg.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx=0; gbc.gridy=0; dlg.add(new JLabel("Name: " + emp.getName()), gbc);
        gbc.gridy++;      dlg.add(new JLabel("Position: " + emp.getPosition()), gbc);
        gbc.gridy++;      dlg.add(new JLabel("Salary: " + emp.getSalary()), gbc);

        gbc.gridy++;      dlg.add(new JLabel("Select Month:"), gbc);
        JComboBox<Month> cbMonth = new JComboBox<>(Month.values());
        gbc.gridx=1;      dlg.add(cbMonth, gbc);

        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2;
        JButton btnCompute = new JButton("Compute");
        dlg.add(btnCompute, gbc);

        gbc.gridy++; gbc.gridwidth=2;
        JLabel lblResult = new JLabel();
        dlg.add(lblResult, gbc);

        btnCompute.addActionListener(ae -> {
            Payroll p = new Payroll(emp);
            p.computePayroll();
            NumberFormat cur = NumberFormat.getCurrencyInstance(new Locale("en","PH"));
            StringBuilder sb = new StringBuilder();
            sb.append("SSS: ").append(cur.format(p.getSSS())).append("  ");
            sb.append("PhilHealth: ").append(cur.format(p.getPhilHealth())).append("  ");
            sb.append("Pag-IBIG: ").append(cur.format(p.getPagIbig())).append("\n");
            sb.append("Tax: ").append(cur.format(p.getTax())).append("  ");
            sb.append("Other: ").append(cur.format(p.getOther())).append("  ");
            sb.append("Net Pay: ").append(cur.format(p.getNetPay()));
            lblResult.setText("<html>" + sb.toString().replaceAll("\n","<br>") + "</html>");
        });

        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void openNewEmployeeForm() {
        JDialog dlg = new JDialog(this, "New Employee", true);
        dlg.setSize(350,500);
        dlg.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtId    = new JTextField(10);
        JTextField txtLast  = new JTextField(10);
        JTextField txtFirst = new JTextField(10);
        JTextField txtSSS   = new JTextField(10);
        JTextField txtPH    = new JTextField(10);
        JTextField txtTIN   = new JTextField(10);
        JTextField txtPag   = new JTextField(10);
        JTextField txtPos   = new JTextField(10);
        JTextField txtSal   = new JTextField(10);
        JTextField txtEmpT  = new JTextField(10);
        JTextField txtUser  = new JTextField(10);
        JPasswordField txtPass = new JPasswordField(10);

        String[] labels = {
            "ID:","Last Name:","First Name:","SSS#:","PhilHealth#:",
            "TIN:","Pag-IBIG#:","Position:","Salary:","Emp Type:",
            "Username:","Password:"
        };
        JComponent[] fields = {
            txtId, txtLast, txtFirst, txtSSS, txtPH,
            txtTIN, txtPag, txtPos, txtSal, txtEmpT,
            txtUser, txtPass
        };

        for (int i=0; i<labels.length; i++) {
            gbc.gridx=0; gbc.gridy=i; dlg.add(new JLabel(labels[i]), gbc);
            gbc.gridx=1; dlg.add(fields[i], gbc);
        }

        gbc.gridx=0; gbc.gridy=labels.length; gbc.gridwidth=2;
        JButton btnSubmit = new JButton("Submit");
        dlg.add(btnSubmit, gbc);

        btnSubmit.addActionListener(ae -> {
            try {
                Employee e = new Employee(
                    Integer.parseInt(txtId.getText().trim()),
                    txtLast.getText().trim(),
                    txtFirst.getText().trim(),
                    txtPos.getText().trim(),
                    Double.parseDouble(txtSal.getText().trim()),
                    txtEmpT.getText().trim(),
                    txtSSS.getText().trim(),
                    txtPH.getText().trim(),
                    txtTIN.getText().trim(),
                    txtPag.getText().trim(),
                    new LoginSession(
                        txtUser.getText().trim(),
                        String.valueOf(new String(txtPass.getPassword()).hashCode())
                    )
                );
                employees.add(e);
                tableModel.addRow(new Object[]{
                    e.getId(), e.getLastName(), e.getFirstName(),
                    e.getSssNumber(), e.getPhilHealthNumber(),
                    e.getTin(), e.getPagIbigNumber()
                });
                writeEmployeeToCSV(e, CSV_FILE);
                dlg.dispose();
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid input: " + ex.getMessage());
            }
        });

        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private List<Employee> readEmployeesFromCSV(String file) {
        List<Employee> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tok = line.split(",");
                if (tok.length < 11) continue;
                Employee e = new Employee(
                    Integer.parseInt(tok[0].trim()),
                    tok[1].trim(),
                    tok[2].trim(),
                    tok[8].trim(),
                    Double.parseDouble(tok[7].trim()),
                    tok[9].trim(),
                    tok[3].trim(),
                    tok[4].trim(),
                    tok[5].trim(),
                    tok[6].trim(),
                    new LoginSession(tok[9].trim(), tok[10].trim())
                );
                list.add(e);
            }
        } catch (IOException ignored) {}
        return list;
    }

    private void writeEmployeeToCSV(Employee e, String file) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.printf(
                "%d,%s,%s,%s,%s,%s,%s,%.2f,%s,%s,%s%n",
                e.getId(),
                e.getLastName(), e.getFirstName(),
                e.getSssNumber(), e.getPhilHealthNumber(),
                e.getTin(), e.getPagIbigNumber(),
                e.getSalary(), e.getEmploymentType(),
                e.getLogin().getUsername(), e.getLogin().getPasswordHash()
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving to CSV: " + ex.getMessage());
        }
    }

    private void createLoginTab() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> handleLogin());

        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx=0; gbc.gridy=0; loginPanel.add(lblUsername, gbc);
        gbc.gridx=1;                loginPanel.add(txtUsername, gbc);
        gbc.gridx=0; gbc.gridy=1; loginPanel.add(lblPassword, gbc);
        gbc.gridx=1;                loginPanel.add(txtPassword, gbc);
        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; loginPanel.add(btnLogin, gbc);

        tabbedPane.addTab("Login", loginPanel);
    }

    private void createPayrollTab() {
        payrollPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,6,4,6);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {
            "Name:", "Position:", "Salary:", "SSS:", "PhilHealth:",
            "Pag-IBIG:", "Tax:", "Other:", "Total Deductions:", "Net Pay:"
        };
        JLabel[] values = new JLabel[labels.length];
        for (int i=0; i<labels.length; i++) {
            gbc.gridx=0; gbc.gridy=i; payrollPanel.add(new JLabel(labels[i]), gbc);
            values[i] = new JLabel("-");
            gbc.gridx=1; payrollPanel.add(values[i], gbc);
        }

        lblNameVal      = values[0];
        lblPosVal       = values[1];
        lblSalaryVal    = values[2];
        lblSSSVal       = values[3];
        lblPhilHealthVal= values[4];
        lblPagIbigVal   = values[5];
        lblTaxVal       = values[6];
        lblOtherVal     = values[7];
        lblTotalVal     = values[8];
        lblNetVal       = values[9];

        tabbedPane.addTab("Payroll Info", payrollPanel);
    }

    private void createAttendanceTab() {
        attendancePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblIn  = new JLabel("Time In (HH:MM):");
        JLabel lblOut = new JLabel("Time Out (HH:MM):");
        txtTimeIn  = new JTextField(10);
        txtTimeOut = new JTextField(10);
        JButton btnCalc = new JButton("Compute Hours");
        btnCalc.addActionListener(e -> handleAttendance());

        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx=0; gbc.gridy=0; attendancePanel.add(lblIn, gbc);
        gbc.gridx=1;            attendancePanel.add(txtTimeIn, gbc);
        gbc.gridx=0; gbc.gridy=1; attendancePanel.add(lblOut, gbc);
        gbc.gridx=1;            attendancePanel.add(txtTimeOut, gbc);
        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; attendancePanel.add(btnCalc, gbc);

        tabbedPane.addTab("Attendance", attendancePanel);
    }

    private void handleLogin() {
        try {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty())
                throw new IllegalArgumentException("Fields cannot be empty.");

            for (Employee emp : employees) {
                if (emp.getLogin().getUsername().equals(username)
                 && emp.getLogin().authenticate(password)) {
                    currentUser = emp;
                    Payroll payroll = new Payroll(emp);
                    payroll.computePayroll();

                    NumberFormat cur = NumberFormat.getCurrencyInstance(new Locale("en","PH"));
                    lblNameVal.setText(emp.getName());
                    lblPosVal.setText(emp.getPosition());
                    lblSalaryVal.setText(cur.format(emp.getSalary()));
                    lblSSSVal.setText(cur.format(payroll.getSSS()));
                    lblPhilHealthVal.setText(cur.format(payroll.getPhilHealth()));
                    lblPagIbigVal.setText(cur.format(payroll.getPagIbig()));
                    lblTaxVal.setText(cur.format(payroll.getTax()));
                    lblOtherVal.setText(cur.format(payroll.getOther()));
                    lblTotalVal.setText(cur.format(payroll.getDeductions()));
                    lblNetVal.setText(cur.format(payroll.getNetPay()));

                    // unlock all other tabs
                    tabbedPane.setEnabledAt(0, true);
                    tabbedPane.setEnabledAt(2, true);
                    tabbedPane.setEnabledAt(3, true);
                    tabbedPane.setSelectedIndex(2);
                    return;
                }
            }
            throw new Exception("Invalid login.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAttendance() {
        try {
            LocalTime in  = LocalTime.parse(txtTimeIn.getText());
            LocalTime out = LocalTime.parse(txtTimeOut.getText());
            Attendance att = new Attendance(1, "Today", in, out);
            double hours = att.calculateWorkedHours();
            JOptionPane.showMessageDialog(this, "Hours Worked: " + hours);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Invalid time format. Use HH:MM.",
                "Time Format Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MotorPHUI());
    }
}
