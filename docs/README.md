# Olaf User Guide

Olaf is a desktop chatbot that tracks ToDos, deadlines, and events across sessions.
Enter deadline and event dates in `yyyy-MM-dd` format. Olaf displays them as `MMM dd yyyy`.

## Running with Gradle

Prerequisite: JDK 25.

1. Open a terminal in the project root folder containing `build.gradle`.
1. Run `java --version` and confirm that it reports Java 25.
1. Launch Olaf with the command for your operating system and terminal:

   | Environment | Command |
   |---|---|
   | Windows PowerShell | `.\gradlew.bat run` |
   | Windows Command Prompt | `gradlew.bat run` |
   | macOS/Linux with Bash, Zsh, Fish, or PowerShell | `./gradlew run` |
   | Git Bash or WSL | `./gradlew run` |

   If you are using an IDE, run the matching command in its integrated terminal.
1. After Olaf's window displays its welcome message, enter `bye` and press **Enter** or click
   **Send** to verify that it runs and exits normally.

Commands can be submitted either by pressing **Enter** in the command field or by clicking
**Send**. The conversation scrolls automatically as new messages are added.

On macOS or Linux, if the terminal reports that `gradlew` is not executable, run
`chmod +x gradlew` and then retry `./gradlew run`.

In Visual Studio Code, use `Java: Configure Java Runtime` from the Command Palette to select
**JDK 25**. Do not launch Olaf with Code Runner's **Run Code** command, the Java **Run** link,
or `F5`; use the Gradle wrapper command above so the complete project is built and launched.

## Running tests

From the project root, run the test suite with the command for your operating system and terminal:

| Environment | Command |
|---|---|
| Windows PowerShell | `.\gradlew.bat test` |
| Windows Command Prompt | `gradlew.bat test` |
| macOS/Linux with Bash, Zsh, Fish, or PowerShell | `./gradlew test` |
| Git Bash or WSL | `./gradlew test` |

The suite tests command parsing, task operations, rescheduling, persistence, and task encoding,
including escaped characters and malformed storage records. A successful run ends with
`BUILD SUCCESSFUL`, and the HTML report is generated at
`build/reports/tests/test/index.html`.

On Windows, if Gradle cannot load `GradleWorkerMain`, retry with
`.\gradlew.bat test '-Dfile.encoding=COMPAT'` while still using JDK 25.

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
- Use `reschedule` to change a deadline or event's dates, as described below.
- Use `bye` to exit Olaf.

Example: `delete 2` removes the task currently displayed as number 2 and reports how many
tasks remain in the list.

## Rescheduling deadlines and events

Use `list` to find the task's number in the full task list. Numbers in `find` results are local to
that search and should not be used for rescheduling. Enter commands in the chat window or console.

For a deadline, use `reschedule <task number> /by <yyyy-MM-dd>`.

Example: `reschedule 2 /by 2026-09-20`

```text
 OK, I've rescheduled this task:
   [D][ ] return book (by: Sep 20 2026)
```

For an event, supply both dates using
`reschedule <task number> /from <yyyy-MM-dd> /to <yyyy-MM-dd>`.

Example: `reschedule 3 /from 2026-09-20 /to 2026-09-22`

```text
 OK, I've rescheduled this task:
   [E][ ] meeting (from: Sep 20 2026 to: Sep 22 2026)
```

- Dates must use `yyyy-MM-dd`. Earlier, later, past, and unchanged dates are accepted.
- Both event dates are replaced, so the event's duration may change. The end must be on or after
  the start; same-day events are allowed.
- The description, completion status, task type, and list position are preserved. Completed
  tasks stay completed; use `unmark <task number>` to reopen them.
- ToDos have no dates and cannot be rescheduled. Choose a deadline or event instead.
- Use `/by` for deadlines and `/from` followed by `/to` for events. Commands and markers ignore letter case.
- Only exact dates are supported. Relative delays such as `tomorrow` or `3 days` and reminder snoozing
  are unsupported.

Successful changes are saved automatically and persist after restarting Olaf. Invalid task
numbers, malformed dates, incomplete commands, and reversed event ranges produce an error
without changing the task list or saved data. For example, rescheduling an event with an end
before its start returns `error: The event end date must be on or after its start date.`


## AI Use Declaration

As my intended career path is NOT related to software engineering, I choose AI-5 as my AI use level, where I get Codex to do the tasks, and then I myself review the results.
