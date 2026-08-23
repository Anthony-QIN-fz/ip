/**
 * Represents a task stored by Olaf.
 */
abstract class Task {
    private final String description;

    protected Task(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }
}
