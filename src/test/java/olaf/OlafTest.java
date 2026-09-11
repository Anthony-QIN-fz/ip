package olaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Olaf's interface-independent command facade.
 */
class OlafTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void executeCommand_addAndDelete_exactCountMessagesReturned() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));

        assertEquals(" Got it. I've added this task:\n   [T][ ] read book"
                + "\n Now you have 1 task in the list.", olaf.executeCommand("todo read book").message());
        assertEquals(" Got it. I've added this task:\n   [T][ ] write notes"
                + "\n Now you have 2 tasks in the list.", olaf.executeCommand("todo write notes").message());
        assertEquals(" Noted. I've removed this task:\n   [T][ ] read book"
                + "\n Now you have 1 task in the list.", olaf.executeCommand("delete 1").message());
        assertEquals(" Noted. I've removed this task:\n   [T][ ] write notes"
                + "\n Now you have 0 tasks in the list.", olaf.executeCommand("delete 1").message());
    }

    @Test
    void executeCommand_allTaskTypes_listFormattingPreserved() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));
        olaf.executeCommand("todo read book");
        olaf.executeCommand("deadline return book /by 2026-09-01");
        olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");

        String[] taskDescriptions = {"[T][ ] read book", "[D][ ] return book (by: Sep 01 2026)",
                "[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)"};
        for (int index = 0; index < taskDescriptions.length; index++) {
            Olaf.CommandResult result = olaf.executeCommand("mark " + (index + 1));
            assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
            assertEquals(" Nice! I've marked this task as done:\n   "
                    + taskDescriptions[index].replace("[ ]", "[X]"), result.message());
        }

        String expectedList = String.join(System.lineSeparator(), " Here are the tasks in your list:",
                " 1.[T][X] read book", " 2.[D][X] return book (by: Sep 01 2026)",
                " 3.[E][X] meeting (from: Sep 01 2026 to: Sep 02 2026)");
        assertEquals(expectedList, olaf.executeCommand("list").message());
    }

    @Test
    void executeCommand_markThenReload_completionPersisted() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");

        Olaf.CommandResult result = olaf.executeCommand("mark 1");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" Nice! I've marked this task as done:\n   [T][X] read book", result.message());
        assertEquals(" Here are the tasks in your list:" + System.lineSeparator() + " 1.[T][X] read book",
                new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_unmarkThenReload_completionPersisted() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");
        olaf.executeCommand("mark 1");

        Olaf.CommandResult result = olaf.executeCommand("unmark 1");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" OK, I've marked this task as not done yet:\n   [T][ ] read book", result.message());
        assertEquals(" Here are the tasks in your list:" + System.lineSeparator() + " 1.[T][ ] read book",
                new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_deleteThenReload_remainingTaskRenumbered() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");
        olaf.executeCommand("todo write notes");

        Olaf.CommandResult result = olaf.executeCommand("delete 1");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" Here are the tasks in your list:" + System.lineSeparator() + " 1.[T][ ] write notes",
                new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_addThenReload_taskWasPersisted() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);

        Olaf.CommandResult addResult = olaf.executeCommand("todo read book");
        Olaf.CommandResult listResult = new Olaf(dataFile).executeCommand("list");

        assertEquals(Olaf.CommandStatus.CONTINUE, addResult.status());
        assertTrue(addResult.message().contains("[T][ ] read book"));
        assertEquals(Olaf.CommandStatus.CONTINUE, listResult.status());
        assertTrue(listResult.message().contains("1.[T][ ] read book"));
    }

    @Test
    void executeCommand_unknownCommand_recoverableErrorReturned() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));

        Olaf.CommandResult result = olaf.executeCommand("dance");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertTrue(result.message().startsWith("error: Unknown command."));
    }

    @Test
    void executeCommand_invalidTaskNumber_recoverableErrorReturned() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));

        Olaf.CommandResult result = olaf.executeCommand("mark 1");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals("error: There are no tasks in your list.", result.message());
    }

    @Test
    void executeCommand_bye_exitRequested() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));

        Olaf.CommandResult result = olaf.executeCommand("bye");

        assertEquals(Olaf.CommandStatus.EXIT_REQUESTED, result.status());
        assertEquals("Bye. Hope to see you again soon!", result.message());
    }

    @Test
    void executeCommand_storageCannotSave_fatalErrorReturned()
            throws IOException, StorageException {
        Path fileInsteadOfDirectory = temporaryDirectory.resolve("blocked");
        Files.writeString(fileInsteadOfDirectory, "not a directory");
        Olaf olaf = new Olaf(fileInsteadOfDirectory.resolve("olaf.txt"));

        Olaf.CommandResult result = olaf.executeCommand("todo read book");

        assertEquals(Olaf.CommandStatus.FATAL_ERROR, result.status());
        assertTrue(result.message().startsWith("error: Unable to save task data"));
    }

    @Test
    void executeCommand_rescheduleDeadlineThenReload_onlyDateChanged() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");
        olaf.executeCommand("deadline return book /by 2026-09-01");
        olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");
        olaf.executeCommand("mark 2");

        Olaf.CommandResult result = olaf.executeCommand("reschedule 2 /by 2026-09-20");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" OK, I've rescheduled this task:\n   [D][X] return book (by: Sep 20 2026)",
                result.message());
        String expectedList = String.join(System.lineSeparator(), " Here are the tasks in your list:",
                " 1.[T][ ] read book", " 2.[D][X] return book (by: Sep 20 2026)",
                " 3.[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)");
        assertEquals(expectedList, olaf.executeCommand("list").message());
        assertEquals(expectedList, new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_rescheduleEventThenReload_onlyDatesChanged() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("deadline return book /by 2026-09-01");
        olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");
        olaf.executeCommand("todo write notes");

        Olaf.CommandResult result = olaf.executeCommand("reschedule 2 /from 2026-09-20 /to 2026-09-22");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" OK, I've rescheduled this task:\n"
                + "   [E][ ] meeting (from: Sep 20 2026 to: Sep 22 2026)", result.message());
        String expectedList = String.join(System.lineSeparator(), " Here are the tasks in your list:",
                " 1.[D][ ] return book (by: Sep 01 2026)",
                " 2.[E][ ] meeting (from: Sep 20 2026 to: Sep 22 2026)", " 3.[T][ ] write notes");
        assertEquals(expectedList, olaf.executeCommand("list").message());
        assertEquals(expectedList, new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_rescheduleInvalid_memoryAndFileUnchanged() throws IOException, StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");
        olaf.executeCommand("deadline return book /by 2026-09-01");
        olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");
        olaf.executeCommand("mark 3");
        String originalList = olaf.executeCommand("list").message();
        String originalFile = Files.readString(dataFile);
        String[] invalidCommands = {"reschedule", "reschedule 0 /by 2026-09-20",
                "reschedule -1 /by 2026-09-20", "reschedule 4 /by 2026-09-20",
                "reschedule 4 /from 2026-09-20 /to 2026-09-22", "reschedule 1 /by 2026-09-20",
                "reschedule 1 /from 2026-09-20 /to 2026-09-22",
                "reschedule 2 /from 2026-09-20 /to 2026-09-22", "reschedule 3 /by 2026-09-20",
                "reschedule 2 /by 2026-02-29", "reschedule 2 /by 2026-09-20 extra",
                "reschedule 2 /by 2026-09-20 /by 2026-09-21",
                "reschedule 3 /from 2026-09-20 /to 2026-02-29",
                "reschedule 3 /from 2026-09-22 /to 2026-09-20"};

        for (String command : invalidCommands) {
            Olaf.CommandResult result = olaf.executeCommand(command);

            assertEquals(Olaf.CommandStatus.CONTINUE, result.status(), command);
            assertTrue(result.message().startsWith("error: "), command);
            assertEquals(originalList, olaf.executeCommand("list").message(), command);
            assertEquals(originalFile, Files.readString(dataFile), command);
        }
    }

    @Test
    void executeCommand_rescheduleEmptyList_recoverableErrorWithoutCreatingFile() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);

        Olaf.CommandResult result = olaf.executeCommand("reschedule 1 /by 2026-09-20");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals("error: There are no tasks in your list.", result.message());
        assertTrue(Files.notExists(dataFile));
    }

    @Test
    void executeCommand_rescheduleLegacyReversedEvent_validRangeSaved() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("event meeting /from 2026-09-02 /to 2026-09-01");
        Olaf reloadedOlaf = new Olaf(dataFile);

        Olaf.CommandResult result = reloadedOlaf.executeCommand(
                "reschedule 1 /from 2026-09-20 /to 2026-09-20");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" OK, I've rescheduled this task:\n"
                + "   [E][ ] meeting (from: Sep 20 2026 to: Sep 20 2026)", result.message());
        assertEquals(reloadedOlaf.executeCommand("list").message(),
                new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_rescheduleCannotSave_fatalErrorReturned() throws IOException, StorageException {
        String[] commands = {"reschedule 1 /by 2026-09-20",
                "reschedule 2 /from 2026-09-20 /to 2026-09-22"};
        for (int index = 0; index < commands.length; index++) {
            Path dataFile = temporaryDirectory.resolve("olaf" + index + ".txt");
            Olaf olaf = new Olaf(dataFile);
            olaf.executeCommand("deadline return book /by 2026-09-01");
            olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");
            Files.delete(dataFile);
            Files.createDirectory(dataFile);

            Olaf.CommandResult result = olaf.executeCommand(commands[index]);

            assertEquals(Olaf.CommandStatus.FATAL_ERROR, result.status());
            assertTrue(result.message().startsWith("error: Unable to save task data"));
        }
    }
}
