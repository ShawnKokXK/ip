package maggigorengayam.command;

import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The `list` command: shows every task currently in the list. */
public class ListCommand extends Command {
    /** Prints every task currently in {@code tasks}. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
