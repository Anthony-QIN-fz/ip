package olaf;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses user input into validated commands that Olaf can execute.
 */
final class Parser {
    private static final String COMMAND_EXIT = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_RESCHEDULE = "reschedule";
    private static final String MARKER_BY = "/by";
    private static final String MARKER_FROM = "/from";
    private static final String MARKER_TO = "/to";
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
    private static final String INVALID_FIND_COMMAND_MESSAGE =
            "Use 'find <keyword>' to find matching tasks.";
    private static final String INVALID_RESCHEDULE_COMMAND_MESSAGE =
            "Use 'reschedule <task number> /by <yyyy-MM-dd>' for a deadline or "
                    + "'reschedule <task number> /from <yyyy-MM-dd> /to <yyyy-MM-dd>' for an event.";
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Unknown command. Use todo, deadline, event, find, list, mark, unmark, "
                    + "delete, reschedule, or bye.";

    /**
     * Parses one line of user input.
     *
     * @param input raw command entered by the user.
     * @return validated command and its required data
     * @throws CommandParseException if the input is unknown or malformed
     */
    ParsedCommand parse(String input) throws CommandParseException {
        String trimmedInput = input.trim();
        if (trimmedInput.equalsIgnoreCase(COMMAND_EXIT)) {
            return ParsedCommand.createWithoutPayload(ParsedCommand.Action.EXIT);
        }
        if (trimmedInput.equalsIgnoreCase(COMMAND_LIST)) {
            return ParsedCommand.createWithoutPayload(ParsedCommand.Action.LIST);
        }

        String[] commandParts = trimmedInput.split("\\s+", 2);
        String commandWord = commandParts[0].toLowerCase(Locale.ROOT);
        String arguments = commandParts.length == 2 ? commandParts[1] : "";
        return switch (commandWord) {
            case COMMAND_MARK -> parseTaskNumberCommand(
                    arguments, ParsedCommand.Action.MARK, INVALID_MARK_COMMAND_MESSAGE);
            case COMMAND_UNMARK -> parseTaskNumberCommand(
                    arguments, ParsedCommand.Action.UNMARK, INVALID_UNMARK_COMMAND_MESSAGE);
            case COMMAND_DELETE -> parseTaskNumberCommand(
                    arguments, ParsedCommand.Action.DELETE, INVALID_DELETE_COMMAND_MESSAGE);
            case COMMAND_TODO -> parseTodoCommand(arguments);
            case COMMAND_DEADLINE -> parseDeadlineCommand(arguments);
            case COMMAND_EVENT -> parseEventCommand(arguments);
            case COMMAND_FIND -> parseFindCommand(arguments);
            case COMMAND_RESCHEDULE -> parseRescheduleCommand(arguments);
            default -> throw new CommandParseException(UNKNOWN_COMMAND_MESSAGE);
        };
    }

    private ParsedCommand parseTaskNumberCommand(String arguments, ParsedCommand.Action action,
            String invalidCommandMessage) throws CommandParseException {
        return ParsedCommand.createForTaskNumber(action, parseTaskNumber(arguments, invalidCommandMessage));
    }

