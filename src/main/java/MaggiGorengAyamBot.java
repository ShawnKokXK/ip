import java.io.IOException;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.command.Command;
import maggigorengayam.parser.Parser;
import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/**
 * Runs the same parse-execute pipeline as the CLI's {@code MaggiGorengAyam}
 * entry point, but as a request/response call the GUI can drive one
 * message at a time instead of a blocking stdin loop. The task list and
 * its on-disk save file are shared with the CLI, so either front end sees
 * changes made by the other.
 */
public class MaggiGorengAyamBot {
    private static final String DATA_FILE_PATH = "data/maggigorengayam.txt";

    private final Ui ui = new Ui();
    private final Storage storage = new Storage(DATA_FILE_PATH);
    private final TaskList tasks;
    private final String startupMessage;
    private boolean isExit = false;

    /** Loads any previously saved tasks, noting a startup message to show once the GUI opens. */
    public MaggiGorengAyamBot() {
        String welcome = ui.showWelcome();
        TaskList loadedTasks;
        String warning = null;
        try {
            Storage.LoadResult result = storage.load();
            loadedTasks = new TaskList(result.tasks);
            if (result.skippedLineCount > 0) {
                warning = ui.showLoadWarning(result.skippedLineCount);
            }
        } catch (IOException e) {
            warning = ui.showLoadingError();
            loadedTasks = new TaskList();
        }
        this.tasks = loadedTasks;
        this.startupMessage = warning == null ? welcome : welcome + "\n" + warning;
    }

    /** Returns the message to show as soon as the GUI opens (welcome banner, plus any load warning). */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Parses and executes {@code input} against the live task list, returning
     * the resulting message. {@link #isExit()} reports {@code true} after this
     * returns from an exit command (e.g. {@code bye}).
     */
    public String getResponse(String input) {
        try {
            Command c = Parser.parse(input);
            String response = c.execute(tasks, ui, storage);
            isExit = c.isExit();
            return response;
        } catch (MaggiGorengAyamException e) {
            return ui.showError(e.getMessage());
        }
    }

    /** Whether the most recently executed command should end the session (e.g. {@code bye}). */
    public boolean isExit() {
        return isExit;
    }
}
