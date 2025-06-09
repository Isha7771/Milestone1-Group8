import java.time.LocalTime;

public class Attendance {
    private int id;
    private String date;
    private LocalTime timeIn;
    private LocalTime timeOut;

    public Attendance(int id, String date, LocalTime timeIn, LocalTime timeOut) {
        this.id = id;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public double calculateWorkedHours() {
        return (timeOut.toSecondOfDay() - timeIn.toSecondOfDay()) / 3600.0;
    }
}
