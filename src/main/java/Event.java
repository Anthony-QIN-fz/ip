/**
 * Represents a task that takes place between a given start and end.
 */
final class Event extends Task {
    private final String from;
    private final String to;

    Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
