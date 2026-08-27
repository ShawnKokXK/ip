public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public void markAsDone() {
        isDone = true;
    }

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

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
