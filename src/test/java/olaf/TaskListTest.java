package olaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests operations on the in-memory task list.
 */
class TaskListTest {
    @Test
    void find_keywordUsesDifferentCase_matchingTasksReturnedInInsertionOrder() {
        Task firstTask = new Todo("Read Book");
        Task secondTask = new Deadline("return book", LocalDate.of(2026, 9, 1));
        TaskList tasks = new TaskList(List.of(firstTask, new Todo("buy groceries"), secondTask));

        List<Task> matchingTasks = tasks.find("BOOK");

        assertEquals(List.of(firstTask, secondTask), matchingTasks);
    }

    @Test
    void find_keywordAppearsWithinWord_partialMatchReturned() {
        Task matchingTask = new Todo("organize notebook");
        TaskList tasks = new TaskList(List.of(matchingTask));

        assertEquals(List.of(matchingTask), tasks.find("book"));
    }

    @Test
    void find_keywordAppearsOnlyInFormattedDate_noMatchReturned() {
        Task datedTask = new Deadline("submit report", LocalDate.of(2026, 8, 30));
        TaskList tasks = new TaskList(List.of(datedTask));

        assertTrue(tasks.find("Aug").isEmpty());
    }

    @Test
    void find_keywordDoesNotAppear_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find("exercise").isEmpty());
    }

    @Test
    void find_matchingTaskReturned_resultListIsImmutable() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> matchingTasks = tasks.find("book");

        assertThrows(UnsupportedOperationException.class,
                () -> matchingTasks.add(new Todo("write book")));
    }
}
