public class Deductions {
    private double salary;
    public Deductions(double salary) { this.salary = salary; }
    public double calculateSSS()        { return salary * 0.045; }
    public double calculatePhilHealth() { return salary * 0.03;  }
    public double calculatePagIbig()    { return salary * 0.02;  }
    public double calculateTax()        { return salary * 0.10;  }
    public double calculateOtherDeductions() { return 50.0; }
}
