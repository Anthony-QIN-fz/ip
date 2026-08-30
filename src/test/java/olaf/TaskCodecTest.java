package olaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion between tasks and their storage records.
 */
class TaskCodecTest {
    private static final int DEFAULT_LINE_NUMBER = 1;

    private final TaskCodec taskCodec = new TaskCodec();

    /**
     * Verifies that an incomplete to-do task is encoded correctly.
     */
    @Test
    void encode_todoNotDone_correctRecordReturned() {
        Task task = new Todo("read book");

        assertEquals("T | 0 | read book", taskCodec.encode(task));
    }

    /**
     * Verifies that a completed deadline is encoded correctly.
     */
    @Test
    void encode_deadlineDone_correctRecordReturned() {
        Task task = new Deadline("return book", LocalDate.of(2019, 10, 15));
        task.markAsDone();

        assertEquals("D | 1 | return book | 2019-10-15", taskCodec.encode(task));
    }

    /**
     * Verifies that an incomplete event is encoded correctly.
     */
    @Test
    void encode_eventNotDone_correctRecordReturned() {
        Task task = new Event("project meeting", LocalDate.of(2019, 10, 20),
                LocalDate.of(2019, 10, 21));

        assertEquals("E | 0 | project meeting | 2019-10-20 | 2019-10-21",
                taskCodec.encode(task));
    }

    /**
     * Verifies that reserved characters in descriptions are escaped during encoding.
     */
    @Test
    void encode_descriptionContainsReservedCharacters_charactersEscaped() {
        Task task = new Todo("review | chapter \\ notes");

        assertEquals("T | 0 | review \\| chapter \\\\ notes", taskCodec.encode(task));
    }

    /**
     * Verifies that an incomplete to-do record is decoded correctly.
     */
    @Test
    void decode_todoNotDone_correctTaskReturned() throws StorageException {
        Task task = taskCodec.decode("T | 0 | read book", DEFAULT_LINE_NUMBER);

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(List.of(), task.getAdditionalStorageFields());
    }

    /**
     * Verifies that a completed deadline record is decoded correctly.
     */
    @Test
    void decode_deadlineDone_correctTaskReturned() throws StorageException {
        Task task = taskCodec.decode("D | 1 | return book | 2019-10-15",
                DEFAULT_LINE_NUMBER);

        assertInstanceOf(Deadline.class, task);
        assertEquals("return book", task.getDescription());
        assertTrue(task.isDone());
        assertEquals(List.of("2019-10-15"), task.getAdditionalStorageFields());
    }

