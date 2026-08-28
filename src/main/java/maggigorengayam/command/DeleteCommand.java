package maggigorengayam.command;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The `delete <n>` command: removes the n-th task (1-indexed) from the list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MaggiGorengAyamException {
        int index = toValidIndex(taskNumber, tasks.size());
        Task removed = tasks.remove(index);
        if (saveTasks(storage, tasks, ui)) {
            ui.showRemoved(removed, tasks.size());
        }
    }
}
