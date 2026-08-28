package maggigorengayam.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.command.AddCommand;
import maggigorengayam.command.Command;
import maggigorengayam.command.ExitCommand;
import maggigorengayam.command.ListCommand;
import maggigorengayam.command.OnCommand;
import maggigorengayam.storage.Storage;
import maggigorengayam.task.ToDo;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/**
 * Parser.parse() is exercised here mainly by executing the Command it
 * returns against a real TaskList and checking the resulting state, since
 * the Command subclasses expose no getters of their own - execute() is
 * their only public behaviour. Storage is pointed at a JUnit @TempDir so
 * AddCommand/MarkCommand/etc. can save without touching real project files.
 */
public class ParserTest {

    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    public void parse_bye_returnsExitCommandThatSignalsExit() throws MaggiGorengAyamException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_list_returnsListCommandThatDoesNotSignalExit() throws MaggiGorengAyamException {
        Command command = Parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_validTodo_addsToDoWithGivenDescription() throws MaggiGorengAyamException {
        Command command = Parser.parse("todo read book");
        TaskList tasks = new TaskList();

        assertInstanceOf(AddCommand.class, command);
        command.execute(tasks, new Ui(), newStorage());

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void parse_todoEmptyDescription_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_todoDescriptionContainingPipeCharacter_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("todo buy | milk"));
    }

    @Test
    public void parse_validDeadlineDateOnly_addsDeadlineWithGivenDateAndNoTime() throws MaggiGorengAyamException {
        Command command = Parser.parse("deadline return book /by 2019-12-02");
        TaskList tasks = new TaskList();

        command.execute(tasks, new Ui(), newStorage());

        assertEquals("[D][ ] return book (by: Dec 2 2019)", tasks.get(0).toString());
    }

    @Test
    public void parse_validDeadlineWithTime_addsDeadlineWithGivenDateAndTime() throws MaggiGorengAyamException {
        Command command = Parser.parse("deadline return book /by 2019-12-02 1800");
        TaskList tasks = new TaskList();

        command.execute(tasks, new Ui(), newStorage());

        assertEquals("[D][ ] return book (by: Dec 2 2019, 6pm)", tasks.get(0).toString());
    }

    @Test
    public void parse_deadlineMissingByMarker_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("deadline return book"));
    }

    @Test
    public void parse_deadlineInvalidDateValue_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("deadline return book /by not-a-date"));
    }

    @Test
    public void parse_validEvent_addsEventWithGivenFromAndTo() throws MaggiGorengAyamException {
        Command command = Parser.parse("event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        TaskList tasks = new TaskList();

        command.execute(tasks, new Ui(), newStorage());

        assertEquals("[E][ ] project meeting (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)", tasks.get(0).toString());
    }

    @Test
    public void parse_eventMissingFromMarker_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("event meeting"));
    }

    @Test
    public void parse_eventMissingToMarker_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("event meeting /from Mon"));
    }

    @Test
    public void parse_validMark_marksTaskAtGivenOneIndexedPosition() throws MaggiGorengAyamException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task"));

        Command command = Parser.parse("mark 1");
        command.execute(tasks, new Ui(), newStorage());

        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    public void parse_validUnmark_unmarksTaskAtGivenOneIndexedPosition() throws MaggiGorengAyamException {
        TaskList tasks = new TaskList();
        ToDo task = new ToDo("task");
        task.markAsDone();
        tasks.add(task);

        Command command = Parser.parse("unmark 1");
        command.execute(tasks, new Ui(), newStorage());

        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    public void parse_validDelete_removesTaskAtGivenOneIndexedPosition() throws MaggiGorengAyamException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task"));

        Command command = Parser.parse("delete 1");
        command.execute(tasks, new Ui(), newStorage());

        assertEquals(0, tasks.size());
    }

    @Test
    public void parse_markMissingTaskNumber_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("mark"));
    }

    @Test
    public void parse_markNonNumericTaskNumber_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("mark abc"));
    }

    @Test
    public void parse_validOnDate_returnsOnCommand() throws MaggiGorengAyamException {
        Command command = Parser.parse("on 2019-12-02");

        assertInstanceOf(OnCommand.class, command);
    }

    @Test
    public void parse_onMissingDate_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("on"));
    }

    @Test
    public void parse_onInvalidDateFormat_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("on not-a-date"));
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        assertThrows(MaggiGorengAyamException.class, () -> Parser.parse("blah"));
    }
}
