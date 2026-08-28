import java.io.IOException;

/**
 * One user command, already understood by {@link Parser} and ready to run
 * against the live task list. Each kind of command (add, mark, delete,
 * list, ...) is its own subclass, so MaggiGorengAyam's main loop no longer
 * needs to know how any particular command works - it just calls
 * {@link #execute}.
 */
public abstract class Command {
    /**
     * Carries out this command: mutating {@code tasks} if needed, saving
     * to {@code storage} if the mutation should persist, and reporting
     * the result through {@code ui}.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MaggiGorengAyamException;

    /** Whether this command should end the program's main loop. Only ExitCommand overrides this. */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the current task list to disk, reporting an OOPS message on
     * failure (e.g. the data directory could not be created/written to)
     * instead of letting an IOException crash the whole program. Shared by
     * every command that mutates the task list.
     *
     * @return true if the save succeeded, false if it failed and an error
     *         was already shown - callers should skip their normal success
     *         message in that case.
     */
    protected static boolean saveTasks(Storage storage, TaskList tasks, Ui ui) {
        try {
            storage.save(tasks.getAll());
            return true;
        } catch (IOException e) {
            ui.showSaveError();
            return false;
        }
    }

    /**
     * Converts a 1-indexed task number that {@link Parser} already
     * confirmed is a well-formed whole number into a valid 0-based index
     * into the current task list, or reports it as out of range. This is
     * a range check against live list state, which is why it lives here
     * rather than in Parser (which never sees the TaskList). Shared by
     * MarkCommand/UnmarkCommand/DeleteCommand.
     */
    protected static int toValidIndex(int number, int taskCount) throws MaggiGorengAyamException {
        if (number < 1 || number > taskCount) {
            throw new MaggiGorengAyamException(
                    "Task number " + number + " does not exist. You have " + taskCount + " task(s) in the list.");
        }
        return number - 1;
    }
}
