package manager;


import entities.Epic;
import entities.Status;
import entities.Subtask;
import entities.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    protected TaskManager taskManager;
    protected Integer  taskId1, taskId2, taskId3, epicId1, epicId2, epicId3;
    protected Integer subId11, subId12, subId21, subId22, subId23,  subId31;
    @BeforeEach
    protected void createManagerForTest() {
        taskManager = new InMemoryTaskManager();
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        createTasksTest();
    }

    protected void createTasksTest() {
        LocalDateTime startTime = LocalDateTime.parse("2001-10-02T10:11:01");
        taskId1 = taskManager.createTask(
                new Task("task1","this task1", 12, startTime));
        startTime = startTime.plusMinutes(13);
        taskId2 = taskManager.createTask(
                new Task("task2","this task2", 11, startTime));
        startTime = startTime.plusMinutes(12);
        taskId3 = taskManager.createTask(
                new Task("task3","this task3", 11, startTime));
        startTime = startTime.plusMinutes(12);

        epicId1 = taskManager.createEpic(new Epic("epic1","this epic1"));
        epicId2 = taskManager.createEpic(new Epic("epic2","this epic2"));
        epicId3 = taskManager.createEpic(new Epic("epic3","this epic3"));
        taskManager.getTask(taskId3).getEndTime();

        subId11 = taskManager.createSubtask(
                new Subtask(epicId1, "sud11", "this sub11", 5, startTime));
        startTime = startTime.plusMinutes(6);
        subId12 = taskManager.createSubtask(
                new Subtask(epicId1, "sud12", "this sub12", 4, startTime));

        startTime = startTime.plusMinutes(5);
        subId21 = taskManager.createSubtask(
                new Subtask(epicId2, "sud21", "this sub21", 10, startTime));
        startTime = startTime.plusMinutes(11);
        subId22 = taskManager.createSubtask(
                new Subtask(epicId2, "sud22", "this sub22", 12, startTime));
        startTime = startTime.plusMinutes(13);
        subId23 = taskManager.createSubtask(
                new Subtask(epicId2, "sud23", "this sub23", 12, startTime));
        startTime = startTime.plusMinutes(13);

        subId31 = taskManager.createSubtask(
                new Subtask(epicId3, "sud31", "this sub31"));
    }

    @AfterEach
    protected void clearManagerForTest() {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
    }
    @Test
    void getTasksTest() {
        final Set<Integer> ids = taskManager.getTasks()
                .stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
        assertTrue(ids.containsAll(Set.of(taskId1, taskId2, taskId3)));
    }

    @Test
    void deleteTasksTest() {
        final Set<Integer> ids = taskManager.getTasks()
                .stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
        taskManager.deleteTasks();
        final Set<Integer> ids1 = taskManager.getTasks()
                .stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
        assertAll(
                ()->assertFalse(ids.isEmpty()),
                ()->assertTrue(ids1.isEmpty())
        );
    }

    @Test
    void getTaskTest() {
        final Task task = taskManager.getTask(656565);
        final Task task2 = taskManager.getTask(taskId2);
        assertAll(
                ()->assertNull(task),
                ()->assertNotNull(task2),
                ()->assertEquals("task2", task2.getName())
        );
    }

    @Test
    void createTaskTest() {
        LocalDateTime startTime = LocalDateTime.parse("2001-10-02T20:22:02");
        final Integer taskId = taskManager.createTask(
                new Task("createTaskTest", "this create Task Test", 11, startTime));
        final Integer taskId1 = taskManager.createTask(
                new Task("createTaskTest1", "this create Task Test1", 11, startTime));
        final Task task = taskManager.getTask(taskId);

        LocalDateTime startTime1 = startTime.plusMinutes(2);
        final Integer taskId2 = taskManager.createTask(
                new Task("createTaskTest2", "this create Task Test2", 11, startTime1));
        LocalDateTime startTime2 = startTime.plusMinutes(11);
        final Integer taskId3 = taskManager.createTask(
                new Task("createTaskTest2", "this create Task Test2", 11, startTime2));
        LocalDateTime startTime3 = startTime.plusMinutes(12);
        final Integer taskId4 = taskManager.createTask(
                new Task("createTaskTest2", "this create Task Test2", 11, startTime3));
        assertAll(
                ()->assertNotNull(taskId),
                ()->assertNotNull(task),
                ()->assertEquals("createTaskTest", task.getName()),
                ()->assertEquals(taskId, task.getId()),
                ()->assertNull(taskId1),
                ()->assertNull(taskId2),
                ()->assertNull(taskId3),
                ()->assertNotNull(taskId4)
        );
    }

    @Test
    void updateTaskTest() {
        final Task task = taskManager.getTask(taskId1);
        final Status status = task.getStatus();
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        final Task task1 = taskManager.getTask(taskId1);
        final Status status1 = task1.getStatus();
        assertAll(
                ()->assertNull(status),
                ()->assertEquals(Status.DONE, status1)
        );
    }

    @Test
    void deleteTaskTest() {
        final Task task = taskManager.getTask(taskId1);
        taskManager.deleteTask(taskId1);
        final Task task2 = taskManager.getTask(taskId1);
        assertAll(
                ()->assertNull(task2),
                ()->assertNotNull(task)
        );
    }

    @Test
    void getSubtasksTest() {
        final Set<Integer> ids = taskManager.getSubtasks()
                .stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
        assertTrue(ids.containsAll(Set.of(subId11, subId12, subId21, subId22, subId23,  subId31)));
    }

    @Test
    void deleteSubtasksTest() {
        final Set<Integer> ids = taskManager.getSubtasks()
                .stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
        taskManager.deleteSubtasks();
        final Set<Integer> ids1 = taskManager.getSubtasks()
                .stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
        assertAll(
                ()->assertFalse(ids.isEmpty()),
                ()->assertTrue(ids1.isEmpty())
        );
    }

    @Test
    void getSubtaskTest() {
        final Subtask subtask = taskManager.getSubtask(6568865);
        final Subtask subtask2 = taskManager.getSubtask(subId22);
        assertAll(
                ()->assertNull(subtask),
                ()->assertNotNull(subtask2),
                ()->assertEquals("sud22", subtask2.getName())
        );
    }

    @Test
    void createSubtaskTest() {
        Integer subId32 = taskManager.createSubtask(
                new Subtask(epicId3, "sud32", "this sub32"));
        final Subtask subtask = taskManager.getSubtask(subId32);
        final Integer id = subtask.getId();
        final List<Integer> subtaskIds = taskManager.getEpic(epicId3).getSubtaskIds();
        final boolean contains = subtaskIds.contains(id);
        Integer subId = taskManager.createSubtask(
                new Subtask(569, "sud", "this sub"));
        assertAll(
                ()->assertNull(subId),
                ()->assertNotNull(subId32),
                ()->assertEquals(subId32, id),
                ()->assertTrue(contains)
        );

    }

    @Test
    void updateSubtaskTest() {
        final Subtask subtask = taskManager.getSubtask(subId31);
        final Integer id = subtask.getId();
        final Status status = subtask.getStatus();
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        final Subtask subtask1 = taskManager.getSubtask(subId31);
        final Integer id1 = subtask1.getId();
        final Status status1 = subtask1.getStatus();
        assertAll(
                ()->assertNull(status),
                ()->assertEquals(id, id1),
                ()->assertEquals(subtask1, Status.IN_PROGRESS)
        );
    }

    @Test
    void deleteSubtaskTest() {
        final Subtask subtask = taskManager.getSubtask(subId21);
        final int epicId = subtask.getEpicId();

        final List<Integer> subtaskIds = taskManager.getEpic(epicId).getSubtaskIds();
        final boolean contains = subtaskIds.contains(subId21);

        taskManager.deleteSubtask(subId21);
        final Subtask subtask2 = taskManager.getSubtask(subId21);
        final List<Integer> subtaskIds1 = taskManager.getEpic(epicId).getSubtaskIds();
        final boolean contains1 = subtaskIds1.contains(subId21);
        assertAll(
                ()->assertNull(subtask2),
                ()->assertNotNull(subtask),
                ()->assertTrue(contains),
                ()->assertFalse(contains1)
        );
    }

    @Test
    void getEpicsTest() {
        final Set<Integer> ids = taskManager.getEpics()
                .stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
        assertTrue(ids.containsAll(Set.of(epicId1, epicId2, epicId3)));
    }

    @Test
    void deleteEpicsTest() {
        final Set<Integer> ids = taskManager.getEpics()
                .stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
        taskManager.deleteEpics();
        final Set<Integer> ids1 = taskManager.getEpics()
                .stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
        assertAll(
                ()->assertFalse(ids.isEmpty()),
                ()->assertTrue(ids1.isEmpty())
        );
    }

    @Test
    void getEpicTest() {
        final Epic epic = taskManager.getEpic(100);
        final Epic epic2 = taskManager.getEpic(epicId2);
        assertAll(
                ()->assertNull(epic),
                ()->assertNotNull(epic2),
                ()->assertEquals("epic2", epic2.getName())
        );
    }

    @Test
    void createEpicTest() {
        final Integer epicId = taskManager.createEpic(new Epic("epic", "this epic"));
        final Epic epic = taskManager.getEpic(epicId);
        final Integer id = epic.getId();
        assertAll(
                ()->assertNotNull(epic),
                ()->assertNotNull(epicId),
                ()->assertEquals(epicId, id)
        );
    }

    @Test
    void updateEpicTest() {
        final Integer epicId = taskManager.createEpic(new Epic("epic", "this epic"));
        final Epic epic = taskManager.getEpic(epicId);
        final String description = epic.getDescription();
        epic.setDescription("this update epic");
        taskManager.updateEpic(epic);
        final Epic updateEpic = taskManager.getEpic(epicId);
        assertAll(
                ()->assertNotNull(updateEpic),
                ()->assertNotNull(epic),
                ()->assertEquals(epic.getId(), updateEpic.getId()),
                ()->assertEquals(description, "this epic"),
                ()->assertEquals(updateEpic.getDescription(), "this update epic")
        );

    }

    @Test
    void deleteEpicTest() {
        final Epic epic = taskManager.getEpic(epicId2);
        final Subtask subtask21 = taskManager.getSubtask(subId21);
        final Subtask subtask22 = taskManager.getSubtask(subId22);
        final Set<Integer> historyIds = taskManager.getHistory().
                stream().map(Task::getId).collect(Collectors.toSet());
        final boolean contains = historyIds.contains(epicId2);
        taskManager.deleteEpic(epicId2);
        final Epic epic1 = taskManager.getEpic(epicId2);
        final Subtask subtask21d = taskManager.getSubtask(subId21);
        final Subtask subtask22d = taskManager.getSubtask(subId22);
        final Set<Integer> historyIds1 = taskManager.getHistory()
                .stream().map(Task::getId).collect(Collectors.toSet());
        final boolean contains1 = historyIds1.contains(epicId2);
        assertAll(
                ()->assertNotNull(epic),
                ()->assertNotNull(subtask21),
                ()->assertNotNull(subtask22),
                ()->assertNull(epic1),
                ()->assertNull(subtask21d),
                ()->assertNull(subtask22d),
                ()->assertTrue(contains),
                ()->assertFalse(contains1)
        );

    }

    @Test
    void getEpicSubtasksTest() {
        final Epic epic = taskManager.getEpic(epicId2);
        final List<Integer> subtaskIds = epic.getSubtaskIds();
        assertAll(
                ()->assertNotNull(epic),
                ()->assertTrue(subtaskIds.size() == 3),
                ()->assertTrue(subtaskIds.containsAll(Set.of(subId21, subId22, subId23)))
        );
    }

    @Test
    void getHistoryTest() {
        final List<Integer> historyIds = taskManager.getHistory()
                .stream().map(Task::getId).collect(Collectors.toList());

        taskManager.getTask(taskId1);
        taskManager.getTask(taskId2);
        taskManager.getTask(taskId2);
        taskManager.getTask(epicId2);
        taskManager.getTask(epicId1);
        final List<Integer> historyIds1 = taskManager.getHistory()
                .stream().map(Task::getId).collect(Collectors.toList());

        taskManager.deleteEpic(epicId1);
        final List<Integer> historyIds2 = taskManager.getHistory()
                .stream().map(Task::getId).collect(Collectors.toList());

        taskManager.deleteTask(taskId3);
        final List<Integer> historyIds3 = taskManager.getHistory()
                .stream().map(Task::getId).collect(Collectors.toList());

        taskManager.deleteTask(taskId2);
        final List<Integer> historyIds4 = taskManager.getHistory()
                .stream().map(Task::getId).collect(Collectors.toList());

        taskManager.deleteTask(taskId1);
        taskManager.deleteEpic(epicId2);
        final List<Task> history = taskManager.getHistory();

        assertAll(
                ()->assertArrayEquals(historyIds.toArray(new Integer[0]), new Integer[]{taskId3}),
                ()->assertArrayEquals(historyIds1.toArray(new Integer[0]),
                        new Integer[]{taskId3,taskId1,taskId2,epicId2,epicId1}),
                ()->assertArrayEquals(historyIds2.toArray(new Integer[0]),
                        new Integer[]{taskId3,taskId1,taskId2,epicId2}),
                ()->assertArrayEquals(historyIds3.toArray(new Integer[0]), new Integer[]{taskId1,taskId2,epicId2}),
                ()->assertArrayEquals(historyIds4.toArray(new Integer[0]), new Integer[]{taskId1,epicId2}),
                ()->assertTrue(history.isEmpty())
        );

    }

    @Test
    void getPrioritizedTasksTest() {
        final List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        taskManager.deleteTask(taskId1);
        final List<Task> prioritizedTasks1 = taskManager.getPrioritizedTasks();
        ass
    }

    @Test
    void updateEpicStatusTest() {
    }

    @Test
    void updateExecutionTimeEpicTest() {
    }

    @Test
    void isTaskTimingValidTest() {
    }

    @Test
    void fillingIntervalTest() {
    }

    @Test
    void clearingIntervalTest() {
    }
    @Test
    void StatusEpicTest(){


    }
}