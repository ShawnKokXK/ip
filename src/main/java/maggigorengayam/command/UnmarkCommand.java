package maggigorengayam.command;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The `unmark <n>` command: marks the n-th task (1-indexed) as not done. */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MaggiGorengAyamException {
        int index = toValidIndex(taskNumber, tasks.size());
        tasks.get(index).markAsNotDone();
        if (saveTasks(storage, tasks, ui)) {
            ui.showUnmarked(tasks.get(index));
        }
    }
}
