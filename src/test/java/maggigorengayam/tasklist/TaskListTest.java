package maggigorengayam.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import maggigorengayam.task.Task;

public class TaskListTest {

    @Test
    public void remove_middleIndex_returnsRemovedTaskAndShiftsLaterTasksDown() {
        TaskList taskList = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");
        Task third = new Task("third");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        Task removed = taskList.remove(1);

        assertSame(second, removed);
        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(third, taskList.get(1));
    }

    @Test
    public void remove_firstIndex_returnsFirstTaskAndRemainingTasksShiftDown() {
        TaskList taskList = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.remove(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    public void remove_lastIndex_returnsLastTaskAndSizeDecrements() {
        TaskList taskList = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.remove(1);

        assertSame(second, removed);
        assertEquals(1, taskList.size());
        assertSame(first, taskList.get(0));
    }

    @Test
    public void remove_onlyTaskInList_listBecomesEmpty() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("only task"));

        Task removed = taskList.remove(0);

        assertEquals("only task", removed.getDescription());
        assertEquals(0, taskList.size());
        assertTrue(taskList.getAll().isEmpty());
    }

    @Test
    public void remove_negativeIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("task"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(-1));
    }

    @Test
    public void remove_indexEqualToSize_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("task"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(1));
    }

    @Test
    public void remove_fromEmptyList_exceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(0));
    }
}
