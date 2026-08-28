import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MaggiGorengAyam {
    private static final String DATA_FILE_PATH = "data/maggigorengayam.txt";

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage(DATA_FILE_PATH);
        // Tasks saved by a previous run (if any) are loaded back in here;
        // a missing/first-time data file just means an empty starting list.
        TaskList tasks;
        try {
            Storage.LoadResult result = storage.load();
            tasks = new TaskList(result.tasks);
            if (result.skippedLineCount > 0) {
                ui.showLoadWarning(result.skippedLineCount);
            }
        } catch (IOException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (command.isEmpty()) {
                continue;
            }
            try {
                ParsedCommand parsed = Parser.parse(command);
                switch (parsed.type) {
                case BYE:
                    ui.showGoodbye();
                    isExit = true;
                    break;
                case LIST:
                    ui.showTaskList(tasks);
                    break;
                case ON: {
                    List<Task> matches = new ArrayList<>();
                    for (Task task : tasks.getAll()) {
                        if (task.occursOn(parsed.onDate)) {
                            matches.add(task);
                        }
                    }
                    ui.showTasksOn(DateTimeUtil.formatDateOnlyForDisplay(parsed.onDate), matches);
                    break;
                }
                case MARK: {
                    int index = toValidIndex(parsed.taskNumber, tasks.size());
                    tasks.get(index).markAsDone();
                    if (saveTasks(storage, tasks, ui)) {
                        ui.showMarked(tasks.get(index));
                    }
                    break;
                }
                case UNMARK: {
                    int index = toValidIndex(parsed.taskNumber, tasks.size());
                    tasks.get(index).markAsNotDone();
                    if (saveTasks(storage, tasks, ui)) {
                        ui.showUnmarked(tasks.get(index));
                    }
                    break;
                }
                case DELETE: {
                    int index = toValidIndex(parsed.taskNumber, tasks.size());
                    Task removed = tasks.remove(index);
                    if (saveTasks(storage, tasks, ui)) {
                        ui.showRemoved(removed, tasks.size());
                    }
                    break;
                }
                case TODO:
                case DEADLINE:
                case EVENT:
                    tasks.add(parsed.taskToAdd);
                    if (saveTasks(storage, tasks, ui)) {
                        ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
                    }
                    break;
                }
            } catch (MaggiGorengAyamException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    /**
     * Saves the current task list to disk, reporting an OOPS message on
     * failure (e.g. the data directory could not be created/written to)
     * instead of letting an IOException crash the whole program.
     *
     * @return true if the save succeeded, false if it failed and an error
     *         was already printed.
     */
    private static boolean saveTasks(Storage storage, TaskList tasks, Ui ui) {
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
     * rather than in Parser (which never sees the TaskList).
     */
    private static int toValidIndex(int number, int taskCount) throws MaggiGorengAyamException {
        if (number < 1 || number > taskCount) {
            throw new MaggiGorengAyamException(
                    "Task number " + number + " does not exist. You have " + taskCount + " task(s) in the list.");
        }
        return number - 1;
    }
}
