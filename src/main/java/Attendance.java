import java.time.LocalTime;

public class Attendance {
    private LocalTime timeIn, timeOut;
    public Attendance(LocalTime in, LocalTime out) {
        this.timeIn = in; this.timeOut = out;
    }
    public double calculateWorkedHours() {
        return (timeOut.toSecondOfDay() - timeIn.toSecondOfDay()) / 3600.0;
    }
}
