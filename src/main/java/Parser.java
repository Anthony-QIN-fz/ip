import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses user input into validated commands that Olaf can execute.
 */
final class Parser {
    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";
    private static final String INVALID_MARK_COMMAND_MESSAGE =
            "Use 'mark <task number>' to mark a task as done.";
    private static final String INVALID_UNMARK_COMMAND_MESSAGE =
            "Use 'unmark <task number>' to mark a task as not done.";
    private static final String INVALID_DELETE_COMMAND_MESSAGE =
            "Use 'delete <task number>' to delete a task.";
    private static final String INVALID_TODO_COMMAND_MESSAGE =
            "Use 'todo <description>' to add a ToDo.";
    private static final String INVALID_DEADLINE_COMMAND_MESSAGE =
            "Use 'deadline <description> /by <yyyy-MM-dd>' to add a deadline.";
    private static final String INVALID_EVENT_COMMAND_MESSAGE =
            "Use 'event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>' to add an event.";
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Unknown command. Use todo, deadline, event, list, mark, unmark, delete, or bye.";

    /**
     * Parses one line of user input.
     *
     * @param input raw command entered by the user
     * @return validated command and its required data
     * @throws CommandParseException if the input is unknown or malformed
     */
    ParsedCommand parse(String input) throws CommandParseException {
        String trimmedInput = input.trim();
        if (trimmedInput.equalsIgnoreCase(EXIT_COMMAND)) {
            return ParsedCommand.withoutPayload(ParsedCommand.Action.EXIT);
        }
        if (trimmedInput.equalsIgnoreCase(LIST_COMMAND)) {
            return ParsedCommand.withoutPayload(ParsedCommand.Action.LIST);
        }

        String commandWord = getCommandWord(trimmedInput).toLowerCase(Locale.ROOT);
        return switch (commandWord) {
            case MARK_COMMAND -> parseTaskNumberCommand(
                    input, ParsedCommand.Action.MARK, INVALID_MARK_COMMAND_MESSAGE);
            case UNMARK_COMMAND -> parseTaskNumberCommand(
                    input, ParsedCommand.Action.UNMARK, INVALID_UNMARK_COMMAND_MESSAGE);
            case DELETE_COMMAND -> parseTaskNumberCommand(
                    input, ParsedCommand.Action.DELETE, INVALID_DELETE_COMMAND_MESSAGE);
            case TODO_COMMAND -> parseTodoCommand(input);
            case DEADLINE_COMMAND -> parseDeadlineCommand(input);
            case EVENT_COMMAND -> parseEventCommand(input);
            default -> throw new CommandParseException(UNKNOWN_COMMAND_MESSAGE);
        };
    }

    private ParsedCommand parseTaskNumberCommand(String input, ParsedCommand.Action action,
            String invalidCommandMessage) throws CommandParseException {
        String[] commandParts = input.trim().split("\\s+");
        if (commandParts.length != 2) {
            throw new CommandParseException(invalidCommandMessage);
        }

        try {
            int taskNumber = Integer.parseInt(commandParts[1]);
            return ParsedCommand.forTaskNumber(action, taskNumber);
        } catch (NumberFormatException exception) {
            throw new CommandParseException(invalidCommandMessage);
        }
    }

    private ParsedCommand parseTodoCommand(String input) throws CommandParseException {
        String description = getCommandArguments(input);
        if (description.isEmpty()) {
            throw new CommandParseException(INVALID_TODO_COMMAND_MESSAGE);
        }
        return ParsedCommand.add(new Todo(description));
    }

    private ParsedCommand parseDeadlineCommand(String input) throws CommandParseException {
        String arguments = getCommandArguments(input);
        int byMarkerIndex = findMarker(arguments, BY_MARKER);
        if (byMarkerIndex < 0) {
            throw new CommandParseException(INVALID_DEADLINE_COMMAND_MESSAGE);
        }

        String description = arguments.substring(0, byMarkerIndex).trim();
        String by = arguments.substring(byMarkerIndex + BY_MARKER.length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new CommandParseException(INVALID_DEADLINE_COMMAND_MESSAGE);
        }

        try {
            LocalDate byDate = TaskDateFormat.parse(by);
            return ParsedCommand.add(new Deadline(description, byDate));
        } catch (DateTimeParseException exception) {
            throw new CommandParseException(INVALID_DEADLINE_COMMAND_MESSAGE);
        }
    }

    private ParsedCommand parseEventCommand(String input) throws CommandParseException {
        String arguments = getCommandArguments(input);
        int fromMarkerIndex = findMarker(arguments, FROM_MARKER);
        int toMarkerIndex = findMarker(arguments, TO_MARKER);
        if (fromMarkerIndex < 0 || toMarkerIndex < fromMarkerIndex + FROM_MARKER.length()) {
            throw new CommandParseException(INVALID_EVENT_COMMAND_MESSAGE);
        }

        String description = arguments.substring(0, fromMarkerIndex).trim();
        String from = arguments.substring(fromMarkerIndex + FROM_MARKER.length(), toMarkerIndex).trim();
        String to = arguments.substring(toMarkerIndex + TO_MARKER.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new CommandParseException(INVALID_EVENT_COMMAND_MESSAGE);
        }

        try {
            LocalDate fromDate = TaskDateFormat.parse(from);
            LocalDate toDate = TaskDateFormat.parse(to);
            return ParsedCommand.add(new Event(description, fromDate, toDate));
        } catch (DateTimeParseException exception) {
            throw new CommandParseException(INVALID_EVENT_COMMAND_MESSAGE);
        }
    }

    private String getCommandWord(String input) {
        return input.split("\\s+", 2)[0];
    }

    private String getCommandArguments(String input) {
        String[] commandParts = input.trim().split("\\s+", 2);
        return commandParts.length == 2 ? commandParts[1].trim() : "";
    }

    private int findMarker(String text, String marker) {
        Pattern markerPattern = Pattern.compile(
                "(?i)(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
        Matcher matcher = markerPattern.matcher(text);
        return matcher.find() ? matcher.start() : -1;
    }
}
