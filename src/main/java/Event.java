import java.time.LocalDate;
import java.util.List;

/**
 * Represents a task that takes place between a given start and end.
 */
final class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    String getTypeCode() {
        return "E";
    }

    @Override
    List<String> getAdditionalStorageFields() {
        return List.of(startDate.toString(), endDate.toString());
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + TaskDateFormat.format(startDate)
                + " to: " + TaskDateFormat.format(endDate) + ")";
    }
}
