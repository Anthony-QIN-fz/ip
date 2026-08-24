# Olaf User Guide

Olaf is a command-line chatbot that tracks ToDos, deadlines, and events for the current session.
Dates and times are stored exactly as text, so you can use the format that is most useful to you.

## Adding ToDos

Use `todo <description>` for a task without a date or time.

Example: `todo borrow book`

Olaf adds and displays the task as `[T][ ] borrow book`.

## Adding deadlines

Use `deadline <description> /by <date or time>` for a task that must be completed by a given time.

Example: `deadline return book /by Sunday`

Olaf adds and displays the task as `[D][ ] return book (by: Sunday)`.

## Adding events

Use `event <description> /from <start> /to <end>` for a task with a start and end.

Example: `event project meeting /from Mon 2pm /to 4pm`

Olaf adds and displays the task as `[E][ ] project meeting (from: Mon 2pm to: 4pm)`.

## Managing tasks

- Use `list` to display all tasks and their task numbers.
- Use `mark <task number>` to mark a task as done.
- Use `unmark <task number>` to mark a task as not done.
- Use `delete <task number>` to remove a task. The remaining tasks are renumbered automatically.
- Use `bye` to exit Olaf.

Example: `delete 2` removes the task currently displayed as number 2 and reports how many
tasks remain in the list.


## AI Use Declaration

As my intended career path is NOT related to software engineering, I choose AI-5 as my AI use level, where I get Codex to do the tasks, and then I myself review the results fully, including the code, tests, behavior etc.
