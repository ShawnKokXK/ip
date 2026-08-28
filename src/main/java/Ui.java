import java.util.List;
import java.util.Scanner;

/**
 * Handles all interaction with the user: reading command lines from
 * stdin and printing every message the program shows on screen (the
 * welcome banner, task confirmations, the divider line, errors). Command
 * handling talks to this instead of calling System.out/Scanner directly,
 * so "how things look on screen" has one home and can change without
 * touching command logic.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads one line of input, with leading/trailing whitespace trimmed. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    public void close() {
        scanner.close();
    }

    public void showWelcome() {
        System.out.println(LINE + "\n" +
                "  __  __  _____    _    \n" +
                " |  \\/  |/ ____|  / \\   \n" +
                " | \\  / ||   __  / _ \\  \n" +
                " | |\\/| ||  |_ |/ ___ \\ \n" +
                " |_|  |_|\\_____/_/   \\_\\\n" +
                "Hello! I'm Maggi Goreng Ayam.\n" +
                "What can I do for you?\n" +
                LINE);
    }

    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    public void showError(String message) {
        System.out.println(LINE);
        System.out.println(" OOPS!!! " + message);
        System.out.println(LINE);
    }

    public void showLoadWarning(int skippedLineCount) {
        System.out.println(LINE);
        System.out.println(" OOPS!!! " + skippedLineCount
                + " saved task(s) in the data file could not be read and were skipped.");
        System.out.println(LINE);
    }

    public void showLoadingError() {
        System.out.println(LINE);
        System.out.println(" OOPS!!! I couldn't load saved tasks from disk. Starting with an empty list.");
        System.out.println(LINE);
    }

    public void showSaveError() {
        System.out.println(LINE);
        System.out.println(" OOPS!!! I couldn't save the task list to disk. Your change is only in memory for now.");
        System.out.println(LINE);
    }

    public void showTaskList(TaskList tasks) {
        System.out.println(LINE);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        System.out.println(LINE);
    }

    public void showTasksOn(String dateLabel, List<Task> matches) {
        System.out.println(LINE);
        System.out.println(" Here are the tasks on " + dateLabel + ":");
        int count = 0;
        for (Task task : matches) {
            count++;
            System.out.println(" " + count + "." + task);
        }
        System.out.println(LINE);
    }

    public void showAdded(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    public void showRemoved(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    public void showMarked(Task task) {
        System.out.println(LINE);
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
        System.out.println(LINE);
    }

    public void showUnmarked(Task task) {
        System.out.println(LINE);
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
        System.out.println(LINE);
    }
}
