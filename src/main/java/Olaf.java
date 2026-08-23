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
                echoCommand(command);
            }
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

    private void echoCommand(String command) {
        System.out.println(DIVIDER);
        System.out.println();
        System.out.println(command);
        System.out.println(DIVIDER);
    }

    private void printFarewell() {
        System.out.println(DIVIDER);
        System.out.println();
        System.out.println(FAREWELL);
        System.out.println(DIVIDER);
    }
}
