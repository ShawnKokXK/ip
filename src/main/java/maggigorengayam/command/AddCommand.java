package maggigorengayam.command;

import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The `todo`/`deadline`/`event` commands: adds an already-built task to the list. */
public class AddCommand extends Command {
    private final Task taskToAdd;

    /** Wraps the already-built task to be added once {@link #execute} runs. */
    public AddCommand(Task taskToAdd) {
        this.taskToAdd = taskToAdd;
    }

    /** Adds {@code taskToAdd} to {@code tasks}, saves, and returns a message reporting the addition. */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(taskToAdd);
        if (saveTasks(storage, tasks)) {
            return ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
        }
        return ui.showSaveError();
    }
}
