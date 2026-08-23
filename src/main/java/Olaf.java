import java.util.Scanner;

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
    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String INVALID_MARK_COMMAND_MESSAGE =
            "Use 'mark <task number>' to mark a task as done.";
    private static final String INVALID_UNMARK_COMMAND_MESSAGE =
            "Use 'unmark <task number>' to mark a task as not done.";

    private final TaskList tasks = new TaskList();

    /**
     * Starts Olaf's command loop.
     *
     * @param args command-line arguments, which Olaf does not use
     */
    public static void main(String[] args) {
        new Olaf().run();
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
                handleCommand(command);
            }
        }
    }

    private void handleCommand(String command) {
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

        try {
            tasks.add(command);
            printMessage("added: " + command);
        } catch (TaskListFullException exception) {
            printError(exception.getMessage());
        }
    }

    private void handleUnmarkCommand(String command) {
        String[] commandParts = command.trim().split("\\s+");
        if (commandParts.length != 2) {
            printError(INVALID_UNMARK_COMMAND_MESSAGE);
            return;
        }

        try {
            int taskNumber = Integer.parseInt(commandParts[1]);
            Task unmarkedTask = tasks.markAsNotDone(taskNumber);
            printMessage(" OK, I've marked this task as not done yet:\n   " + unmarkedTask);
        } catch (NumberFormatException exception) {
            printError(INVALID_UNMARK_COMMAND_MESSAGE);
        } catch (InvalidTaskNumberException exception) {
            printError(exception.getMessage());
        }
    }

    private void handleMarkCommand(String command) {
        String[] commandParts = command.trim().split("\\s+");
        if (commandParts.length != 2) {
            printError(INVALID_MARK_COMMAND_MESSAGE);
            return;
        }

        try {
            int taskNumber = Integer.parseInt(commandParts[1]);
            Task markedTask = tasks.markAsDone(taskNumber);
            printMessage(" Nice! I've marked this task as done:\n   " + markedTask);
        } catch (NumberFormatException exception) {
            printError(INVALID_MARK_COMMAND_MESSAGE);
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
}
