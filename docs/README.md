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

The suite tests task encoding and decoding, including escaped characters and malformed storage
records. A successful run ends with `BUILD SUCCESSFUL`, and the HTML report is generated at
`build/reports/tests/test/index.html`.

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
