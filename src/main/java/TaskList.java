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

    /**
     * Marks the task identified by its user-facing number as done.
     *
     * @param taskNumber one-based task number shown by the list command
     * @return the task that was marked
     * @throws InvalidTaskNumberException if no task has the supplied number
     */
    Task markAsDone(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > size) {
            throw new InvalidTaskNumberException(size);
        }

        Task task = tasks[taskNumber - 1];
        task.markAsDone();
        return task;
    }

    int size() {
        return size;
    }

    Task get(int index) {
        return tasks[index];
    }
}
