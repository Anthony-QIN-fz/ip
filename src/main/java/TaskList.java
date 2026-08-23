/**
 * Stores Olaf's tasks in insertion order for the current application session.
 */
final class TaskList {
    private static final int MAX_TASKS = 100;

    private final Task[] tasks = new Task[MAX_TASKS];
    private int size;

    /**
     * Adds a task with the supplied description.
     *
     * @param description text entered by the user
     * @throws TaskListFullException if the task list has reached its capacity
     */
    void add(String description) throws TaskListFullException {
        if (size == MAX_TASKS) {
            throw new TaskListFullException();
        }
        tasks[size] = new Todo(description);
        size++;
    }

    int size() {
        return size;
    }

    String getDescription(int index) {
        return tasks[index].getDescription();
    }
}
