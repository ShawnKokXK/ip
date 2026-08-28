package maggigorengayam.task;

import java.time.LocalDate;
import java.time.LocalTime;

import maggigorengayam.util.DateTimeUtil;

/** A task spanning a "from" date/time to a "to" date/time. */
public class Event extends Task {
    protected LocalDate fromDate;
    protected LocalTime fromTime;
    protected LocalDate toDate;
    protected LocalTime toTime;

    /** Creates an event with the given description and from/to dates; either time may be {@code null}. */
    public Event(String description, LocalDate fromDate, LocalTime fromTime, LocalDate toDate, LocalTime toTime) {
        super(description);
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
    }

    /** True when {@code date} falls within [fromDate, toDate], inclusive of both ends. */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(fromDate) && !date.isAfter(toDate);
    }

    /** e.g. "[E][ ] project meeting (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)". */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeUtil.formatForDisplay(fromDate, fromTime)
                + " to: "
                + DateTimeUtil.formatForDisplay(toDate, toTime)
                + ")";
    }

    /** e.g. "E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600". */
    @Override
    public String toSaveFormat() {
        return "E | " + getStatusFlag() + " | " + description + " | "
                + DateTimeUtil.formatForSave(fromDate, fromTime) + " | "
                + DateTimeUtil.formatForSave(toDate, toTime);
    }
}
