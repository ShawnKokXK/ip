package maggigorengayam.task;

import java.time.LocalDate;

/**
 * A single to-do item shared by every task type: a description and a
 * done/not-done status. {@link ToDo}/{@link Deadline}/{@link Event} each
 * add their own date(s) on top of this.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /** Creates a not-done task with the given description. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** The task's description, as typed by the user. */
    public String getDescription() {
        return description;
    }

    /** "X" if done, otherwise a blank space - the console display icon. */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns "1"/"0" for the done flag as stored on disk, distinct from
     * {@link #getStatusIcon()} which is "X"/" " for console display.
     */
    protected String getStatusFlag() {
        return isDone ? "1" : "0";
    }

    /**
     * Encodes this task as one line of the on-disk save format. Each
     * concrete task type (ToDo/Deadline/Event) overrides this since the
     * fields to save differ by type; Task itself is never saved directly.
     */
    public String toSaveFormat() {
        throw new UnsupportedOperationException("Task subclasses must override toSaveFormat()");
    }

    /**
     * Whether this task occurs on the given date, used by the
     * {@code on <date>} command. A plain Task/ToDo has no date, so it
     * never matches; Deadline and Event override this with their own
     * date(s).
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** e.g. "[ ] read book" or "[X] read book" - each task type prepends its own type-letter prefix to this. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
