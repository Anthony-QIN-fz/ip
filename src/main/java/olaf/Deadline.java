package olaf;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a task that must be completed by a given date.
 */
final class Deadline extends Task {
    /** Identifies a deadline task in storage and displayed responses. */
    static final String TYPE_CODE = "D";

    private final LocalDate dueDate;

    Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /** {@inheritDoc} */
    @Override
    String getTypeCode() {
        return TYPE_CODE;
    }

    /** {@inheritDoc} */
    @Override
    List<String> getAdditionalStorageFields() {
        return List.of(dueDate.toString());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (by: " + TaskDateFormat.format(dueDate) + ")";
    }
}
