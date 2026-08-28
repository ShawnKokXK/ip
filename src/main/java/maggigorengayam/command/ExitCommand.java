package maggigorengayam.command;

import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The `bye` command: says goodbye and signals the main loop to stop. */
public class ExitCommand extends Command {
    /** Prints the farewell message; the loop itself exits via {@link #isExit}. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Always {@code true}: this is the one command that ends the main loop. */
    @Override
    public boolean isExit() {
        return true;
    }
}
