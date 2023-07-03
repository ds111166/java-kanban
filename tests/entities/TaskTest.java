package entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;

class TaskTest {
    protected Task task;
    @BeforeAll
    void CreateTask(){
        task = new Task("Задача 1", "Это \"Задача 1\"");
    }
    @Test
    void getStatus() {
        final Status status = task.getStatus();
        Assertions.assertEquals(status, Status.NEW);
    }

    @Test
    void setStatus() {
        task.setStatus(Status.IN_PROGRESS);
        final Status status = task.getStatus();
        Assertions.assertEquals(status, Status.IN_PROGRESS);
    }

    @Test
    void getId() {
        final Integer taskId = task.getId();
        Assertions.assertNull(taskId);
    }

    @Test
    void setId() {
        int id = 1_234_567_890;
        task.setId(id);
        final Integer taskId = task.getId();
        Assertions.assertEquals(id, taskId);
    }

    @Test
    void getName() {
        final String name = task.getName();
        Assertions.assertEquals("Задача 1", name);
    }

    @Test
    void setName() {
        final String name = "Задача 1*";
        task.setName(name);
        Assertions.assertEquals(name, task.getName());
    }

    @Test
    void getDescription() {
        final String description = task.getDescription();
        Assertions.assertEquals("Это \"Задача 1\"", description);

    }

    @Test
    void setDescription() {
        task.setDescription("Это новое описание");
        final String description = task.getDescription();
        Assertions.assertEquals("Это новое описание", description);
    }

    @Test
    void getType() {
        final TaskType type = task.getType();
        Assertions.assertEquals(type, TaskType.TASK);
    }

    @Test
    void testHashCode() {
        final int i = task.hashCode();

    }

    @Test
    void testEquals() {
    }

    @Test
    void testToString() {
    }


}