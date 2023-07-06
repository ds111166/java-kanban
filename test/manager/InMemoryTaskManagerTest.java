package manager;


import entities.Epic;
import entities.Status;
import entities.Subtask;
import entities.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    @Override
    public void createManagerForTest() {
        taskManager = new InMemoryTaskManager();
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        createTasksTest();
    }

    @AfterEach
    @Override
    protected void clearManagerForTest() {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskManager.getHistory()
                .stream()
                .map(Task::getId)
                .forEach(id -> taskManager.history.remove(id));
    }

    @Test
    void updateEpicStatusTest() {
        Integer epicId = taskManager.createEpic(new Epic("EpicStatusTest", "this EpicStatusTest"));
        final Epic epic = taskManager.getEpic(epicId);
        final Status status = epic.getStatus();


        Integer subId1 = taskManager.createSubtask(
                new Subtask(epicId, "sub1", "this sub1"));
        Integer subId2 = taskManager.createSubtask(
                new Subtask(epicId, "sub2", "this sub2"));
        Integer subId3 = taskManager.createSubtask(
                new Subtask(epicId, "sub3", "this sub3"));
        final Status status1 = epic.getStatus();

        Subtask subtask1 = taskManager.getSubtask(subId1);
        Subtask subtask2 = taskManager.getSubtask(subId2);
        Subtask subtask3 = taskManager.getSubtask(subId3);
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        subtask3.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);
        final Status status2 = taskManager.getEpic(epicId).getStatus();

        subtask1 = taskManager.getSubtask(subId1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        final Status status3 = taskManager.getEpic(epicId).getStatus();

        subtask2 = taskManager.getSubtask(subId2);
        subtask3 = taskManager.getSubtask(subId3);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);
        final Status status4 = taskManager.getEpic(epicId).getStatus();

        subtask1 = taskManager.getSubtask(subId1);
        subtask2 = taskManager.getSubtask(subId2);
        subtask3 = taskManager.getSubtask(subId3);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);
        final Status status5 = taskManager.getEpic(epicId).getStatus();

        assertAll(
                () -> assertEquals(status, Status.NEW),
                () -> assertEquals(status1, Status.NEW),
                () -> assertEquals(status2, Status.NEW),
                () -> assertEquals(status3, Status.IN_PROGRESS),
                () -> assertEquals(status4, Status.DONE),
                () -> assertEquals(status5, Status.IN_PROGRESS)
        );

    }

    @Test
    void updateExecutionTimeEpicTest() {
        Integer epicId = taskManager.createEpic(new Epic("EpicStatusTest", "this EpicStatusTest"));
        Epic epic = taskManager.getEpic(epicId);
        final int duration1 = epic.getDuration();
        final LocalDateTime startTimeEpic1 = epic.getStartTime();
        final LocalDateTime endTimeEpic1 = epic.getEndTime();

        LocalDateTime startTime = LocalDateTime.parse("2222-12-31T01:01:01");
        Integer subId1 = taskManager.createSubtask(
                new Subtask(epicId, "sub1", "this sub1", 1, startTime));
        Integer subId2 = taskManager.createSubtask(
                new Subtask(epicId, "sub2", "this sub2", 11, startTime.plusMinutes(2)));
        Integer subId3 = taskManager.createSubtask(
                new Subtask(epicId, "sub3", "this sub3", 45, startTime.plusMinutes(2 + 12)));

        epic = taskManager.getEpic(epicId);
        final int duration2 = epic.getDuration();
        final LocalDateTime startTimeEpic2 = epic.getStartTime();
        final LocalDateTime endTimeEpic2 = epic.getEndTime();
        final Subtask subtask1 = taskManager.getSubtask(subId1);
        final Subtask subtask2 = taskManager.getSubtask(subId2);
        final Subtask subtask3 = taskManager.getSubtask(subId3);
        final LocalDateTime startTimeSubtask1 = subtask1.getStartTime();
        final LocalDateTime endTimesubtask3 = subtask3.getEndTime();
        int duration = subtask1.getDuration() + subtask2.getDuration() + subtask3.getDuration();

        assertAll(
                () -> assertEquals(duration1, 0),
                () -> assertNull(startTimeEpic1),
                () -> assertNull(endTimeEpic1),
                () -> assertEquals(duration2, duration),
                () -> assertEquals(startTimeSubtask1, startTimeEpic2),
                () -> assertEquals(endTimesubtask3, endTimeEpic2)
        );
    }

}