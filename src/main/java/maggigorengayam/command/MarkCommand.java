package maggigorengayam.command;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The `mark <n>` command: marks the n-th task (1-indexed) as done. */
public class MarkCommand extends Command {
    private final int taskNumber;

    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MaggiGorengAyamException {
        int index = toValidIndex(taskNumber, tasks.size());
        tasks.get(index).markAsDone();
        if (saveTasks(storage, tasks, ui)) {
            ui.showMarked(tasks.get(index));
        }
    }
}
