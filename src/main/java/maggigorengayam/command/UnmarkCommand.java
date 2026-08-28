package maggigorengayam.command;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The {@code unmark <n>} command: marks the n-th task (1-indexed) as not done. */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /** Marks the task at the given 1-indexed position as not done once {@link #execute} runs. */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Marks the task at {@code taskNumber} as not done, saves, and reports it. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MaggiGorengAyamException {
        int index = toValidIndex(taskNumber, tasks.size());
        tasks.get(index).markAsNotDone();
        if (saveTasks(storage, tasks, ui)) {
            ui.showUnmarked(tasks.get(index));
        }
    }
}
