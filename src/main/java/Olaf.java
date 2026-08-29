import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the Olaf chatbot application.
 */
public class Olaf {
    private static final String BANNER = "  ___  _        __\n"
            + " / _ \\| | __ _ / _|\n"
            + "| | | | |/ _` | |_\n"
            + "| |_| | | (_| |  _|\n"
            + " \\___/|_|\\__,_|_|";
    private static final String DIVIDER = "_".repeat(60);
    private static final String GREETING = "Hello! I'm Olaf. What can I do for you?";
    private static final String FAREWELL = "Bye. Hope to see you again soon!";
    private static final Path DATA_FILE_PATH = Path.of("data", "olaf.txt");
    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";
    private static final String INVALID_MARK_COMMAND_MESSAGE =
            "Use 'mark <task number>' to mark a task as done.";
    private static final String INVALID_UNMARK_COMMAND_MESSAGE =
            "Use 'unmark <task number>' to mark a task as not done.";
    private static final String INVALID_DELETE_COMMAND_MESSAGE =
            "Use 'delete <task number>' to delete a task.";
    private static final String INVALID_TODO_COMMAND_MESSAGE =
            "Use 'todo <description>' to add a ToDo.";
    private static final String INVALID_DEADLINE_COMMAND_MESSAGE =
            "Use 'deadline <description> /by <date or time>' to add a deadline.";
    private static final String INVALID_EVENT_COMMAND_MESSAGE =
            "Use 'event <description> /from <start> /to <end>' to add an event.";
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Unknown command. Use todo, deadline, event, list, mark, unmark, delete, or bye.";

    private final Storage storage;
    private final TaskList tasks;

    private Olaf(Storage storage, TaskList tasks) {
        this.storage = storage;
        this.tasks = tasks;
    }

    /**
     * Starts Olaf's command loop.
     *
     * @param args command-line arguments, which Olaf does not use
     */
    public static void main(String[] args) {
        Storage storage = new Storage(DATA_FILE_PATH);
        try {
            TaskList tasks = storage.load();
            new Olaf(storage, tasks).run();
        } catch (StorageException exception) {
            printStartupError(exception.getMessage());
        }
    }