    private int parseTaskNumber(String text, String invalidCommandMessage) throws CommandParseException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new CommandParseException(invalidCommandMessage);
        }
    }

    private ParsedCommand parseTodoCommand(String arguments) throws CommandParseException {
        String description = requireText(arguments, INVALID_TODO_COMMAND_MESSAGE);
        return ParsedCommand.add(new Todo(description));
    }

    private ParsedCommand parseDeadlineCommand(String arguments) throws CommandParseException {
        String trimmedArguments = arguments.trim();
        int byMarkerIndex = findMarker(trimmedArguments, MARKER_BY);
        if (byMarkerIndex < 0) {
            throw new CommandParseException(INVALID_DEADLINE_COMMAND_MESSAGE);
        }

        String description = requireText(trimmedArguments.substring(0, byMarkerIndex),
                INVALID_DEADLINE_COMMAND_MESSAGE);
        String dueDateText = requireText(trimmedArguments.substring(byMarkerIndex + MARKER_BY.length()),
                INVALID_DEADLINE_COMMAND_MESSAGE);
        LocalDate dueDate = parseDate(dueDateText, INVALID_DEADLINE_COMMAND_MESSAGE);
        return ParsedCommand.add(new Deadline(description, dueDate));
    }

    private ParsedCommand parseEventCommand(String arguments) throws CommandParseException {
        String trimmedArguments = arguments.trim();
        int fromMarkerIndex = findMarker(trimmedArguments, MARKER_FROM);
        int toMarkerIndex = findMarker(trimmedArguments, MARKER_TO);
        if (fromMarkerIndex < 0 || toMarkerIndex < fromMarkerIndex + MARKER_FROM.length()) {
            throw new CommandParseException(INVALID_EVENT_COMMAND_MESSAGE);
        }

        String description = requireText(trimmedArguments.substring(0, fromMarkerIndex),
                INVALID_EVENT_COMMAND_MESSAGE);
        String startDateText = requireText(
                trimmedArguments.substring(fromMarkerIndex + MARKER_FROM.length(), toMarkerIndex),
                INVALID_EVENT_COMMAND_MESSAGE);
        String endDateText = requireText(trimmedArguments.substring(toMarkerIndex + MARKER_TO.length()),
                INVALID_EVENT_COMMAND_MESSAGE);
        LocalDate startDate = parseDate(startDateText, INVALID_EVENT_COMMAND_MESSAGE);
        LocalDate endDate = parseDate(endDateText, INVALID_EVENT_COMMAND_MESSAGE);
        return ParsedCommand.add(new Event(description, startDate, endDate));
    }

    private ParsedCommand parseFindCommand(String arguments) throws CommandParseException {
        String keyword = requireText(arguments, INVALID_FIND_COMMAND_MESSAGE);
        return ParsedCommand.find(keyword);
    }

    private ParsedCommand parseRescheduleCommand(String arguments) throws CommandParseException {
        String[] parts = arguments.trim().split("\\s+");
        if (parts.length == 3 && parts[1].equalsIgnoreCase(MARKER_BY)) {
            int taskNumber = parseTaskNumber(parts[0], INVALID_RESCHEDULE_COMMAND_MESSAGE);
            LocalDate dueDate = parseDate(parts[2], INVALID_RESCHEDULE_COMMAND_MESSAGE);
            return ParsedCommand.rescheduleDeadline(taskNumber, dueDate);
        }
        if (parts.length == 5 && parts[1].equalsIgnoreCase(MARKER_FROM)
                && parts[3].equalsIgnoreCase(MARKER_TO)) {
            int taskNumber = parseTaskNumber(parts[0], INVALID_RESCHEDULE_COMMAND_MESSAGE);
            LocalDate startDate = parseDate(parts[2], INVALID_RESCHEDULE_COMMAND_MESSAGE);
            LocalDate endDate = parseDate(parts[4], INVALID_RESCHEDULE_COMMAND_MESSAGE);
            return ParsedCommand.rescheduleEvent(taskNumber, startDate, endDate);
        }
        throw new CommandParseException(INVALID_RESCHEDULE_COMMAND_MESSAGE);
    }

    private String requireText(String text, String invalidCommandMessage) throws CommandParseException {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new CommandParseException(invalidCommandMessage);
        }
        return trimmedText;
    }

    private LocalDate parseDate(String dateText, String invalidCommandMessage) throws CommandParseException {
        try {
            return TaskDateFormat.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new CommandParseException(invalidCommandMessage);
        }
    }

    /**
     * Finds the first case-insensitive marker bounded by whitespace or the start/end of the text.
     * These boundaries prevent words such as {@code /bylaws} from being treated as markers.
     */
    private int findMarker(String text, String marker) {
        Pattern markerPattern = Pattern.compile(
                "(?i)(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
        Matcher matcher = markerPattern.matcher(text);
        return matcher.find() ? matcher.start() : -1;
    }
}
