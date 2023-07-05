package entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    protected static Task task;

    @BeforeAll
    static void CreateTaskTest() {
        task = new Task(1, "name", "description", Status.IN_PROGRESS, 1, LocalDateTime.now());
        task = new Task("name", "description", Status.DONE, 1, LocalDateTime.now());
        task = new Task("Задача 1", "Это \"Задача 1\"");
    }


    @Test
    void setGetStatusTest() {
        task.setStatus(Status.IN_PROGRESS);
        final Status status = task.getStatus();
        assertEquals(status, Status.IN_PROGRESS);
    }


    @Test
    void setGetIdTest() {
        int id = 1_234_567_890;
        task.setId(id);
        final Integer taskId = task.getId();
        assertEquals(id, taskId);
    }

    @Test
    void setGetNameTest() {
        final String name = "Задача 1*";
        task.setName(name);
        assertEquals(name, task.getName());
    }

    @Test
    void setGetDescriptionTest() {
        task.setDescription("Это новое описание");
        final String description = task.getDescription();
        assertEquals("Это новое описание", description);
    }

    @Test
    void getTypeTest() {
        final TaskType type = task.getType();
        assertEquals(type, TaskType.TASK);
    }

    @Test
    void setGetDurationTest() {
        final int duration = 120;
        task.setDuration(duration);
        assertEquals(duration, task.getDuration());
    }

    @Test
    void setGetStartTimeTest() {
        LocalDateTime startTime = LocalDateTime.of(1265, 3, 8, 11, 23);
        task.setStartTime(startTime);
        assertEquals(startTime, task.getStartTime());
    }

    @Test
    void testEquals() {
        Task task1 = new Task("task", "task test", 22, LocalDateTime.of(9005, 3, 8, 11, 23));
        Task task2 = new Task("task", "task test", 22, LocalDateTime.of(9005, 3, 8, 11, 23));
        assertTrue(task1.equals(task2));
        task1.setId(1);
        task2.setId(2);
        assertFalse(task1.equals(task2));

    }

    @Test
    void testHashCode() {
        Task task1 = new Task("task", "task test", 22, LocalDateTime.of(9005, 3, 8, 11, 23));
        Task task2 = new Task("task", "task test", 22, LocalDateTime.of(9005, 3, 8, 11, 23));
        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void testToString() {
        final String string = task.toString();
        assertEquals(string, task.toString());
    }

    @Test
    void getEndTimeTest() {
        LocalDateTime startTime = task.getStartTime();
        int duration = task.getDuration();
        if (startTime == null) {
            startTime = LocalDateTime.of(9005, 3, 8, 11, 23);
            task.setStartTime(startTime);
        }
        if (duration == 0) {
            duration = 123;
            task.setDuration(duration);
        }
        final LocalDateTime endTime = task.getStartTime().plusMinutes(task.getDuration());
        assertEquals(endTime, task.getEndTime());
    }
}