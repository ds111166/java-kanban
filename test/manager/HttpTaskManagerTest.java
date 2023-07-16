package manager;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import org.junit.jupiter.api.*;
import server.KVServer;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static KVServer server;

    @BeforeAll
    public static void createKVServerForTest() {
        try {
            server = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();
    }

    @AfterAll
    public static void stopKVServerForTest() {
        server.stop();
    }

    @BeforeEach
    @Override
    public void createManagerForTest() {
        taskManager = new HttpTaskManager("http://localhost:8078/");
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
    void loadFromServerTest() {
        HttpTaskManager httpTaskManager = new HttpTaskManager("http://localhost:8078/");
        httpTaskManager.load();

        List<Task> tasks = httpTaskManager.getTasks();
        final List<Integer> tasksIds = httpTaskManager.getTasks()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(tasksIds.size() == 3
                        & tasksIds.containsAll(Set.of(taskId1, taskId2, taskId3)),
                "после загрузки с сервера список Задач имеет не верный размер и состав");
        List<Subtask> subtasks = httpTaskManager.getSubtasks();
        final List<Integer> subtasksIds = httpTaskManager.getSubtasks()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(subtasksIds.size() == 6
                        & subtasksIds.containsAll(Set.of(subId11, subId12, subId21, subId22, subId23, subId31)),
                "после загрузки с сервера список Подзадач имеет не верный размер и состав");

        final List<Integer> epicsIds = httpTaskManager.getEpics()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(epicsIds.size() == 3
                        & epicsIds.containsAll(Set.of(epicId1, epicId2, epicId3)),
                "после загрузки с сервера список Эпиков имеет не верный размер и состав");

        final List<Integer> historyIds = taskManager.getHistory()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(historyIds.size() == 1
                & historyIds.contains(taskId3), "после загрузки с сервера список  Истории " +
                "имеет не верный размер и состав");

        clearManagerForTest();
        final List<Task> tasks1 = taskManager.getTasks();
        final List<Subtask> subtasks1 = taskManager.getSubtasks();
        final List<Epic> epics1 = taskManager.getEpics();
        final List<Task> history1 = taskManager.getHistory();
        assertTrue(tasks1.isEmpty()
                & subtasks1.isEmpty()
                & epics1.isEmpty()
                & history1.isEmpty(), "после удаления всех задач, эпиков, подзадач, " +
                "полученный соответствующие списки не пусты");

        HttpTaskManager httpTaskManager1 = new HttpTaskManager("http://localhost:8078/");
        httpTaskManager1.load();
        final List<Task> tasks2 = httpTaskManager1.getTasks();
        final List<Subtask> subtasks2 = httpTaskManager1.getSubtasks();
        final List<Epic> epics2 = httpTaskManager1.getEpics();
        final List<Task> history2 = taskManager.getHistory();
        assertTrue(tasks2.isEmpty()
                & subtasks2.isEmpty()
                & epics2.isEmpty()
                & history2.isEmpty(), "после создания менеджера и загрузки с сервера данных без задач, подзадач, эпиков и истории " +
                "полученные соответствующие списки не пусты");

        final Integer epId1 = httpTaskManager1.createEpic(new Epic("epic1", "this epic1"));
        final Integer epId2 = httpTaskManager1.createEpic(new Epic("epic2", "this epic2"));

        HttpTaskManager httpTaskManager2 = new HttpTaskManager("http://localhost:8078/");
        httpTaskManager2.load();
        final List<Integer> epicsIds3 = httpTaskManager2.getEpics()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(epicsIds3.containsAll(Set.of(epId1, epId2)), "после создания менеджера и загрузки с сервера" +
                "с двумя эпиками полученный список эпиков не верен по своему составу");
    }

}