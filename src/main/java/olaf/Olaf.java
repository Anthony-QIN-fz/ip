package olaf;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Coordinates command parsing, task operations, persistence, and response generation.
 */
public class Olaf {
    private static final Path DATA_FILE_PATH = Path.of("data", "olaf.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;

    Olaf(Path dataFilePath) throws StorageException {
        storage = new Storage(dataFilePath);
        tasks = storage.load();
        parser = new Parser();
    }

    /**
     * Starts Olaf's command-line interface.
     *
     * @param args command-line arguments, which Olaf does not use
     */
    public static void main(String[] args) {
        try (Ui ui = new Ui()) {
            try {
                Olaf olaf = createDefault();
                olaf.runCommandLine(ui);
            } catch (StorageException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    static Olaf createDefault() throws StorageException {
        return new Olaf(DATA_FILE_PATH);
    }

    /**
     * Returns the greeting shown when a user starts a conversation with Olaf.
     *
     * @return Olaf's welcome message
     */
    public String getWelcomeMessage() {
        return ResponseFormatter.formatWelcome();
    }

    /**
     * Parses and executes one command, returning text and session status for the active interface.
     * Expected command and task-number errors are returned as recoverable responses. A storage
     * failure is fatal because the in-memory task list may no longer match the persisted data.
     *
     * @param input command entered by the user
     * @return response text and the resulting session status
     * @throws NullPointerException if input is null
     */
    public CommandResult executeCommand(String input) {
        Objects.requireNonNull(input, "input");

        try {
            ParsedCommand command = parser.parse(input);
            return execute(command);
        } catch (CommandParseException | InvalidTaskNumberException exception) {
            return new CommandResult(ResponseFormatter.formatError(exception.getMessage()),
                    CommandStatus.CONTINUE);
        } catch (StorageException exception) {
            return new CommandResult(ResponseFormatter.formatError(exception.getMessage()),
                    CommandStatus.FATAL_ERROR);
        }
    }

    private void runCommandLine(Ui ui) {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            CommandResult result = executeCommand(ui.readCommand());
            ui.showResponse(result.message());
            if (result.status() != CommandStatus.CONTINUE) {
                return;
            }
        }
    }

    private CommandResult execute(ParsedCommand command)
            throws InvalidTaskNumberException, StorageException {
        return switch (command.getAction()) {
            case EXIT -> new CommandResult(ResponseFormatter.formatFarewell(),
                    CommandStatus.EXIT_REQUESTED);
            case LIST -> new CommandResult(ResponseFormatter.formatTaskList(tasks),
                    CommandStatus.CONTINUE);
            case FIND -> new CommandResult(
                    ResponseFormatter.formatMatchingTasks(tasks.find(command.getKeyword())),
                    CommandStatus.CONTINUE);
            case ADD -> addTask(command.getTask());
            case MARK -> markTask(command.getTaskNumber());
            case UNMARK -> unmarkTask(command.getTaskNumber());
            case DELETE -> deleteTask(command.getTaskNumber());
        };
    }

    private CommandResult addTask(Task task) throws StorageException {
        tasks.add(task);
        storage.save(tasks);
        return new CommandResult(ResponseFormatter.formatTaskAdded(task, tasks.size()),
                CommandStatus.CONTINUE);
    }

    private CommandResult markTask(int taskNumber)
            throws InvalidTaskNumberException, StorageException {
        Task markedTask = tasks.markAsDone(taskNumber);
        storage.save(tasks);
        return new CommandResult(ResponseFormatter.formatTaskMarkedAsDone(markedTask),
                CommandStatus.CONTINUE);
    }

    private CommandResult unmarkTask(int taskNumber)
            throws InvalidTaskNumberException, StorageException {
        Task unmarkedTask = tasks.markAsNotDone(taskNumber);
        storage.save(tasks);
        return new CommandResult(ResponseFormatter.formatTaskMarkedAsNotDone(unmarkedTask),
                CommandStatus.CONTINUE);
    }

    private CommandResult deleteTask(int taskNumber)
            throws InvalidTaskNumberException, StorageException {
        Task deletedTask = tasks.delete(taskNumber);
        storage.save(tasks);
        return new CommandResult(ResponseFormatter.formatTaskDeleted(deletedTask, tasks.size()),
                CommandStatus.CONTINUE);
    }

    /**
     * Describes whether an interface should continue after displaying a command response.
     */
    public enum CommandStatus {
        /** The command completed or failed recoverably, so more input may be accepted. */
        CONTINUE,
        /** The user requested a normal exit. */
        EXIT_REQUESTED,
        /** Persistence failed, so accepting further commands would be unsafe. */
        FATAL_ERROR
    }

    /**
     * Contains the text and session status produced by one command.
     *
     * @param message user-facing response text
     * @param status status that the active interface should apply after displaying the response
     */
    public record CommandResult(String message, CommandStatus status) {
        /**
         * Creates an immutable command result with non-null response data.
         *
         * @param message user-facing response text
         * @param status status that the active interface should apply
         */
        public CommandResult {
            Objects.requireNonNull(message, "message");
            Objects.requireNonNull(status, "status");
        }
    }
}
