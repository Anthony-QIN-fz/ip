package olaf;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Handles all command-line interaction between Olaf and the user.
 */
final class Ui implements AutoCloseable {
    private static final String BANNER = "  ___  _        __\n"
            + " / _ \\| | __ _ / _|\n"
            + "| | | | |/ _` | |_\n"
            + "| |_| | | (_| |  _|\n"
            + " \\___/|_|\\__,_|_|";
    private static final String DIVIDER = "_".repeat(60);
    private static final String GREETING = "Hello! I'm Olaf. What can I do for you?";
    private static final String FAREWELL = "Bye. Hope to see you again soon!";

    private final Scanner scanner;
    private final PrintStream output;

    Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied streams, allowing console behavior to be tested independently.
     *
     * @param input stream from which commands are read.
     * @param output stream to which responses are written.
     */
    Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    String readCommand() {
        return scanner.nextLine();
    }

    void showWelcome() {
        output.println(BANNER);
        output.println();
        output.println(GREETING);
        output.println(DIVIDER);
    }

    void showFarewell() {
        showMessage(FAREWELL);
    }

    void showTaskList(TaskList tasks) {
        output.println(DIVIDER);
        output.println(" Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            output.println(" " + (index + 1) + "." + tasks.get(index));
        }
        output.println(DIVIDER);
    }

    void showTaskAdded(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        showMessage(" Got it. I've added this task:\n   " + task
                + "\n Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    void showTaskMarkedAsDone(Task task) {
        showMessage(" Nice! I've marked this task as done:\n   " + task);
    }

    void showTaskMarkedAsNotDone(Task task) {
        showMessage(" OK, I've marked this task as not done yet:\n   " + task);
    }

    void showTaskDeleted(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        showMessage(" Noted. I've removed this task:\n   " + task
                + "\n Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    void showError(String message) {
        showMessage("error: " + message);
    }

    @Override
    public void close() {
        scanner.close();
    }

    private void showMessage(String message) {
        output.println(DIVIDER);
        output.println(message);
        output.println(DIVIDER);
    }
}
