import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MaggiGorengAyam {
    private static final String LINE = "____________________________________________________________";
    private static final String DATA_FILE_PATH = "data/maggigorengayam.txt";

    public static void main(String[] args) {
        String banner = LINE + "\n" +
                "  __  __  _____    _    \n" +
                " |  \\/  |/ ____|  / \\   \n" +
                " | \\  / ||   __  / _ \\  \n" +
                " | |\\/| ||  |_ |/ ___ \\ \n" +
                " |_|  |_|\\_____/_/   \\_\\\n" +
                "Hello! I'm Maggi Goreng Ayam.\n" +
                "What can I do for you?\n" +
                LINE;
        System.out.println(banner);

        Storage storage = new Storage(DATA_FILE_PATH);
        // Tasks saved by a previous run (if any) are loaded back in here;
        // a missing/first-time data file just means an empty starting list.
        TaskList tasks;
        try {
            Storage.LoadResult result = storage.load();
            tasks = new TaskList(result.tasks);
            if (result.skippedLineCount > 0) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! " + result.skippedLineCount
                        + " saved task(s) in the data file could not be read and were skipped.");
                System.out.println(LINE);
            }
        } catch (IOException e) {
            System.out.println(LINE);
            System.out.println(" OOPS!!! I couldn't load saved tasks from disk. Starting with an empty list.");
            System.out.println(LINE);
            tasks = new TaskList();
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) {
                continue;
            }
            try {
                if (command.equals("bye")) {
                    System.out.println(LINE);
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;
                }
                if (command.equals("list")) {
                    System.out.println(LINE);
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                    System.out.println(LINE);
                    continue;
                }
                if (command.equals("on") || command.startsWith("on ")) {
                    String dateArg = command.substring(2).trim();
                    if (dateArg.isEmpty()) {
                        throw new MaggiGorengAyamException("Which date? e.g. 'on 2019-12-02'.");
                    }
                    LocalDate queryDate;
                    try {
                        queryDate = DateTimeUtil.parseDateOnly(dateArg);
                    } catch (DateTimeParseException e) {
                        throw new MaggiGorengAyamException(
                                "I don't understand '" + dateArg
                                        + "' as a date. Please use yyyy-MM-dd, e.g. 'on 2019-12-02'.");
                    }
                    System.out.println(LINE);
                    System.out.println(" Here are the tasks on " + DateTimeUtil.formatDateOnlyForDisplay(queryDate) + ":");
                    int count = 0;
                    for (Task task : tasks.getAll()) {
                        if (task.occursOn(queryDate)) {
                            count++;
                            System.out.println(" " + count + "." + task);
                        }
                    }
                    System.out.println(LINE);
                    continue;
                }
                if (command.equals("mark") || command.startsWith("mark ")) {
                    int index = parseTaskIndex(command.substring(4).trim(), tasks.size(), "mark");
                    tasks.get(index).markAsDone();
                    if (!saveTasks(storage, tasks)) {
                        continue;
                    }
                    System.out.println(LINE);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(index));
                    System.out.println(LINE);
                    continue;
                }
                if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int index = parseTaskIndex(command.substring(6).trim(), tasks.size(), "unmark");
                    tasks.get(index).markAsNotDone();
                    if (!saveTasks(storage, tasks)) {
                        continue;
                    }
                    System.out.println(LINE);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(index));
                    System.out.println(LINE);
                    continue;
                }
                if (command.equals("delete") || command.startsWith("delete ")) {
                    int index = parseTaskIndex(command.substring(6).trim(), tasks.size(), "delete");
                    Task removed = tasks.remove(index);
                    if (!saveTasks(storage, tasks)) {
                        continue;
                    }
                    printRemoved(removed, tasks.size());
                    continue;
                }
                if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new MaggiGorengAyamException("What TODO you want bro, I'll give you maggi goreng ayam");
                    }
                    requireNoPipeCharacter(description);
                    tasks.add(new ToDo(description));
                    if (!saveTasks(storage, tasks)) {
                        continue;
                    }
                    printAdded(tasks.get(tasks.size() - 1), tasks.size());
                    continue;
                }
                if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String rest = command.substring(8).trim();
                    if (rest.isEmpty()) {
                        throw new MaggiGorengAyamException("Woah I don't know how to read mind bro, please type in ur description and deadline");
                    }
                    if (!rest.contains(" /by ")) {
                        throw new MaggiGorengAyamException(
                                "Yo, put the deadline using '/by', e.g. 'deadline return book /by Sunday'. Dont make me put the deadline next min.");
                    }
                    String[] parts = rest.split(" /by ", 2);
                    String description = parts[0].trim();
                    String by = parts[1].trim();
                    if (description.isEmpty()) {
                        throw new MaggiGorengAyamException("only date no description?? what you want bro?");
                    }
                    if (by.isEmpty()) {
                        throw new MaggiGorengAyamException("No deadline?? say that to your gf thanks");
                    }
                    requireNoPipeCharacter(description);
                    DateTimeUtil.ParsedDateTime parsedBy = parseDateField(by, "deadline date");
                    tasks.add(new Deadline(description, parsedBy.date, parsedBy.time));
                    if (!saveTasks(storage, tasks)) {
                        continue;
                    }
                    printAdded(tasks.get(tasks.size() - 1), tasks.size());
                    continue;
                }
                if (command.equals("event") || command.startsWith("event ")) {
                    String rest = command.substring(5).trim();
                    if (rest.isEmpty()) {
                        throw new MaggiGorengAyamException("Huhhhh, sry i got no telepathy feature...");
                    }
                    if (!rest.contains(" /from ")) {
                        throw new MaggiGorengAyamException(
                                "Please use '/from', e.g. "
                                        + "'event project meeting /from Mon 2pm /to 4pm'.");
                    }
                    String[] fromParts = rest.split(" /from ", 2);
                    String description = fromParts[0].trim();
                    String afterFrom = fromParts[1].trim();
                    if (description.isEmpty()) {
                        throw new MaggiGorengAyamException("what u want? where is the description??");
                    }
                    if (!afterFrom.contains(" /to ")) {
                        throw new MaggiGorengAyamException(
                                "Till when? forever? Please use '/to', e.g. "
                                        + "'event project meeting /from Mon 2pm /to 4pm'.");
                    }
                    String[] toParts = afterFrom.split(" /to ", 2);
                    String from = toParts[0].trim();
                    String to = toParts[1].trim();
                    if (from.isEmpty()) {
                        throw new MaggiGorengAyamException("From what?? Specify a start time after '/from'.");
                    }
                    if (to.isEmpty()) {
                        throw new MaggiGorengAyamException("To what?? Specify an end time after '/to'.");
                    }
                    requireNoPipeCharacter(description);
                    DateTimeUtil.ParsedDateTime parsedFrom = parseDateField(from, "start date/time");
                    DateTimeUtil.ParsedDateTime parsedTo = parseDateField(to, "end date/time");
                    tasks.add(new Event(description, parsedFrom.date, parsedFrom.time, parsedTo.date, parsedTo.time));
                    if (!saveTasks(storage, tasks)) {
                        continue;
                    }
                    printAdded(tasks.get(tasks.size() - 1), tasks.size());
                    continue;
                }
                throw new MaggiGorengAyamException("Huhhh???");
            } catch (MaggiGorengAyamException e) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! " + e.getMessage());
                System.out.println(LINE);
            }
        }
        scanner.close();
    }

    /**
     * Saves the current task list to disk, reporting an OOPS message on
     * failure (e.g. the data directory could not be created/written to)
     * instead of letting an IOException crash the whole program.
     *
     * @return true if the save succeeded, false if it failed and an error
     *         was already printed.
     */
    private static boolean saveTasks(Storage storage, TaskList tasks) {
        try {
            storage.save(tasks.getAll());
            return true;
        } catch (IOException e) {
            System.out.println(LINE);
            System.out.println(" OOPS!!! I couldn't save the task list to disk. Your change is only in memory for now.");
            System.out.println(LINE);
            return false;
        }
    }

    /**
     * Rejects a task description that contains the '|' character, since
     * that character is the field delimiter used by
     * {@link Task#toSaveFormat()}/{@link Storage}. Without this check, a
     * description containing " | " would be split into the wrong number of
     * parts on the next load and the whole task would be silently dropped -
     * this check turns that silent data loss into an immediate, explicit
     * error at the point the user enters the offending text. (Dates/times
     * don't need this check: {@link #parseDateField} already rejects any
     * text that isn't a valid date, which includes anything containing '|'.)
     */
    private static void requireNoPipeCharacter(String description) throws MaggiGorengAyamException {
        if (description.contains("|")) {
            throw new MaggiGorengAyamException(
                    "Sorry, the '|' character can't be used in a task description"
                            + " because it's used internally to save your tasks. Please remove it and try again.");
        }
    }

    /**
     * Parses a "yyyy-MM-dd" or "yyyy-MM-dd HHmm" value typed after `/by`,
     * `/from`, or `/to`, converting an unparseable value into a friendly
     * {@link MaggiGorengAyamException} instead of a raw
     * {@link DateTimeParseException}.
     */
    private static DateTimeUtil.ParsedDateTime parseDateField(String value, String fieldLabel)
            throws MaggiGorengAyamException {
        try {
            return DateTimeUtil.parse(value);
        } catch (DateTimeParseException e) {
            throw new MaggiGorengAyamException(
                    "I don't understand '" + value + "' as a " + fieldLabel
                            + ". Please use yyyy-MM-dd, optionally followed by a 24-hour time,"
                            + " e.g. '2019-12-02' or '2019-12-02 1800'.");
        }
    }

    /**
     * Parses a 1-indexed task number typed after a "mark"/"unmark"/"delete"
     * command and converts it to a valid 0-based index into {@code tasks}.
     */
    private static int parseTaskIndex(String arg, int taskCount, String commandName)
            throws MaggiGorengAyamException {
        if (arg.isEmpty()) {
            throw new MaggiGorengAyamException(
                    "Please specify which task to " + commandName + ", e.g. '" + commandName + " 2'.");
        }
        int number;
        try {
            number = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new MaggiGorengAyamException("The task number must be a whole number, e.g. '" + commandName + " 2'.");
        }
        if (number < 1 || number > taskCount) {
            throw new MaggiGorengAyamException(
                    "Task number " + number + " does not exist. You have " + taskCount + " task(s) in the list.");
        }
        return number - 1;
    }

    private static void printAdded(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    private static void printRemoved(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }
}
