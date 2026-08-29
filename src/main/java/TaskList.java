import java.util.ArrayList;
import java.util.List;

/**
 * Stores Olaf's tasks in insertion order.
 */
final class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    TaskList() {
    }

    /**
     * Creates a task list containing a defensive copy of the supplied tasks.
     *
     * @param initialTasks tasks loaded from storage
     */
    TaskList(List<Task> initialTasks) {
        tasks.addAll(initialTasks);
    }

    /**
     * Adds the supplied task to the end of the list.
     *
     * @param task task to add
     */
    void add(Task task) {
        tasks.add(task);
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

    /**
     * Deletes the task identified by its user-facing number.
     *
     * @param taskNumber one-based task number shown by the list command
     * @return the deleted task
     * @throws InvalidTaskNumberException if no task has the supplied number
     */
    Task delete(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    int size() {
        return tasks.size();
    }

    Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns an immutable snapshot for persistence.
     *
     * @return snapshot of all tasks in insertion order
     */
    List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    private Task getByTaskNumber(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    private void validateTaskNumber(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new InvalidTaskNumberException(tasks.size());
        }
    }
}
