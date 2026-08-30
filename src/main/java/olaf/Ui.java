package olaf;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
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

    /**
     * Returns whether another command is available from the input stream.
     *
     * @return true if another command can be read
     */
    boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from the input stream.
     *
     * @return next command entered by the user
     */
    String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Olaf's banner and greeting.
     */
    void showWelcome() {
        output.println(BANNER);
        output.println();
        output.println(GREETING);
        output.println(DIVIDER);
    }

    /**
     * Displays Olaf's farewell message.
     */
    void showFarewell() {
        showMessage(FAREWELL);
    }

    /**
     * Displays every task in the supplied list with its user-facing number.
     *
     * @param tasks task list to display
     */
    void showTaskList(TaskList tasks) {
        showTasks(" Here are the tasks in your list:", tasks.getTasks());
    }

    /**
     * Displays tasks that match a find command, numbered within the filtered results.
     *
     * @param matchingTasks matching tasks in display order
     */
    void showMatchingTasks(List<Task> matchingTasks) {
        showTasks(" Here are the matching tasks in your list:", matchingTasks);
    }

    private void showTasks(String heading, List<Task> tasks) {
        output.println(DIVIDER);
        output.println(heading);
        for (int index = 0; index < tasks.size(); index++) {
            output.println(" " + (index + 1) + "." + tasks.get(index));
        }
        output.println(DIVIDER);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task added task
     * @param taskCount number of tasks after the addition
     */
    void showTaskAdded(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        showMessage(" Got it. I've added this task:\n   " + task
                + "\n Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task task that was marked
     */
    void showTaskMarkedAsDone(Task task) {
        showMessage(" Nice! I've marked this task as done:\n   " + task);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked
     */
    void showTaskMarkedAsNotDone(Task task) {
        showMessage(" OK, I've marked this task as not done yet:\n   " + task);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task deleted task
     * @param taskCount number of tasks after the deletion
     */
    void showTaskDeleted(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        showMessage(" Noted. I've removed this task:\n   " + task
                + "\n Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /**
     * Displays an error message to the user.
     *
     * @param message error details to display
     */
    void showError(String message) {
        showMessage("error: " + message);
    }

    /** {@inheritDoc} */
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
