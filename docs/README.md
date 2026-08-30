# Olaf User Guide

Olaf is a command-line chatbot that tracks ToDos, deadlines, and events across sessions.
Enter deadline and event dates in `yyyy-MM-dd` format. Olaf displays them as `MMM dd yyyy`.

## Adding ToDos

Use `todo <description>` for a task without a date or time.

Example: `todo borrow book`

Olaf adds and displays the task as `[T][ ] borrow book`.

## Adding deadlines

Use `deadline <description> /by <yyyy-MM-dd>` for a task that must be completed by a given date.

Example: `deadline return book /by 2019-10-15`

Olaf adds and displays the task as `[D][ ] return book (by: Oct 15 2019)`.

## Adding events

Use `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>` for a task with start and end dates.

Example: `event project meeting /from 2019-10-20 /to 2019-10-21`

Olaf adds and displays the task as
`[E][ ] project meeting (from: Oct 20 2019 to: Oct 21 2019)`.

## Managing tasks

- Use `list` to display all tasks and their task numbers.
- Use `mark <task number>` to mark a task as done.
- Use `unmark <task number>` to mark a task as not done.
- Use `delete <task number>` to remove a task. The remaining tasks are renumbered automatically.
- Use `bye` to exit Olaf.

Example: `delete 2` removes the task currently displayed as number 2 and reports how many
tasks remain in the list.


## AI Use Declaration

As my intended career path is NOT related to software engineering, I choose AI-5 as my AI use level, where I get Codex to do the tasks, and then I myself review the results.
