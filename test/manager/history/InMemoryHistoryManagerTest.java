package manager.history;

import entities.Status;
import entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task1, task2, task3;

    @BeforeEach
    public void createHistoryManagerForTest() {
        LocalDateTime startTime = LocalDateTime.parse("2001-10-02T10:11:01");
        task1 = new Task(1, "task1", "this task1", Status.NEW, 12, startTime);
        task2 = new Task(2, "task2", "this task2", Status.NEW,
                1, startTime.plusMinutes(13));
        task3 = new Task(3, "task3", "this task3", Status.NEW,
                1, startTime.plusMinutes(13 + 14));
        historyManager = new InMemoryHistoryManager();
        historyManager.add(task1);
        historyManager.add(task2);

    }

    @Test
    void addTest() {
        final List<Task> history = historyManager.getHistory();
        assertAll(
                () -> assertTrue(history.size() == 2),
                () -> assertTrue(history.contains(task1)),
                () -> assertTrue(history.contains(task2)),
                () -> assertFalse(history.contains(task3))
        );
    }

    @Test
    void removeTest() {
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        historyManager.remove(1000);
        final List<Task> history = historyManager.getHistory();
        assertAll(
                () -> assertTrue(history.size() == 2),
                () -> assertTrue(history.contains(task1)),
                () -> assertFalse(history.contains(task2)),
                () -> assertTrue(history.contains(task3))
        );
    }

    @Test
    void getHistoryTest() {
        final List<Task> history = historyManager.getHistory();
        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        final List<Task> history1 = historyManager.getHistory();
        assertAll(
                () -> assertTrue(history.size() == 2),
                () -> assertTrue(history.contains(task1)),
                () -> assertTrue(history.contains(task2)),
                () -> assertFalse(history.contains(task3)),
                () -> assertTrue(history1.isEmpty())
        );
    }
}