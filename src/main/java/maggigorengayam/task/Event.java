package maggigorengayam.task;

import java.time.LocalDate;
import java.time.LocalTime;

import maggigorengayam.util.DateTimeUtil;

public class Event extends Task {
    protected LocalDate fromDate;
    protected LocalTime fromTime;
    protected LocalDate toDate;
    protected LocalTime toTime;

    public Event(String description, LocalDate fromDate, LocalTime fromTime, LocalDate toDate, LocalTime toTime) {
        super(description);
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(fromDate) && !date.isAfter(toDate);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeUtil.formatForDisplay(fromDate, fromTime)
                + " to: "
                + DateTimeUtil.formatForDisplay(toDate, toTime)
                + ")";
    }

    @Override
    public String toSaveFormat() {
        return "E | " + getStatusFlag() + " | " + description + " | "
                + DateTimeUtil.formatForSave(fromDate, fromTime) + " | "
                + DateTimeUtil.formatForSave(toDate, toTime);
    }
}
