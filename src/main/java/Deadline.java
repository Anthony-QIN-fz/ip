import java.time.LocalDate;
import java.util.List;

/**
 * Represents a task that must be completed by a given date.
 */
final class Deadline extends Task {
    private final LocalDate dueDate;

    Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    @Override
    String getTypeCode() {
        return "D";
    }

    @Override
    List<String> getAdditionalStorageFields() {
        return List.of(dueDate.toString());
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + TaskDateFormat.format(dueDate) + ")";
    }
}
