package maggigorengayam.command;

import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The `bye` command: says goodbye and signals the main loop to stop. */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
