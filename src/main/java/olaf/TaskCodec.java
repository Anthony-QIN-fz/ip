package olaf;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts tasks to and from Olaf's escaped, pipe-delimited storage format.
 */
final class TaskCodec {
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String STATUS_DONE = "1";
    private static final String STATUS_NOT_DONE = "0";
    private static final String FIELD_SEPARATOR = " | ";

    /**
     * Encodes one task as a storage record.
     *
     * @param task task to encode.
     * @return escaped storage record
     */
    String encode(Task task) {
        List<String> fields = new ArrayList<>();
        fields.add(task.getTypeCode());
        fields.add(task.isDone() ? STATUS_DONE : STATUS_NOT_DONE);
        fields.add(task.getDescription());
        fields.addAll(task.getAdditionalStorageFields());

        List<String> escapedFields = fields.stream()
                .map(this::escape)
                .toList();
        return String.join(FIELD_SEPARATOR, escapedFields);
    }

    /**
     * Decodes one storage record into a task.
     *
     * @param record stored task record.
     * @param lineNumber one-based source line number used in error messages.
     * @return decoded task
     * @throws StorageException if the record is malformed
     */
    Task decode(String record, int lineNumber) throws StorageException {
        List<String> fields = splitFields(record, lineNumber);
        if (fields.size() < 2) {
            throw createInvalidRecordException(lineNumber, "missing task type or status");
        }

        String type = fields.get(0);
        String status = fields.get(1);
        validateStatus(status, lineNumber);

        Task task = switch (type) {
            case TYPE_TODO -> decodeTodo(fields, lineNumber);
            case TYPE_DEADLINE -> decodeDeadline(fields, lineNumber);
            case TYPE_EVENT -> decodeEvent(fields, lineNumber);
            default -> throw createInvalidRecordException(lineNumber, "unknown task type '" + type + "'");
        };

        if (STATUS_DONE.equals(status)) {
            task.markAsDone();
        }
        return task;
    }

    private Task decodeTodo(List<String> fields, int lineNumber) throws StorageException {
        validateFields(fields, 3, lineNumber);
        return new Todo(requireText(fields.get(2), "description", lineNumber));
    }

    private Task decodeDeadline(List<String> fields, int lineNumber) throws StorageException {
        validateFields(fields, 4, lineNumber);
        String description = requireText(fields.get(2), "description", lineNumber);
        LocalDate by = parseDate(requireText(fields.get(3), "deadline", lineNumber),
                "deadline", lineNumber);
        return new Deadline(description, by);
    }

    private Task decodeEvent(List<String> fields, int lineNumber) throws StorageException {
        validateFields(fields, 5, lineNumber);
        String description = requireText(fields.get(2), "description", lineNumber);
        LocalDate from = parseDate(requireText(fields.get(3), "event start", lineNumber),
                "event start", lineNumber);
        LocalDate to = parseDate(requireText(fields.get(4), "event end", lineNumber),
                "event end", lineNumber);
        return new Event(description, from, to);
    }

    private LocalDate parseDate(String value, String fieldName, int lineNumber)
            throws StorageException {
        try {
            return TaskDateFormat.parse(value);
        } catch (DateTimeParseException exception) {
            throw createInvalidRecordException(lineNumber, fieldName + " must use yyyy-MM-dd");
        }
    }

    private List<String> splitFields(String record, int lineNumber) throws StorageException {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaping = false;

        for (int index = 0; index < record.length(); index++) {
            char character = record.charAt(index);
            if (isEscaping) {
                if (character != '\\' && character != '|') {
                    throw createInvalidRecordException(lineNumber, "invalid escape sequence");
                }
                currentField.append(character);
                isEscaping = false;
            } else if (character == '\\') {
                isEscaping = true;
            } else if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }

        if (isEscaping) {
            throw createInvalidRecordException(lineNumber, "unfinished escape sequence");
        }
        fields.add(currentField.toString().trim());
        return fields;
    }

    private String escape(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    private void validateStatus(String status, int lineNumber) throws StorageException {
        if (!STATUS_DONE.equals(status) && !STATUS_NOT_DONE.equals(status)) {
            throw createInvalidRecordException(lineNumber, "status must be 0 or 1");
        }
    }

    private void validateFields(List<String> fields, int expectedCount, int lineNumber)
            throws StorageException {
        if (fields.size() != expectedCount) {
            throw createInvalidRecordException(lineNumber, "incorrect number of fields");
        }
    }

    private String requireText(String value, String fieldName, int lineNumber) throws StorageException {
        if (value.isBlank()) {
            throw createInvalidRecordException(lineNumber, fieldName + " cannot be empty");
        }
        return value;
    }

    private StorageException createInvalidRecordException(int lineNumber, String reason) {
        return new StorageException("Invalid task data on line " + lineNumber + ": " + reason + ".");
    }
}