    private void run() {
        printWelcome();
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                if (isExitCommand(command)) {
                    printFarewell();
                    return;
                }
                try {
                    handleCommand(command);
                } catch (StorageException exception) {
                    printError(exception.getMessage());
                    return;
                }
            }
        }
    }

    private void handleCommand(String command) throws StorageException {
        if (isListCommand(command)) {
            printTaskList();
            return;
        }
        if (isMarkCommand(command)) {
            handleMarkCommand(command);
            return;
        }
        if (isUnmarkCommand(command)) {
            handleUnmarkCommand(command);
            return;
        }
        if (isDeleteCommand(command)) {
            handleDeleteCommand(command);
            return;
        }
        if (startsWithCommandWord(command, TODO_COMMAND)) {
            handleTodoCommand(command);
            return;
        }
        if (startsWithCommandWord(command, DEADLINE_COMMAND)) {
            handleDeadlineCommand(command);
            return;
        }
        if (startsWithCommandWord(command, EVENT_COMMAND)) {
            handleEventCommand(command);
            return;
        }

        printError(UNKNOWN_COMMAND_MESSAGE);
    }

    private void handleTodoCommand(String command) throws StorageException {
        String description = getCommandArguments(command);
        if (description.isEmpty()) {
            printError(INVALID_TODO_COMMAND_MESSAGE);
            return;
        }
        addTask(new Todo(description));
    }

    private void handleDeadlineCommand(String command) throws StorageException {
        String arguments = getCommandArguments(command);
        int byMarkerIndex = findMarker(arguments, BY_MARKER);
        if (byMarkerIndex < 0) {
            printError(INVALID_DEADLINE_COMMAND_MESSAGE);
            return;
        }

        String description = arguments.substring(0, byMarkerIndex).trim();
        String by = arguments.substring(byMarkerIndex + BY_MARKER.length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            printError(INVALID_DEADLINE_COMMAND_MESSAGE);
            return;
        }
        addTask(new Deadline(description, by));
    }

    private void handleEventCommand(String command) throws StorageException {
        String arguments = getCommandArguments(command);
        int fromMarkerIndex = findMarker(arguments, FROM_MARKER);
        int toMarkerIndex = findMarker(arguments, TO_MARKER);
        if (fromMarkerIndex < 0 || toMarkerIndex < fromMarkerIndex + FROM_MARKER.length()) {
            printError(INVALID_EVENT_COMMAND_MESSAGE);
            return;
        }

        String description = arguments.substring(0, fromMarkerIndex).trim();
        String from = arguments.substring(fromMarkerIndex + FROM_MARKER.length(), toMarkerIndex).trim();
        String to = arguments.substring(toMarkerIndex + TO_MARKER.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            printError(INVALID_EVENT_COMMAND_MESSAGE);
            return;
        }
        addTask(new Event(description, from, to));
    }

    private void addTask(Task task) throws StorageException {
        tasks.add(task);
        storage.save(tasks);
        String taskWord = tasks.size() == 1 ? "task" : "tasks";
        printMessage(" Got it. I've added this task:\n   " + task
                + "\n Now you have " + tasks.size() + " " + taskWord + " in the list.");
    }

    private String getCommandArguments(String command) {
        String[] commandParts = command.trim().split("\\s+", 2);
        return commandParts.length == 2 ? commandParts[1].trim() : "";
    }

    private int findMarker(String text, String marker) {
        Pattern markerPattern = Pattern.compile(
                "(?i)(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
        Matcher matcher = markerPattern.matcher(text);
        return matcher.find() ? matcher.start() : -1;
    }

    private void handleUnmarkCommand(String command) throws StorageException {
        String[] commandParts = command.trim().split("\\s+");
        if (commandParts.length != 2) {
            printError(INVALID_UNMARK_COMMAND_MESSAGE);
            return;
        }

        try {
            int taskNumber = Integer.parseInt(commandParts[1]);
            Task unmarkedTask = tasks.markAsNotDone(taskNumber);
            storage.save(tasks);
            printMessage(" OK, I've marked this task as not done yet:\n   " + unmarkedTask);
        } catch (NumberFormatException exception) {
            printError(INVALID_UNMARK_COMMAND_MESSAGE);
        } catch (InvalidTaskNumberException exception) {
            printError(exception.getMessage());
        }
    }

    private void handleMarkCommand(String command) throws StorageException {
        String[] commandParts = command.trim().split("\\s+");
        if (commandParts.length != 2) {
            printError(INVALID_MARK_COMMAND_MESSAGE);
            return;
        }

        try {
            int taskNumber = Integer.parseInt(commandParts[1]);
            Task markedTask = tasks.markAsDone(taskNumber);
            storage.save(tasks);
            printMessage(" Nice! I've marked this task as done:\n   " + markedTask);
        } catch (NumberFormatException exception) {
            printError(INVALID_MARK_COMMAND_MESSAGE);
        } catch (InvalidTaskNumberException exception) {
            printError(exception.getMessage());
        }
    }

    private void handleDeleteCommand(String command) throws StorageException {
        String[] commandParts = command.trim().split("\\s+");
        if (commandParts.length != 2) {
            printError(INVALID_DELETE_COMMAND_MESSAGE);
            return;
        }

        try {
            int taskNumber = Integer.parseInt(commandParts[1]);
            Task deletedTask = tasks.delete(taskNumber);
            storage.save(tasks);
            String taskWord = tasks.size() == 1 ? "task" : "tasks";
            printMessage(" Noted. I've removed this task:\n   " + deletedTask
                    + "\n Now you have " + tasks.size() + " " + taskWord + " in the list.");
        } catch (NumberFormatException exception) {
            printError(INVALID_DELETE_COMMAND_MESSAGE);
        } catch (InvalidTaskNumberException exception) {
            printError(exception.getMessage());
        }
    }

    private void printWelcome() {
        System.out.println(BANNER);
        System.out.println();
        System.out.println(GREETING);
        System.out.println(DIVIDER);
    }

    private boolean isExitCommand(String command) {
        return command.trim().equalsIgnoreCase(EXIT_COMMAND);
    }

    private boolean isListCommand(String command) {
        return command.trim().equalsIgnoreCase(LIST_COMMAND);
    }

    private boolean isMarkCommand(String command) {
        return startsWithCommandWord(command, MARK_COMMAND);
    }

    private boolean isUnmarkCommand(String command) {
        return startsWithCommandWord(command, UNMARK_COMMAND);
    }

    private boolean isDeleteCommand(String command) {
        return startsWithCommandWord(command, DELETE_COMMAND);
    }

    private boolean startsWithCommandWord(String command, String commandWord) {
        String[] commandParts = command.trim().split("\\s+", 2);
        return commandParts[0].equalsIgnoreCase(commandWord);
    }

    private void printTaskList() {
        System.out.println(DIVIDER);
        System.out.println(" Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println(" " + (index + 1) + "." + tasks.get(index));
        }
        System.out.println(DIVIDER);
    }

    private void printFarewell() {
        printMessage(FAREWELL);
    }

    private void printMessage(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    private void printError(String message) {
        printMessage("error: " + message);
    }

    private static void printStartupError(String message) {
        System.out.println(DIVIDER);
        System.out.println("error: " + message);
        System.out.println(DIVIDER);
    }
}
