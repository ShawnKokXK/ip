import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeParseException;
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
     * The result of a {@link #load()} call: the tasks that were
     * successfully parsed, plus a count of lines that were skipped because
     * they could not be parsed (e.g. the file was hand-edited into an
     * invalid state). Callers can use {@code skippedLineCount} to warn the
     * user that some previously-saved tasks were silently lost, instead of
     * that data loss going completely unnoticed.
     */
    public static class LoadResult {
        public final List<Task> tasks;
        public final int skippedLineCount;

        public LoadResult(List<Task> tasks, int skippedLineCount) {
            this.tasks = tasks;
            this.skippedLineCount = skippedLineCount;
        }
    }

    /**
     * Loads tasks from the data file. A missing file (e.g. the very first
     * run) is not an error: it just means there is nothing to load yet, so
     * an empty result is returned. A line that cannot be parsed is skipped
     * rather than failing the whole load, so a single bad line doesn't lose
     * every other saved task; the number of lines skipped is reported back
     * via {@link LoadResult#skippedLineCount} rather than being hidden.
     *
     * @throws IOException if the file exists but could not be read (e.g.
     *         it is actually a directory, or permissions deny access) -
     *         this is distinct from "no file yet", which is not an error.
     */
    public LoadResult load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        int skippedLineCount = 0;
        File file = new File(filePath);
        if (!file.exists()) {
            return new LoadResult(tasks, 0);
        }
        for (String line : Files.readAllLines(file.toPath())) {
            if (line.isBlank()) {
                continue;
            }
            Task task = parseLine(line);
            if (task != null) {
                tasks.add(task);
            } else {
                skippedLineCount++;
            }
        }
        return new LoadResult(tasks, skippedLineCount);
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
            try {
                DateTimeUtil.ParsedDateTime by = DateTimeUtil.parse(parts[3]);
                task = new Deadline(description, by.date, by.time);
            } catch (DateTimeParseException e) {
                return null;
            }
            break;
        case "E":
            if (parts.length != 5) {
                return null;
            }
            try {
                DateTimeUtil.ParsedDateTime from = DateTimeUtil.parse(parts[3]);
                DateTimeUtil.ParsedDateTime to = DateTimeUtil.parse(parts[4]);
                task = new Event(description, from.date, from.time, to.date, to.time);
            } catch (DateTimeParseException e) {
                return null;
            }
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
