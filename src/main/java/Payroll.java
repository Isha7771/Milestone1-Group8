public class Payroll {
    private Employee employee;
    private Deductions ded;
    private double sss, philHealth, pagIbig, tax, other;

    public Payroll(Employee employee) {
        this.employee = employee;
        this.ded      = new Deductions(employee.getSalary());
    }
    public void computePayroll() {
        sss        = ded.calculateSSS();
        philHealth = ded.calculatePhilHealth();
        pagIbig    = ded.calculatePagIbig();
        tax        = ded.calculateTax();
        other      = ded.calculateOtherDeductions();
    }
    public double getSSS()        { return sss; }
    public double getPhilHealth() { return philHealth; }
    public double getPagIbig()    { return pagIbig; }
    public double getTax()        { return tax; }
    public double getOther()      { return other; }
    public double getDeductions(){ return sss + philHealth + pagIbig + tax + other; }
    public double getNetPay()     { return employee.getSalary() - getDeductions(); }
}
