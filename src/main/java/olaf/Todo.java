package olaf;

import java.util.List;

/**
 * Represents a task without a date.
 */
final class Todo extends Task {
    Todo(String description) {
        super(description);
    }

    @Override
    String getTypeCode() {
        return "T";
    }

    @Override
    List<String> getAdditionalStorageFields() {
        return List.of();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
