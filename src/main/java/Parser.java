import java.time.format.DateTimeParseException;

/**
 * Turns one raw command line into a {@link Command}, ready for
 * MaggiGorengAyam's main loop to execute without needing to know how any
 * particular command works. Parser only knows about the text the user
 * typed - it has no TaskList, so a command that needs a task number (e.g.
 * `mark 5`) is built with the number as typed, syntactically validated
 * (a whole number) but not yet range-checked; that check happens inside
 * the command's own execute(), once it has a live TaskList.
 */
public class Parser {
    public static Command parse(String command) throws MaggiGorengAyamException {
        if (command.equals("bye")) {
            return new ExitCommand();
        }
        if (command.equals("list")) {
            return new ListCommand();
        }
        if (command.equals("on") || command.startsWith("on ")) {
            return parseOn(command);
        }
        if (command.equals("mark") || command.startsWith("mark ")) {
            return new MarkCommand(parseTaskNumber(command.substring(4).trim(), "mark"));
        }
        if (command.equals("unmark") || command.startsWith("unmark ")) {
            return new UnmarkCommand(parseTaskNumber(command.substring(6).trim(), "unmark"));
        }
        if (command.equals("delete") || command.startsWith("delete ")) {
            return new DeleteCommand(parseTaskNumber(command.substring(6).trim(), "delete"));
        }
        if (command.equals("todo") || command.startsWith("todo ")) {
            return new AddCommand(parseTodo(command));
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return new AddCommand(parseDeadline(command));
        }
        if (command.equals("event") || command.startsWith("event ")) {
            return new AddCommand(parseEvent(command));
        }
        throw new MaggiGorengAyamException("Huhhh???");
    }

    private static Command parseOn(String command) throws MaggiGorengAyamException {
        String dateArg = command.substring(2).trim();
        if (dateArg.isEmpty()) {
            throw new MaggiGorengAyamException("Which date? e.g. 'on 2019-12-02'.");
        }
        try {
            return new OnCommand(DateTimeUtil.parseDateOnly(dateArg));
        } catch (DateTimeParseException e) {
            throw new MaggiGorengAyamException(
                    "I don't understand '" + dateArg + "' as a date. Please use yyyy-MM-dd, e.g. 'on 2019-12-02'.");
        }
    }

    private static Task parseTodo(String command) throws MaggiGorengAyamException {
        String description = command.substring(4).trim();
        if (description.isEmpty()) {
            throw new MaggiGorengAyamException("What TODO you want bro, I'll give you maggi goreng ayam");
        }
        requireNoPipeCharacter(description);
        return new ToDo(description);
    }

    private static Task parseDeadline(String command) throws MaggiGorengAyamException {
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
        return new Deadline(description, parsedBy.date, parsedBy.time);
    }

    private static Task parseEvent(String command) throws MaggiGorengAyamException {
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
        return new Event(description, parsedFrom.date, parsedFrom.time, parsedTo.date, parsedTo.time);
    }

    /**
     * Parses a 1-indexed task number typed after "mark"/"unmark"/"delete".
     * Only checks that the text is a well-formed whole number; whether
     * that number actually exists in the current list is a range check
     * the command's execute() does once it has a live TaskList.
     */
    private static int parseTaskNumber(String arg, String commandName) throws MaggiGorengAyamException {
        if (arg.isEmpty()) {
            throw new MaggiGorengAyamException(
                    "Please specify which task to " + commandName + ", e.g. '" + commandName + " 2'.");
        }
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new MaggiGorengAyamException("The task number must be a whole number, e.g. '" + commandName + " 2'.");
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
}
