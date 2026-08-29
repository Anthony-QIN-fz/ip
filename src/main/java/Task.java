import java.util.List;

/**
 * Represents a task stored by Olaf.
 */
abstract class Task {
    private final String description;
    private boolean isDone;

    protected Task(String description) {
        this.description = description;
    }

    /**
     * Marks this task as completed.
     */
    void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    void markAsNotDone() {
        isDone = false;
    }

    String getDescription() {
        return description;
    }

    boolean isDone() {
        return isDone;
    }

    /**
     * Returns the one-character code used to identify this task type in storage.
     *
     * @return storage type code
     */
    abstract String getTypeCode();

    /**
     * Returns task-type-specific fields that follow the description in storage.
     *
     * @return immutable list of additional storage fields
     */
    abstract List<String> getAdditionalStorageFields();

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }
}
