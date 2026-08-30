package olaf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Olaf's tasks in a UTF-8 text file.
 */
final class Storage {
    private final Path filePath;
    private final TaskCodec taskCodec = new TaskCodec();

    Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks, treating a missing data file as an empty task list.
     *
     * @return loaded tasks in their stored order
     * @throws StorageException if an existing file cannot be read or parsed
     */
    TaskList load() throws StorageException {
        if (Files.notExists(filePath)) {
            return new TaskList();
        }

        try {
            List<String> records = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (int index = 0; index < records.size(); index++) {
                tasks.add(taskCodec.decode(records.get(index), index + 1));
            }
            return new TaskList(tasks);
        } catch (IOException exception) {
            throw new StorageException("Unable to read task data from " + filePath + ".", exception);
        }
    }

    /**
     * Saves a complete snapshot of the task list, creating its parent directory when needed.
     *
     * @param tasks task list to save
     * @throws StorageException if the directory or data file cannot be written
     */
    void save(TaskList tasks) throws StorageException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            List<String> records = tasks.getTasks().stream()
                    .map(taskCodec::encode)
                    .toList();
            Files.write(filePath, records, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new StorageException("Unable to save task data to " + filePath + ".", exception);
        }
    }
}
