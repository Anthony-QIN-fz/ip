package olaf;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a task that takes place between a given start and end.
 */
final class Event extends Task {
    /** Identifies an event task in storage and displayed responses. */
    static final String TYPE_CODE = "E";

    private final LocalDate startDate;
    private final LocalDate endDate;

    Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** {@inheritDoc} */
    @Override
    String getTypeCode() {
        return TYPE_CODE;
    }

    /** {@inheritDoc} */
    @Override
    List<String> getAdditionalStorageFields() {
        return List.of(startDate.toString(), endDate.toString());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (from: " + TaskDateFormat.format(startDate)
                + " to: " + TaskDateFormat.format(endDate) + ")";
    }
}
