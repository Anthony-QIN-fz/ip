/**
 * Stores Olaf's tasks in insertion order for the current application session.
 */
final class TaskList {
    private static final int MAX_TASKS = 100;

    private final Task[] tasks = new Task[MAX_TASKS];
    private int size;

    /**
     * Adds the supplied task to the end of the list.
     *
     * @param task task to add
     * @throws TaskListFullException if the task list has reached its capacity
     */
    void add(Task task) throws TaskListFullException {
        if (size == MAX_TASKS) {
            throw new TaskListFullException();
        }
        tasks[size] = task;
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
        Task task = getByTaskNumber(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task identified by its user-facing number as not done.
     *
     * @param taskNumber one-based task number shown by the list command
     * @return the task that was marked as not done
     * @throws InvalidTaskNumberException if no task has the supplied number
     */
    Task markAsNotDone(int taskNumber) throws InvalidTaskNumberException {
        Task task = getByTaskNumber(taskNumber);
        task.markAsNotDone();
        return task;
    }

    int size() {
        return size;
    }

    Task get(int index) {
        return tasks[index];
    }

    private Task getByTaskNumber(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > size) {
            throw new InvalidTaskNumberException(size);
        }
        return tasks[taskNumber - 1];
    }
}
