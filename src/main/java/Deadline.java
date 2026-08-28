import java.time.LocalDate;
import java.time.LocalTime;

public class Deadline extends Task {
    protected LocalDate byDate;
    protected LocalTime byTime;

    public Deadline(String description, LocalDate byDate, LocalTime byTime) {
        super(description);
        this.byDate = byDate;
        this.byTime = byTime;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return byDate.equals(date);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeUtil.formatForDisplay(byDate, byTime) + ")";
    }

    @Override
    public String toSaveFormat() {
        return "D | " + getStatusFlag() + " | " + description + " | " + DateTimeUtil.formatForSave(byDate, byTime);
    }
}
