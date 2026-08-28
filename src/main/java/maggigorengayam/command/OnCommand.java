package maggigorengayam.command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;
import maggigorengayam.util.DateTimeUtil;

/** The `on <date>` command: shows deadlines/events occurring on a given date. */
public class OnCommand extends Command {
    private final LocalDate date;

    public OnCommand(LocalDate date) {
        this.date = date;
    }

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
