package manager;

import entities.Epic;
import entities.Status;
import entities.Subtask;
import entities.Task;
import manager.exceptions.TaskValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Integer taskId1, taskId2, taskId3, epicId1, epicId2, epicId3;
    protected Integer subId11, subId12, subId21, subId22, subId23, subId31;

    @BeforeEach
    protected abstract void createManagerForTest();

    @AfterEach
    protected abstract void clearManagerForTest();

    @Test
    void getTasksTest() {
        final Set<Integer> ids = taskManager.getTasks()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
        assertTrue(ids.containsAll(Set.of(taskId1, taskId2, taskId3)), "Получен не верный список задач");
    }

    @Test
    void deleteTasksTest() {
        final Set<Integer> ids = taskManager.getTasks()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
        assertEquals(ids.isEmpty(), false, "Получен пустой список идентификаторов задач в списке");
        taskManager.deleteTasks();
        final Set<Integer> ids1 = taskManager.getTasks()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
        assertEquals(ids1.isEmpty(), true, "Получен не пустой список идентификаторов задач в списке");
    }


    @Test
    void getTaskTest() {
        final Task task = taskManager.getTask(-656565);
        assertNull(task, "Получена на пустая задача по не верному идентификатору");
        final Task task2 = taskManager.getTask(taskId2);
        assertNotNull(task2,"Получена пустая задача по верному идентификатору");
        assertEquals("task2", task2.getName(), "Имя задачи полученной по верному идентификатору не ВЕРНО!");
    }

    @Test
    void createTaskTest() {
        final Integer taskNullId = taskManager.createTask(null);
        assertNull(taskNullId, "При создании пустой задачи получен не пустой идентификатор");

        LocalDateTime startTime = LocalDateTime.parse("2001-10-02T20:22:02");
        final Integer taskId = taskManager.createTask(
                new Task("createTaskTest", "this create Task Test", 11, startTime));
        assertNotNull(taskId, "при создании задачи получен пустой идентификатор");
        final TaskValidationException exception1 = assertThrows(
                TaskValidationException.class,
                () -> {
                    final Integer taskIdexception = taskManager.createTask(
                            new Task("createTaskTest1", "this create Task Test1", 11, startTime));
                });
        assertEquals("Задача пересекается с id=13 c 2001-10-02T20:22 по 2001-10-02T20:33", exception1.getMessage(),
                "неверное сообщение исклчюния");

        final Task task = taskManager.getTask(taskId);

        LocalDateTime startTime1 = startTime.plusMinutes(2);

        final TaskValidationException exception2 = assertThrows(
                TaskValidationException.class,
                () -> {
                    final Integer taskIdexception = taskManager.createTask(
                            new Task("createTaskTest2", "this create Task Test2", 11, startTime1));
                });
        assertEquals("Задача пересекается с id=13 c 2001-10-02T20:22 по 2001-10-02T20:33", exception2.getMessage(),
                "неверное сообщение исклчюния");

        LocalDateTime startTime2 = startTime.plusMinutes(11);
        final TaskValidationException exception3 = assertThrows(
                TaskValidationException.class,
                () -> {
                    final Integer taskIdexception = taskManager.createTask(
                            new Task("createTaskTest2", "this create Task Test2", 11, startTime2));
                });
        assertEquals("Задача пересекается с id=13 c 2001-10-02T20:22 по 2001-10-02T20:33", exception3.getMessage(),
                "неверное сообщение исклчюния");

        LocalDateTime startTime4 = startTime.plusMinutes(32);
        final Integer taskId4 = taskManager.createTask(
                new Task("createTaskTest4", "this create Task Test4", 11, startTime4));
        assertNotNull(taskId4, "при создании задачи получен пустой идентификатор");

        LocalDateTime startTime3 = startTime.plusMinutes(28);
        final TaskValidationException exception4 = assertThrows(
                TaskValidationException.class,
                () -> {
                    final Integer taskIdexception = taskManager.createTask(
                            new Task("createTaskTest2", "this create Task Test2", 11, startTime3));
                });
        assertEquals("Задача пересекается с id=14 c 2001-10-02T20:54 по 2001-10-02T21:05", exception4.getMessage(),
                "неверное сообщение исклчюния");

    }

    @Test
    void updateTaskTest() {
        final Task task = taskManager.getTask(taskId1);
        final Status status = task.getStatus();
        assertNull(status, "получен не пустой статус");

        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        final Task task1 = taskManager.getTask(taskId1);
        final Status status1 = task1.getStatus();
        assertEquals(Status.DONE, status1, "получен статус не равный: DONE");
    }

    @Test
    void deleteTaskTest() {
        final Task task = taskManager.getTask(taskId1);
        assertNotNull(task,"Полученв пустая задача по верному идентификатору");
        taskManager.deleteTask(taskId1);
        final Task task2 = taskManager.getTask(taskId1);
        assertNull(task2, "По идентификатору удаленной задачи получена НЕ ПУСТАЯ задача");
    }

    @Test
    void getSubtasksTest() {
        final Set<Integer> ids = taskManager.getSubtasks()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
        assertTrue(ids.containsAll(Set.of(subId11, subId12, subId21, subId22, subId23, subId31)),
                "Получен не верный список подзадач");
    }

    @Test
    void deleteSubtasksTest() {
        final Set<Integer> ids = taskManager.getSubtasks()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
        assertFalse(ids.isEmpty(),"Получен пустой список подзадач, что не верно");
        taskManager.deleteSubtasks();
        final Set<Integer> ids1 = taskManager.getSubtasks()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
        assertTrue(ids1.isEmpty(),"После удаления всех подзач, получен НЕ ПУСТОЙ списк подзадач");
    }


    @Test
    void getSubtaskTest() {
        final Subtask subtask = taskManager.getSubtask(-6568865);
        assertNull(subtask, "Получена не пустая подзадача по заведомо не верному идентификатору!");
        final Subtask subtask2 = taskManager.getSubtask(subId22);
        assertNotNull(subtask2, "Получена пустая подзадача по заведомо верному идентификатору");
        assertEquals("sud22", subtask2.getName(), "Имя подзадачи полученной по верному идентификатору не ВЕРНО!");
    }

    @Test
    void createSubtaskTest() {
        final Integer subtaskNullId = taskManager.createSubtask(null);
        assertNull(subtaskNullId, "При создании пустой подзадачи получен не пустой идентификатор");
        Integer subId32 = taskManager.createSubtask(
                new Subtask(epicId3, "sud32", "this sub32"));
        assertNotNull(subId32, "Получен пустой идентификатр верно созданной подзадачи");
        final Subtask subtask = taskManager.getSubtask(subId32);
        final Integer id = subtask.getId();
        assertEquals(subId32, id, "Идентификатор полученной задачи не равен идентификатору," +
                " по которому эту подзадачу получили");
        final List<Integer> subtaskIds = taskManager.getEpic(epicId3).getSubtaskIds();
        final boolean contains = subtaskIds.contains(id);
        assertTrue(contains, "В полученом списке идентификаторов подзадач отсутсвует " +
                "заведомо верный идентификатор созданной ранее подзадачи");
        Integer subId = taskManager.createSubtask(
                new Subtask(569, "sud", "this sub"));
        assertNull(subId, "Создана не пустая подзадача с заведомо не верным идентификатором эпика");
    }

    @Test
    void updateSubtaskTest() {
        final Subtask subtask = taskManager.getSubtask(subId31);
        final Integer id = subtask.getId();
        final Status status = subtask.getStatus();
        assertNull(status, "Статус подзадачи с неустановленным статусо не равен NULL!");
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        final Subtask subtask1 = taskManager.getSubtask(subId31);
        final Integer id1 = subtask1.getId();
        assertEquals(id, id1, "Идентификаторы подзадачи до и после обновления не рамны");
        final Status status1 = subtask1.getStatus();
        assertEquals(status1, Status.IN_PROGRESS, "Статус полученной подзадачи с ранее установленным" +
                " статусом \"IN_PROGRESS\" не равен IN_PROGRESS");
    }

    @Test
    void deleteSubtaskTest() {
        final Subtask subtask = taskManager.getSubtask(subId21);
        assertNotNull(subtask, "По верному идентификатору получена пустая подзадача");

        final int epicId = subtask.getEpicId();
        final List<Integer> subtaskIds = taskManager.getEpic(epicId).getSubtaskIds();
        final boolean contains = subtaskIds.contains(subId21);
        assertTrue(contains, "Идентификатор существующей подзадачи отсутствует в в полученном списке подзадач");

        taskManager.deleteSubtask(subId21);
        final Subtask subtask2 = taskManager.getSubtask(subId21);
        assertNull(subtask2, "По идентификатору ранее удаленной подзадачи получена не пустая подзадача");

        final List<Integer> subtaskIds1 = taskManager.getEpic(epicId).getSubtaskIds();
        final boolean contains1 = subtaskIds1.contains(subId21);
        assertFalse(contains1, "Идентификатор ранеее удаленной подзадачи входит в полученный список подзадач");
    }

    @Test
    void getEpicsTest() {
        final Set<Integer> ids = taskManager.getEpics()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
        assertTrue(ids.containsAll(Set.of(epicId1, epicId2, epicId3)), "Получен не верный список эпиков");
    }

    @Test
    void deleteEpicsTest() {
        final Set<Integer> ids = taskManager.getEpics()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
        taskManager.deleteEpics();
        final Set<Integer> ids1 = taskManager.getEpics()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
        assertAll(
                () -> assertFalse(ids.isEmpty()),
                () -> assertTrue(ids1.isEmpty())
        );
    }

    @Test
    void getEpicTest() {
        final Epic epic = taskManager.getEpic(100);
        final Epic epic2 = taskManager.getEpic(epicId2);
        assertAll(
                () -> assertNull(epic),
                () -> assertNotNull(epic2),
                () -> assertEquals("epic2", epic2.getName())
        );
    }

    @Test
    void createEpicTest() {
        final Integer epicNullId = taskManager.createEpic(null);
        final Integer epicId = taskManager.createEpic(new Epic("epic", "this epic"));
        final Epic epic = taskManager.getEpic(epicId);
        final Integer id = epic.getId();
        assertAll(
                () -> assertNull(epicNullId),
                () -> assertNotNull(epic),
                () -> assertNotNull(epicId),
                () -> assertEquals(epicId, id)
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
                () -> assertNotNull(updateEpic),
                () -> assertNotNull(epic),
                () -> assertEquals(epic.getId(), updateEpic.getId()),
                () -> assertEquals(description, "this epic"),
                () -> assertEquals(updateEpic.getDescription(), "this update epic")
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
                () -> assertNotNull(epic),
                () -> assertNotNull(subtask21),
                () -> assertNotNull(subtask22),
                () -> assertNull(epic1),
                () -> assertNull(subtask21d),
                () -> assertNull(subtask22d),
                () -> assertTrue(contains),
                () -> assertFalse(contains1)
        );

    }

    @Test
    void getEpicSubtasksTest() {
        final Epic epic = taskManager.getEpic(epicId2);
        final List<Integer> subtaskIds = epic.getSubtaskIds();
        final List<Integer> subtaskIds1 = taskManager.getEpicSubtasks(epicId2)
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList());

        assertAll(
                () -> assertEquals(subtaskIds.size(), subtaskIds1.size()),
                () -> assertTrue(subtaskIds.containsAll(subtaskIds1)),
                () -> assertNotNull(epic),
                () -> assertEquals(3, subtaskIds.size()),
                () -> assertTrue(subtaskIds.containsAll(Set.of(subId21, subId22, subId23)))
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
                () -> assertArrayEquals(historyIds.toArray(new Integer[0]), new Integer[]{taskId3}),
                () -> assertArrayEquals(historyIds1.toArray(new Integer[0]),
                        new Integer[]{taskId3, taskId1, taskId2, epicId2, epicId1}),
                () -> assertArrayEquals(historyIds2.toArray(new Integer[0]),
                        new Integer[]{taskId3, taskId1, taskId2, epicId2}),
                () -> assertArrayEquals(historyIds3.toArray(new Integer[0]), new Integer[]{taskId1, taskId2, epicId2}),
                () -> assertArrayEquals(historyIds4.toArray(new Integer[0]), new Integer[]{taskId1, epicId2}),
                () -> assertTrue(history.isEmpty())
        );

    }

    @Test
    void getPrioritizedTasksTest() {

        List<Integer> prioritizedTasksIds = taskManager.getPrioritizedTasks().stream().map(Task::getId).collect(Collectors.toList());
        String stringIds = Arrays.toString(prioritizedTasksIds.toArray(new Integer[0]));
        taskManager.deleteTask(taskId1);
        taskManager.deleteTask(taskId3);
        taskManager.getPrioritizedTasks();
        List<Integer> prioritizedTasksIds1 = taskManager.getPrioritizedTasks().stream().map(Task::getId).collect(Collectors.toList());
        String stringIds1 = Arrays.toString(prioritizedTasksIds1.toArray(new Integer[0]));
        String str = "[1, 2, 3, 4, 7, 8, 5, 9, 10, 11, 6, 12]";
        String str1 = "[2, 4, 7, 8, 5, 9, 10, 11, 6, 12]";
        assertAll(
                () -> assertEquals(stringIds, str),
                () -> assertEquals(stringIds1, str1)
        );

    }

    protected void createTasksTest() {
        LocalDateTime startTime = LocalDateTime.parse("2001-10-02T10:11:01");
        taskId1 = taskManager.createTask(
                new Task("task1", "this task1", 12, startTime));
        startTime = startTime.plusMinutes(13);
        taskId2 = taskManager.createTask(
                new Task("task2", "this task2", 11, startTime));
        startTime = startTime.plusMinutes(12);
        taskId3 = taskManager.createTask(
                new Task("task3", "this task3", 11, startTime));
        startTime = startTime.plusMinutes(12);

        epicId1 = taskManager.createEpic(new Epic("epic1", "this epic1"));
        epicId2 = taskManager.createEpic(new Epic("epic2", "this epic2"));
        epicId3 = taskManager.createEpic(new Epic("epic3", "this epic3"));
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
}
