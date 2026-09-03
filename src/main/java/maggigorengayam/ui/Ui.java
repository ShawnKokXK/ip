package maggigorengayam.ui;

import java.util.List;
import java.util.Scanner;

import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;

/**
 * Builds every message the program shows to the user (the welcome banner,
 * task confirmations, the divider line, errors), and reads command lines
 * from stdin. Command handling talks to this instead of formatting
 * messages itself, so "how things look" has one home and can change
 * without touching command logic.
 *
 * <p>Each {@code show*} method returns the message as a String rather than
 * printing it, so the same formatting can be reused by both the CLI (which
 * prints what it gets back) and the JavaFX GUI (which displays it as a
 * chat bubble instead).
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private final Scanner scanner;

    /** Opens a Scanner over standard input for reading command lines. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Whether there is another line of input to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads one line of input, with leading/trailing whitespace trimmed. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Closes the underlying input Scanner. */
    public void close() {
        scanner.close();
    }

    /** Returns the divider line shown before and after every command's output. */
    public String showLine() {
        return LINE;
    }

    /** Returns the startup welcome banner. */
    public String showWelcome() {
        return LINE + "\n"
                + "  __  __  _____    _    \n"
                + " |  \\/  |/ ____|  / \\   \n"
                + " | \\  / ||   __  / _ \\  \n"
                + " | |\\/| ||  |_ |/ ___ \\ \n"
                + " |_|  |_|\\_____/_/   \\_\\\n"
                + "Hello! I'm Maggi Goreng Ayam.\n"
                + "What can I do for you?\n"
                + LINE;
    }

    /** Returns the farewell message shown when the program exits. */
    public String showGoodbye() {
        return " Bye. Hope to see you again soon!";
    }

    /** Returns {@code message} prefixed with "OOPS!!!". */
    public String showError(String message) {
        return " OOPS!!! " + message;
    }

    /** Shown once during startup - see the class doc. */
    public String showLoadWarning(int skippedLineCount) {
        return LINE + "\n"
                + " OOPS!!! " + skippedLineCount
                + " saved task(s) in the data file could not be read and were skipped.\n"
                + LINE;
    }

    /** Shown once during startup - see the class doc. */
    public String showLoadingError() {
        return LINE + "\n"
                + " OOPS!!! I couldn't load saved tasks from disk. Starting with an empty list.\n"
                + LINE;
    }

    /** Returns a warning that a task-list save to disk failed. */
    public String showSaveError() {
        return " OOPS!!! I couldn't save the task list to disk. Your change is only in memory for now.";
    }

    /** Returns every task in {@code tasks}, numbered from 1. */
    public String showTaskList(TaskList tasks) {
        StringBuilder result = new StringBuilder(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            result.append("\n ").append(i + 1).append(".").append(tasks.get(i));
        }
        return result.toString();
    }

    /** Returns {@code matches}, numbered from 1, under a header naming {@code dateLabel}. */
    public String showTasksOn(String dateLabel, List<Task> matches) {
        StringBuilder result = new StringBuilder(" Here are the tasks on " + dateLabel + ":");
        int count = 0;
        for (Task task : matches) {
            count++;
            result.append("\n ").append(count).append(".").append(task);
        }
        return result.toString();
    }

    /** Returns {@code matches}, numbered from 1, as the tasks found by the {@code find} command. */
    public String showMatchingTasks(List<Task> matches) {
        StringBuilder result = new StringBuilder(" Here are the matching tasks in your list:");
        int count = 0;
        for (Task task : matches) {
            count++;
            result.append("\n ").append(count).append(".").append(task);
        }
        return result.toString();
    }

    /** Returns confirmation that {@code task} was added, and the new task count. */
    public String showAdded(Task task, int taskCount) {
        return " Got it. I've added this task:\n   " + task
                + "\n Now you have " + taskCount + " tasks in the list.";
    }

    /** Returns confirmation that {@code task} was removed, and the new task count. */
    public String showRemoved(Task task, int taskCount) {
        return " Noted. I've removed this task:\n   " + task
                + "\n Now you have " + taskCount + " tasks in the list.";
    }

    /** Returns confirmation that {@code task} was marked as done. */
    public String showMarked(Task task) {
        return " Nice! I've marked this task as done:\n   " + task;
    }

    /** Returns confirmation that {@code task} was marked as not done. */
    public String showUnmarked(Task task) {
        return " OK, I've marked this task as not done yet:\n   " + task;
    }
}
