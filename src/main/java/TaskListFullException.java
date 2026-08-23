/**
 * Signals that no more tasks can be added to the in-memory task list.
 */
final class TaskListFullException extends Exception {
    private static final long serialVersionUID = 1L;

    TaskListFullException() {
        super("Task list is full (maximum 100 tasks).");
    }
}
