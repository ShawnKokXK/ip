package maggigorengayam.command;

import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The `todo`/`deadline`/`event` commands: adds an already-built task to the list. */
public class AddCommand extends Command {
    private final Task taskToAdd;

    public AddCommand(Task taskToAdd) {
        this.taskToAdd = taskToAdd;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(taskToAdd);
        if (saveTasks(storage, tasks, ui)) {
            ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
        }
    }
}
