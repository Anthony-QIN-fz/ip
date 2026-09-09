package olaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion of user input into validated commands.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_noPayloadCommandsMixedCaseAndWhitespace_correctActionsReturned() throws CommandParseException {
        assertEquals(ParsedCommand.Action.EXIT, parser.parse(" \tByE  ").getAction());
        assertEquals(ParsedCommand.Action.LIST, parser.parse("  LiSt\t").getAction());
        assertEquals(ParsedCommand.Action.LIST, parser.parse("L\u0130ST").getAction());
    }

    @Test
    void parse_unknownOrUnexpectedArguments_unknownCommandMessageReturned() {
        for (String input : new String[] {"", "  ", "dance", "list extra", "bye extra"}) {
            assertInvalidCommand(input,
                    "Unknown command. Use todo, deadline, event, find, list, mark, unmark, delete, or bye.");
        }
    }

    @Test
    void parse_todoMixedCaseAndWhitespace_descriptionPreserved() throws CommandParseException {
        ParsedCommand command = parser.parse("  ToDo\t read  book  ");

        assertEquals(ParsedCommand.Action.ADD, command.getAction());
        assertEquals("[T][ ] read  book", command.getTask().toString());
    }

    @Test
    void parse_deadlineMixedCaseAndMarkers_validTaskReturned() throws CommandParseException {
        ParsedCommand command = parser.parse("  DeAdLiNe\treturn book \t/BY\t2026-09-01  ");

        assertEquals(ParsedCommand.Action.ADD, command.getAction());
        assertEquals("[D][ ] return book (by: Sep 01 2026)", command.getTask().toString());
    }

    @Test
    void parse_eventMixedCaseAndMarkers_validTaskReturned() throws CommandParseException {
        ParsedCommand command = parser.parse(" EvEnT meeting\t/FROM 2026-09-01 /TO\t2026-09-02 ");

        assertEquals(ParsedCommand.Action.ADD, command.getAction());
        assertEquals("[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)", command.getTask().toString());
    }

    @Test
    void parse_eventEndBeforeStart_datesPreserved() throws CommandParseException {
        ParsedCommand command = parser.parse("event meeting /from 2026-09-02 /to 2026-09-01");

        assertEquals("[E][ ] meeting (from: Sep 02 2026 to: Sep 01 2026)", command.getTask().toString());
    }

    @Test
    void parse_markerInsideDescription_notTreatedAsDelimiter() throws CommandParseException {
        ParsedCommand command = parser.parse("deadline read /bylaws /by 2026-09-01");

        assertEquals("read /bylaws", command.getTask().getDescription());
    }

    @Test
    void parse_deadlineMalformed_usageMessageReturned() {
        String[] inputs = {"deadline", "deadline book", "deadline /by 2026-09-01",
                "deadline book /by", "deadline book/by 2026-09-01", "deadline book /by2026-09-01",
                "deadline book /by 2026-02-29", "deadline book /by 2026-9-01",
                "deadline book /by 01-09-2026", "deadline book /by 2026-09-01 /by 2026-09-02"};

        for (String input : inputs) {
            assertInvalidCommand(input, "Use 'deadline <description> /by <yyyy-MM-dd>' to add a deadline.");
        }
    }

    @Test
    void parse_eventMalformed_usageMessageReturned() {
        String[] inputs = {"event", "event meeting /from 2026-09-01", "event /from 2026-09-01 /to 2026-09-02",
                "event meeting /from /to 2026-09-02", "event meeting /from 2026-09-01 /to",
                "event meeting /to 2026-09-02 /from 2026-09-01",
                "event meeting/from 2026-09-01 /to 2026-09-02",
                "event meeting /from2026-09-01 /to 2026-09-02",
                "event meeting /from 2026-09-01/to 2026-09-02",
                "event meeting /from 2026-09-01 /to2026-09-02",
                "event meeting /from 2026-02-29 /to 2026-09-02",
                "event meeting /from 2026-09-01 /to 2026-02-29"};

        for (String input : inputs) {
            assertInvalidCommand(input,
                    "Use 'event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>' to add an event.");
        }
    }

    @Test
    void parse_todoWithoutDescription_usageMessageReturned() {
        assertInvalidCommand("todo \t", "Use 'todo <description>' to add a ToDo.");
    }

    @Test
    void parse_taskNumberCommandsMixedCase_correctActionsReturned() throws CommandParseException {
        assertTaskNumberCommand(" MaRk\t+1 ", ParsedCommand.Action.MARK, 1);
        assertTaskNumberCommand(" UnMaRk  2 ", ParsedCommand.Action.UNMARK, 2);
        assertTaskNumberCommand(" DeLeTe 3 ", ParsedCommand.Action.DELETE, 3);
    }

    @Test
    void parse_nonpositiveTaskNumbers_leftForTaskListValidation() throws CommandParseException {
        assertTaskNumberCommand("mark 0", ParsedCommand.Action.MARK, 0);
        assertTaskNumberCommand("unmark -1", ParsedCommand.Action.UNMARK, -1);
        assertTaskNumberCommand("delete -2147483648", ParsedCommand.Action.DELETE, Integer.MIN_VALUE);
    }

    @Test
    void parse_taskNumberSyntaxInvalid_usageMessageReturned() {
        String[] arguments = {"", "one", "1 2", "1.5", "2147483648", "\u0000+1"};
        for (String argument : arguments) {
            assertInvalidCommand("mark " + argument, "Use 'mark <task number>' to mark a task as done.");
            assertInvalidCommand("unmark " + argument, "Use 'unmark <task number>' to mark a task as not done.");
            assertInvalidCommand("delete " + argument, "Use 'delete <task number>' to delete a task.");
        }
    }

    @Test
    void parse_findKeyword_findCommandReturned() throws CommandParseException {
        ParsedCommand command = parser.parse("find book");

        assertEquals(ParsedCommand.Action.FIND, command.getAction());
        assertEquals("book", command.getKeyword());
    }

    @Test
    void parse_findCommandUsesMixedCase_findCommandReturned() throws CommandParseException {
        ParsedCommand command = parser.parse("FiNd book");

        assertEquals(ParsedCommand.Action.FIND, command.getAction());
        assertEquals("book", command.getKeyword());
    }

    @Test
    void parse_findMultiWordPhrase_completePhraseReturned() throws CommandParseException {
        ParsedCommand command = parser.parse("  find   project meeting  ");

        assertEquals("project meeting", command.getKeyword());
    }

    @Test
    void parse_findWithoutKeyword_commandParseExceptionThrown() {
        CommandParseException exception = assertThrows(CommandParseException.class,
                () -> parser.parse("find   "));

        assertEquals("Use 'find <keyword>' to find matching tasks.", exception.getMessage());
    }

    private void assertInvalidCommand(String input, String expectedMessage) {
        CommandParseException exception = assertThrows(CommandParseException.class, () -> parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void assertTaskNumberCommand(String input, ParsedCommand.Action expectedAction, int expectedNumber)
            throws CommandParseException {
        ParsedCommand command = parser.parse(input);
        assertEquals(expectedAction, command.getAction());
        assertEquals(expectedNumber, command.getTaskNumber());
    }
}
