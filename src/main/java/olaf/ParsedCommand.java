package olaf;

import java.util.Objects;

/**
 * Represents a command that has been validated and converted into data Olaf can execute.
 */
final class ParsedCommand {
    /** Identifies the operation requested by the user. */
    enum Action {
        EXIT,
        LIST,
        FIND,
        ADD,
        MARK,
        UNMARK,
        DELETE
    }

    private final Action action;
    private final Task task;
    private final int taskNumber;
    private final String keyword;

    private ParsedCommand(Action action, Task task, int taskNumber, String keyword) {
        this.action = action;
        this.task = task;
        this.taskNumber = taskNumber;
        this.keyword = keyword;
    }

    /**
     * Creates a command that does not require an additional value.
     *
     * @param action exit or list action.
     * @return parsed command with no payload
     */
    static ParsedCommand createWithoutPayload(Action action) {
        if (action != Action.EXIT && action != Action.LIST) {
            throw new IllegalArgumentException("Only exit and list commands have no payload.");
        }
        return new ParsedCommand(action, null, 0, null);
    }

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task task to add.
     * @return parsed add command
     */
    static ParsedCommand add(Task task) {
        return new ParsedCommand(Action.ADD, Objects.requireNonNull(task), 0, null);
    }

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param keyword text to find in task descriptions
     * @return parsed find command
     */
    static ParsedCommand find(String keyword) {
        return new ParsedCommand(Action.FIND, null, 0, Objects.requireNonNull(keyword));
    }

    /**
     * Creates a command that operates on a user-facing task number.
     *
     * @param action mark, unmark, or delete action.
     * @param taskNumber one-based task number supplied by the user.
     * @return parsed task-number command
     */
    static ParsedCommand createForTaskNumber(Action action, int taskNumber) {
        if (action != Action.MARK && action != Action.UNMARK && action != Action.DELETE) {
            throw new IllegalArgumentException("The action does not accept a task number.");
        }
        return new ParsedCommand(action, null, taskNumber, null);
    }

    /**
     * Returns the operation represented by this command.
     *
     * @return command action
     */
    Action getAction() {
        return action;
    }

    /**
     * Returns the task carried by an add command.
     *
     * @return task to add
     * @throws IllegalStateException if this is not an add command
     */
    Task getTask() {
        if (action != Action.ADD) {
            throw new IllegalStateException("Only an add command contains a task.");
        }
        return task;
    }

    /**
     * Returns the user-facing task number carried by this command.
     *
     * @return one-based task number
     * @throws IllegalStateException if this command does not operate on a task number
     */
    int getTaskNumber() {
        if (action != Action.MARK && action != Action.UNMARK && action != Action.DELETE) {
            throw new IllegalStateException("This command does not contain a task number.");
        }
        return taskNumber;
    }

    String getKeyword() {
        if (action != Action.FIND) {
            throw new IllegalStateException("Only a find command contains a keyword.");
        }
        return keyword;
    }
}
