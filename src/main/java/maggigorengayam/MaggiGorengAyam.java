package maggigorengayam;

import java.io.IOException;

import maggigorengayam.command.Command;
import maggigorengayam.parser.Parser;
import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

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
        // A blank line is checked for, and silently skipped, before the
        // per-command showLine() bracket below - so pressing Enter with
        // nothing typed produces no output at all, rather than an empty
        // divider pair.
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (command.isEmpty()) {
                continue;
            }
            try {
                ui.showLine();
                Command c = Parser.parse(command);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (MaggiGorengAyamException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        ui.close();
    }
}