    /**
     * Verifies that an incomplete event record is decoded correctly.
     */
    @Test
    void decode_eventNotDone_correctTaskReturned() throws StorageException {
        Task task = taskCodec.decode(
                "E | 0 | project meeting | 2019-10-20 | 2019-10-21",
                DEFAULT_LINE_NUMBER);

        assertInstanceOf(Event.class, task);
        assertEquals("project meeting", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(List.of("2019-10-20", "2019-10-21"),
                task.getAdditionalStorageFields());
    }

    /**
     * Verifies that escaped characters in descriptions are restored during decoding.
     */
    @Test
    void decode_descriptionContainsEscapedCharacters_charactersUnescaped()
            throws StorageException {
        Task task = taskCodec.decode("T | 0 | review \\| chapter \\\\ notes",
                DEFAULT_LINE_NUMBER);

        assertEquals("review | chapter \\ notes", task.getDescription());
    }

    /**
     * Verifies that field whitespace is trimmed during decoding.
     */
    @Test
    void decode_separatorWhitespaceVaries_fieldsTrimmed() throws StorageException {
        Task task = taskCodec.decode("  T| 0 |  read book  ", DEFAULT_LINE_NUMBER);

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    /**
     * Verifies that a record missing its task type or status is rejected.
     */
    @Test
    void decode_taskTypeOrStatusMissing_storageExceptionThrown() {
        assertInvalidRecord("T", 7, "missing task type or status");
    }

    /**
     * Verifies that an unknown task type is rejected.
     */
    @Test
    void decode_unknownTaskType_storageExceptionThrown() {
        assertInvalidRecord("X | 0 | read book", DEFAULT_LINE_NUMBER,
                "unknown task type 'X'");
    }

    /**
     * Verifies that an invalid completion status is rejected.
     */
    @Test
    void decode_invalidStatus_storageExceptionThrown() {
        assertInvalidRecord("T | 2 | read book", DEFAULT_LINE_NUMBER,
                "status must be 0 or 1");
    }

    /**
     * Verifies that records with an incorrect number of fields are rejected.
     */
    @Test
    void decode_incorrectFieldCount_storageExceptionThrown() {
        assertInvalidRecord("T | 0", DEFAULT_LINE_NUMBER, "incorrect number of fields");
        assertInvalidRecord("T | 0 | read book | extra", DEFAULT_LINE_NUMBER,
                "incorrect number of fields");
        assertInvalidRecord("D | 0 | return book", DEFAULT_LINE_NUMBER,
                "incorrect number of fields");
        assertInvalidRecord("D | 0 | return book | 2019-10-15 | extra",
                DEFAULT_LINE_NUMBER, "incorrect number of fields");
        assertInvalidRecord("E | 0 | project meeting | 2019-10-20",
                DEFAULT_LINE_NUMBER, "incorrect number of fields");
        assertInvalidRecord(
                "E | 0 | project meeting | 2019-10-20 | 2019-10-21 | extra",
                DEFAULT_LINE_NUMBER, "incorrect number of fields");
    }

    /**
     * Verifies that records containing blank required fields are rejected.
     */
    @Test
    void decode_requiredFieldBlank_storageExceptionThrown() {
        assertInvalidRecord("T | 0 | ", DEFAULT_LINE_NUMBER, "description cannot be empty");
        assertInvalidRecord("D | 0 |  | 2019-10-15", DEFAULT_LINE_NUMBER,
                "description cannot be empty");
        assertInvalidRecord("D | 0 | return book | ", DEFAULT_LINE_NUMBER,
                "deadline cannot be empty");
        assertInvalidRecord("E | 0 |  | 2019-10-20 | 2019-10-21",
                DEFAULT_LINE_NUMBER, "description cannot be empty");
        assertInvalidRecord("E | 0 | project meeting |  | 2019-10-21",
                DEFAULT_LINE_NUMBER, "event start cannot be empty");
        assertInvalidRecord("E | 0 | project meeting | 2019-10-20 | ",
                DEFAULT_LINE_NUMBER, "event end cannot be empty");
    }

    /**
     * Verifies that records containing invalid dates are rejected.
     */
    @Test
    void decode_dateInvalid_storageExceptionThrown() {
        assertInvalidRecord("D | 0 | return book | 15-10-2019", DEFAULT_LINE_NUMBER,
                "deadline must use yyyy-MM-dd");
        assertInvalidRecord("D | 0 | return book | 2019-02-29", DEFAULT_LINE_NUMBER,
                "deadline must use yyyy-MM-dd");
        assertInvalidRecord("E | 0 | project meeting | 2019-02-29 | 2019-10-21",
                DEFAULT_LINE_NUMBER, "event start must use yyyy-MM-dd");
        assertInvalidRecord("E | 0 | project meeting | 2019-10-20 | 2019-02-29",
                DEFAULT_LINE_NUMBER, "event end must use yyyy-MM-dd");
    }

    /**
     * Verifies that an unsupported escape sequence is rejected.
     */
    @Test
    void decode_escapeSequenceInvalid_storageExceptionThrown() {
        assertInvalidRecord("T | 0 | bad \\q escape", DEFAULT_LINE_NUMBER,
                "invalid escape sequence");
    }

    /**
     * Verifies that an unfinished escape sequence is rejected.
     */
    @Test
    void decode_escapeSequenceUnfinished_storageExceptionThrown() {
        assertInvalidRecord("T | 0 | trailing \\", DEFAULT_LINE_NUMBER,
                "unfinished escape sequence");
    }

    private void assertInvalidRecord(String record, int lineNumber, String reason) {
        StorageException exception = assertThrows(StorageException.class,
                () -> taskCodec.decode(record, lineNumber));

        assertEquals("Invalid task data on line " + lineNumber + ": " + reason + ".",
                exception.getMessage());
    }
}
