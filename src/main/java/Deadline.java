/**
 * Represents a task that must be completed by a given date or time.
 */
final class Deadline extends Task {
    private final String by;

    Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
