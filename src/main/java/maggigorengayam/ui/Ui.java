package maggigorengayam.ui;

import java.util.List;
import java.util.Scanner;

import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;

/**
 * Handles all interaction with the user: reading command lines from
 * stdin and printing every message the program shows on screen (the
 * welcome banner, task confirmations, the divider line, errors). Command
 * handling talks to this instead of calling System.out/Scanner directly,
 * so "how things look on screen" has one home and can change without
 * touching command logic.
 *
 * <p>Most show* methods print only their message, with no leading/trailing
 * divider line: the main loop now brackets every command's output with one
 * {@link #showLine()} before and one after (see MaggiGorengAyam.main()), so
 * a command's own output doesn't need to draw its own divider. The
 * exceptions are {@link #showWelcome()}, {@link #showLoadWarning(int)}, and
 * {@link #showLoadingError()}, which are printed once during startup,
 * outside that per-command bracket, and so still draw their own.
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

    /** Prints the divider line shown before and after every command's output. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Prints the startup welcome banner. */
    public void showWelcome() {
        System.out.println(LINE + "\n"
                + "  __  __  _____    _    \n"
                + " |  \\/  |/ ____|  / \\   \n"
                + " | \\  / ||   __  / _ \\  \n"
                + " | |\\/| ||  |_ |/ ___ \\ \n"
                + " |_|  |_|\\_____/_/   \\_\\\n"
                + "Hello! I'm Maggi Goreng Ayam.\n"
                + "What can I do for you?\n"
                + LINE);
    }

    /** Prints the farewell message shown when the program exits. */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /** Prints {@code message} prefixed with "OOPS!!!". */
    public void showError(String message) {
        System.out.println(" OOPS!!! " + message);
    }

    /** Printed during startup, outside the per-command bracket - see the class doc. */
    public void showLoadWarning(int skippedLineCount) {
        System.out.println(LINE);
        System.out.println(" OOPS!!! " + skippedLineCount
                + " saved task(s) in the data file could not be read and were skipped.");
        System.out.println(LINE);
    }

    /** Printed during startup, outside the per-command bracket - see the class doc. */
    public void showLoadingError() {
        System.out.println(LINE);
        System.out.println(" OOPS!!! I couldn't load saved tasks from disk. Starting with an empty list.");
        System.out.println(LINE);
    }

    /** Prints a warning that a task-list save to disk failed. */
    public void showSaveError() {
        System.out.println(" OOPS!!! I couldn't save the task list to disk. Your change is only in memory for now.");
    }

    /** Prints every task in {@code tasks}, numbered from 1. */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Prints {@code matches}, numbered from 1, under a header naming {@code dateLabel}. */
    public void showTasksOn(String dateLabel, List<Task> matches) {
        System.out.println(" Here are the tasks on " + dateLabel + ":");
        int count = 0;
        for (Task task : matches) {
            count++;
            System.out.println(" " + count + "." + task);
        }
    }


    /** Prints {@code matches}, numbered from 1, as the tasks found by the {@code find} command. */
    public void showMatchingTasks(List<Task> matches) {
        System.out.println(" Here are the matching tasks in your list:");
        int count = 0;
        for (Task task : matches) {
            count++;
            System.out.println(" " + count + "." + task);
        }
    }

    /** Prints confirmation that {@code task} was added, and the new task count. */
    public void showAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints confirmation that {@code task} was removed, and the new task count. */
    public void showRemoved(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints confirmation that {@code task} was marked as done. */
    public void showMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /** Prints confirmation that {@code task} was marked as not done. */
    public void showUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }
}
