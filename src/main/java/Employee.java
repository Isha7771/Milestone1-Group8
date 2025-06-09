public class Employee {
    private int id;
    private String lastName;
    private String firstName;
    private String position;
    private double salary;
    private String employmentType;
    private String sssNumber;
    private String philHealthNumber;
    private String tin;
    private String pagIbigNumber;
    private LoginSession login;

    public Employee(int id,
                    String lastName,
                    String firstName,
                    String position,
                    double salary,
                    String employmentType,
                    String sssNumber,
                    String philHealthNumber,
                    String tin,
                    String pagIbigNumber,
                    LoginSession login) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.position = position;
        this.salary = salary;
        this.employmentType = employmentType;
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tin = tin;
        this.pagIbigNumber = pagIbigNumber;
        this.login = login;
    }

    public int getId() { return id; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getName() { return firstName + " " + lastName; }
    public String getPosition() { return position; }
    public double getSalary() { return salary; }
    public String getEmploymentType() { return employmentType; }
    public String getSssNumber() { return sssNumber; }
    public String getPhilHealthNumber() { return philHealthNumber; }
    public String getTin() { return tin; }
    public String getPagIbigNumber() { return pagIbigNumber; }
    public LoginSession getLogin() { return login; }
}
