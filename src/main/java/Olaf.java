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

        try {
            tasks.add(command);
            printMessage("added: " + command);
        } catch (TaskListFullException exception) {
            printMessage("error: " + exception.getMessage());
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

    private void printTaskList() {
        System.out.println(DIVIDER);
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + ". " + tasks.getDescription(index));
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
}
