package maggigorengayam.command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;
import maggigorengayam.util.DateTimeUtil;

/** The {@code on <date>} command: shows deadlines/events occurring on a given date. */
public class OnCommand extends Command {
    private final LocalDate date;

    /** Shows deadlines/events occurring on the given date once {@link #execute} runs. */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /** Filters {@code tasks} down to those occurring on {@code date} and shows them. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks.getAll()) {
            if (task.occursOn(date)) {
                matches.add(task);
            }
        }
        ui.showTasksOn(DateTimeUtil.formatDateOnlyForDisplay(date), matches);
    }
}
