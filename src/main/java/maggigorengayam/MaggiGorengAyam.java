package maggigorengayam;

import java.io.IOException;

import maggigorengayam.command.Command;
import maggigorengayam.parser.Parser;
import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/**
 * Entry point for the Maggi Goreng Ayam task-list chatbot. Wires together
 * the {@link Ui}, {@link Storage}, and {@link Parser}, then runs the
 * read-parse-execute loop until an {@code ExitCommand} is parsed or input
 * runs out.
 */
public class MaggiGorengAyam {
    private static final String DATA_FILE_PATH = "data/maggigorengayam.txt";

    /**
     * Loads any previously saved tasks, then repeatedly reads a command
     * line, parses it, and executes it against the live task list until
     * the user types {@code bye} or standard input is exhausted.
     */
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
