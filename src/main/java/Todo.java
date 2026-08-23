/**
 * Represents a task without a date.
 */
final class Todo extends Task {
    Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
