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
}
