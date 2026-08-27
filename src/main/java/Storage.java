import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from, and saves tasks to, a plain-text file on disk so the
 * task list survives between runs of the program.
 */
public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file. A missing file (e.g. the very first
     * run) is not an error: it just means there is nothing to load yet, so
     * an empty list is returned. A line that cannot be parsed (e.g. the
     * file was hand-edited into an invalid state) is skipped rather than
     * failing the whole load, so a single bad line doesn't lose every
     * other saved task.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(file.toPath())) {
                Task task = parseLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return tasks;
    }

    private Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        Task task;
        switch (type) {
        case "T":
            if (parts.length != 3) {
                return null;
            }
            task = new ToDo(description);
            break;
        case "D":
            if (parts.length != 4) {
                return null;
            }
            task = new Deadline(description, parts[3]);
            break;
        case "E":
            if (parts.length != 5) {
                return null;
            }
            task = new Event(description, parts[3], parts[4]);
            break;
        default:
            return null;
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Saves the given tasks to the data file, overwriting whatever was
     * there before. Creates the parent directory (e.g. "data/") first if
     * it does not exist yet.
     */
    public void save(List<Task> tasks) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : tasks) {
                writer.write(task.toSaveFormat() + System.lineSeparator());
            }
        }
    }
}
