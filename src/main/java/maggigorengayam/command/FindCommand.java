package maggigorengayam.command;

import java.util.ArrayList;
import java.util.List;

import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The {@code find <keyword>} command: shows tasks whose description contains the given keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Shows tasks whose description contains the given keyword once {@link #execute} runs. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Filters {@code tasks} down to those whose description contains
     * {@code keyword} (case-insensitive) and returns them as a message.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks.getAll()) {
            if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matches.add(task);
            }
        }
        return ui.showMatchingTasks(matches);
    }
}
