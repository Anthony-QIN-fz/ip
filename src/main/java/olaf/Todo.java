package olaf;

import java.util.List;

/**
 * Represents a task without a date.
 */
final class Todo extends Task {
    Todo(String description) {
        super(description);
    }

    /** {@inheritDoc} */
    @Override
    String getTypeCode() {
        return "T";
    }

    /** {@inheritDoc} */
    @Override
    List<String> getAdditionalStorageFields() {
        return List.of();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
