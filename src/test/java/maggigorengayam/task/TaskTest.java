package maggigorengayam.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class TaskTest {

    // ---- Task ----

    @Test
    public void construct_newTask_isNotDone() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getDescription_returnsDescriptionGivenAtConstruction() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
    }

    @Test
    public void markAsDone_notDoneTask_statusIconBecomesX() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_doneTask_statusIconBecomesBlank() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void occursOn_baseTask_alwaysReturnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void toString_notDoneTask_hasBlankStatusIcon() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_doneTask_hasXStatusIcon() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toSaveFormat_baseTask_throwsUnsupportedOperationException() {
        Task task = new Task("read book");

        assertThrows(UnsupportedOperationException.class, task::toSaveFormat);
    }

    // ---- ToDo ----

    @Test
    public void toString_toDo_hasTypeLetterPrefix() {
        ToDo toDo = new ToDo("read book");

        assertEquals("[T][ ] read book", toDo.toString());
    }

    @Test
    public void toSaveFormat_notDoneToDo_encodesTypeStatusAndDescription() {
        ToDo toDo = new ToDo("read book");

        assertEquals("T | 0 | read book", toDo.toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneToDo_encodesDoneStatus() {
        ToDo toDo = new ToDo("read book");
        toDo.markAsDone();

        assertEquals("T | 1 | read book", toDo.toSaveFormat());
    }

    // ---- Deadline ----

    @Test
    public void occursOn_dateEqualToByDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2), null);

        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_dateNotEqualToByDate_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2), null);

        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void toString_deadlineWithoutTime_omitsTimeFromByLabel() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2), null);

        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
    }

    @Test
    public void toString_deadlineWithTime_includesTimeInByLabel() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2), LocalTime.of(18, 0));

        assertEquals("[D][ ] return book (by: Dec 2 2019, 6pm)", deadline.toString());
    }

    @Test
    public void toSaveFormat_deadlineWithoutTime_omitsTimeFromSavedDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2), null);

        assertEquals("D | 0 | return book | 2019-12-02", deadline.toSaveFormat());
    }

    @Test
    public void toSaveFormat_deadlineWithTime_includesTimeInSavedDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2), LocalTime.of(18, 0));

        assertEquals("D | 0 | return book | 2019-12-02 1800", deadline.toSaveFormat());
    }

    // ---- Event ----

    @Test
    public void occursOn_dateBeforeFromDate_returnsFalse() {
        Event event = new Event("meeting", LocalDate.of(2019, 12, 2), null, LocalDate.of(2019, 12, 4), null);

        assertFalse(event.occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_dateEqualToFromDate_returnsTrue() {
        Event event = new Event("meeting", LocalDate.of(2019, 12, 2), null, LocalDate.of(2019, 12, 4), null);

        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_dateBetweenFromAndTo_returnsTrue() {
        Event event = new Event("meeting", LocalDate.of(2019, 12, 2), null, LocalDate.of(2019, 12, 4), null);

        assertTrue(event.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_dateEqualToToDate_returnsTrue() {
        Event event = new Event("meeting", LocalDate.of(2019, 12, 2), null, LocalDate.of(2019, 12, 4), null);

        assertTrue(event.occursOn(LocalDate.of(2019, 12, 4)));
    }

    @Test
    public void occursOn_dateAfterToDate_returnsFalse() {
        Event event = new Event("meeting", LocalDate.of(2019, 12, 2), null, LocalDate.of(2019, 12, 4), null);

        assertFalse(event.occursOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    public void toString_event_showsFromAndToLabels() {
        Event event = new Event("project meeting",
                LocalDate.of(2019, 12, 2), LocalTime.of(14, 0),
                LocalDate.of(2019, 12, 2), LocalTime.of(16, 0));

        assertEquals("[E][ ] project meeting (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)", event.toString());
    }

    @Test
    public void toSaveFormat_event_encodesFromAndToDates() {
        Event event = new Event("project meeting",
                LocalDate.of(2019, 12, 2), LocalTime.of(14, 0),
                LocalDate.of(2019, 12, 2), LocalTime.of(16, 0));

        assertEquals("E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600", event.toSaveFormat());
    }
}
