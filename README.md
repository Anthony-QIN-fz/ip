# Olaf project

Olaf is a chatbot built as a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Olaf.java` file, right-click it, and choose `Run Olaf.main()` (if the code editor is showing compile errors, try restarting the IDE).

Olaf stores ToDos, deadlines, and events across application sessions. Add them with
`todo <description>`, `deadline <description> /by <yyyy-MM-dd>`, or
`event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>`. Enter dates in `yyyy-MM-dd`
format; Olaf displays them in `MMM dd yyyy` format.
Enter `list` to display the stored tasks, `mark <task number>` to mark a task as done,
`unmark <task number>` to mark a task as not done, `delete <task number>` to remove a task,
or `bye` to exit. A typical session looks like this:

Olaf loads tasks from `data/olaf.txt` when it starts and automatically saves the file whenever
the task list changes. The `data` directory and file are created on the first task change.

   ```
     ___  _        __
    / _ \| | __ _ / _|
   | | | | |/ _` | |_
   | |_| | | (_| |  _|
   \___/|_|\__,_|_|

   Hello! I'm Olaf. What can I do for you?
   ____________________________________________________________
   todo read book
   ____________________________________________________________
    Got it. I've added this task:
      [T][ ] read book
    Now you have 1 task in the list.
   ____________________________________________________________
   deadline return book /by 2019-10-15
   ____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Oct 15 2019)
    Now you have 2 tasks in the list.
   ____________________________________________________________
   event project meeting /from 2019-10-20 /to 2019-10-21
   ____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Oct 20 2019 to: Oct 21 2019)
    Now you have 3 tasks in the list.
   ____________________________________________________________
   list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Oct 15 2019)
    3.[E][ ] project meeting (from: Oct 20 2019 to: Oct 21 2019)
   ____________________________________________________________
   mark 2
   ____________________________________________________________
    Nice! I've marked this task as done:
      [D][X] return book (by: Oct 15 2019)
   ____________________________________________________________
   list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Oct 15 2019)
    3.[E][ ] project meeting (from: Oct 20 2019 to: Oct 21 2019)
   ____________________________________________________________
   unmark 2
   ____________________________________________________________
    OK, I've marked this task as not done yet:
      [D][ ] return book (by: Oct 15 2019)
   ____________________________________________________________
   delete 1
   ____________________________________________________________
    Noted. I've removed this task:
      [T][ ] read book
    Now you have 2 tasks in the list.
   ____________________________________________________________
   list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[D][ ] return book (by: Oct 15 2019)
    2.[E][ ] project meeting (from: Oct 20 2019 to: Oct 21 2019)
   ____________________________________________________________
   bye
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.


## AI Use Declaration

As my intended career path is NOT related to software engineering, I choose AI-5 as my AI use level, where I get Codex to do the tasks, and then I myself review the results.
