package olaf;

import java.nio.file.Path;

/**
 * Coordinates the components of the Olaf chatbot application.
 */
public class Olaf {
    private static final Path DATA_FILE_PATH = Path.of("data", "olaf.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;
    private final Ui ui;

    private Olaf(Storage storage, TaskList tasks, Parser parser, Ui ui) {
        this.storage = storage;
        this.tasks = tasks;
        this.parser = parser;
        this.ui = ui;
    }

    /**
     * Starts Olaf's command loop.
     *
     * @param args command-line arguments, which Olaf does not use.
     */
    public static void main(String[] args) {
        Storage storage = new Storage(DATA_FILE_PATH);
        try (Ui ui = new Ui()) {
            try {
                TaskList tasks = storage.load();
                Parser parser = new Parser();
                new Olaf(storage, tasks, parser, ui).run();
            } catch (StorageException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    private void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            try {
                ParsedCommand command = parser.parse(ui.readCommand());
                if (shouldExitAfterExecuting(command)) {
                    return;
                }
            } catch (CommandParseException | InvalidTaskNumberException exception) {
                ui.showError(exception.getMessage());
            } catch (StorageException exception) {
                ui.showError(exception.getMessage());
                return;
            }
        }
    }

    /**
     * Executes a validated command.
     *
     * @param command command to execute.
     * @return true if Olaf should exit after executing the command
     * @throws InvalidTaskNumberException if the command refers to a nonexistent task
     * @throws StorageException if a changed task list cannot be saved
     */
    private boolean shouldExitAfterExecuting(ParsedCommand command)
            throws InvalidTaskNumberException, StorageException {
        switch (command.getAction()) {
            case EXIT:
                ui.showFarewell();
                return true;
            case LIST:
                ui.showTaskList(tasks);
                break;
            case FIND:
                ui.showMatchingTasks(tasks.find(command.getKeyword()));
                break;
            case ADD:
                addTask(command.getTask());
                break;
            case MARK:
                markTask(command.getTaskNumber());
                break;
            case UNMARK:
                unmarkTask(command.getTaskNumber());
                break;
            case DELETE:
                deleteTask(command.getTaskNumber());
                break;
            default:
                throw new IllegalStateException("Unsupported command action: " + command.getAction());
        }
        return false;
    }

    private void addTask(Task task) throws StorageException {
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private void markTask(int taskNumber) throws InvalidTaskNumberException, StorageException {
        Task markedTask = tasks.markAsDone(taskNumber);
        storage.save(tasks);
        ui.showTaskMarkedAsDone(markedTask);
    }

    private void unmarkTask(int taskNumber) throws InvalidTaskNumberException, StorageException {
        Task unmarkedTask = tasks.markAsNotDone(taskNumber);
        storage.save(tasks);
        ui.showTaskMarkedAsNotDone(unmarkedTask);
    }

    private void deleteTask(int taskNumber) throws InvalidTaskNumberException, StorageException {
        Task deletedTask = tasks.delete(taskNumber);
        storage.save(tasks);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
