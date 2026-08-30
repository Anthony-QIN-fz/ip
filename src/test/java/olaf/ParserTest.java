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
}
