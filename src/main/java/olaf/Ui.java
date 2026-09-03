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
        output.println(ResponseFormatter.formatWelcome());
        output.println(DIVIDER);
    }

    /**
     * Displays Olaf's farewell message.
     */
    void showFarewell() {
        showResponse(ResponseFormatter.formatFarewell());
    }

    /**
     * Displays every task in the supplied list with its user-facing number.
     *
     * @param tasks task list to display
     */
    void showTaskList(TaskList tasks) {
        showResponse(ResponseFormatter.formatTaskList(tasks));
    }

    /**
     * Displays tasks that match a find command, numbered within the filtered results.
     *
     * @param matchingTasks matching tasks in display order
     */
    void showMatchingTasks(List<Task> matchingTasks) {
        showResponse(ResponseFormatter.formatMatchingTasks(matchingTasks));
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task added task
     * @param taskCount number of tasks after the addition
     */
    void showTaskAdded(Task task, int taskCount) {
        showResponse(ResponseFormatter.formatTaskAdded(task, taskCount));
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task task that was marked
     */
    void showTaskMarkedAsDone(Task task) {
        showResponse(ResponseFormatter.formatTaskMarkedAsDone(task));
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked
     */
    void showTaskMarkedAsNotDone(Task task) {
        showResponse(ResponseFormatter.formatTaskMarkedAsNotDone(task));
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task deleted task
     * @param taskCount number of tasks after the deletion
     */
    void showTaskDeleted(Task task, int taskCount) {
        showResponse(ResponseFormatter.formatTaskDeleted(task, taskCount));
    }

    /**
     * Displays an error message to the user.
     *
     * @param message error details to display
     */
    void showError(String message) {
        showResponse(ResponseFormatter.formatError(message));
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        scanner.close();
    }

    /**
     * Displays one complete Olaf response between console dividers.
     *
     * @param message response text to display
     */
    void showResponse(String message) {
        output.println(DIVIDER);
        output.println(message);
        output.println(DIVIDER);
    }
}
