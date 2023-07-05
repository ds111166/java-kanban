package entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    protected static Subtask subtask;

    @BeforeAll
    static void CreateSubaskTest() {
        Subtask subtask1 = new Subtask(2, "Под Задача 1", "Это \"Под Задача 1 эпика ID=2\"");
        Subtask subtask2 = new Subtask(1, "name", "description", 1, LocalDateTime.now());
        subtask = new Subtask(333, 3, "name333", "description", Status.DONE, 1,
                LocalDateTime.of(9005, 3, 8, 11, 23));

    }

    @Test
    void setGetEpicId() {
        int epicId = subtask.getEpicId();
        assertEquals(epicId, 333);
        subtask.setEpicId(444);
        epicId = subtask.getEpicId();
        assertEquals(epicId, 444);
    }

    @Test
    void getType() {
        final TaskType type = subtask.getType();
        assertEquals(type, TaskType.SUBTASK);
    }

    @Test
    void testEquals() {
        Subtask subtask1 = new Subtask(333, 3, "name333", "description", Status.DONE, 1,
                LocalDateTime.of(9005, 3, 8, 11, 23));
        Subtask subtask2 = new Subtask(333, 3, "name333", "description", Status.DONE, 1,
                LocalDateTime.of(9005, 3, 8, 11, 23));
        assertTrue(subtask1.equals(subtask2));
        subtask1.setId(1);
        subtask2.setId(2);
        assertFalse(subtask1.equals(subtask2));

    }

    @Test
    void testHashCode() {
        Subtask subtask1 = new Subtask(333, 3, "name333", "description", Status.DONE, 1,
                LocalDateTime.of(9005, 3, 8, 11, 23));
        Subtask subtask2 = new Subtask(333, 3, "name333", "description", Status.DONE, 1,
                LocalDateTime.of(9005, 3, 8, 11, 23));
        assertEquals(subtask1.hashCode(), subtask2.hashCode());
    }

    @Test
    void testToString() {
        final String string = "Subtask{epicId=333, id=3, name='name333', description='description', status=DONE, duration=1, startTime=9005-03-08T11:23}";
        assertEquals(string, subtask.toString());
    }
}