package manager;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import manager.exceptions.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    final static File FILE = new File("./resources/task.csv");

    @BeforeEach
    @Override
    public void createManagerForTest() {
        taskManager = new FileBackedTasksManager(FILE);
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
    void loadFromFileTest() {

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(FILE);
        List<Task> tasks = fileBackedTasksManager.getTasks();
        final List<Integer> tasksIds = fileBackedTasksManager.getTasks()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(tasksIds.size() == 3
                        & tasksIds.containsAll(Set.of(taskId1, taskId2, taskId3)),
                "после загрузки из файла список Задач имеет не верный размер и состав");

        final List<Integer> subtasksIds = fileBackedTasksManager.getSubtasks()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(subtasksIds.size() == 6
                        & subtasksIds.containsAll(Set.of(subId11, subId12, subId21, subId22, subId23, subId31)),
                "после загрузки из файла список Подзадач имеет не верный размер и состав");

        final List<Integer> epicsIds = fileBackedTasksManager.getEpics()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(epicsIds.size() == 3
                        & epicsIds.containsAll(Set.of(epicId1, epicId2, epicId3)),
                "после загрузки из файла список Эпиков имеет не верный размер и состав");

        final List<Integer> historyIds = taskManager.getHistory()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(historyIds.size() == 1
                & historyIds.contains(taskId3), "после загрузки из файла список  Истории " +
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

        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(FILE);
        final List<Task> tasks2 = fileBackedTasksManager1.getTasks();
        final List<Subtask> subtasks2 = fileBackedTasksManager1.getSubtasks();
        final List<Epic> epics2 = fileBackedTasksManager1.getEpics();
        final List<Task> history2 = taskManager.getHistory();
        assertTrue(tasks2.isEmpty()
                & subtasks2.isEmpty()
                & epics2.isEmpty()
                & history2.isEmpty(), "после создания менеджера загрузки из файла данных без задач, подзадач, эпиков и истории " +
                "полученные соответствующие списки не пусты");

        final Integer epId1 = fileBackedTasksManager1.createEpic(new Epic("epic1", "this epic1"));
        final Integer epId2 = fileBackedTasksManager1.createEpic(new Epic("epic2", "this epic2"));
        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(FILE);
        final List<Integer> epicsIds3 = fileBackedTasksManager2.getEpics()
                .stream().map(Task::getId).collect(Collectors.toList());
        assertTrue(epicsIds3.containsAll(Set.of(epId1, epId2)), "после создания менеджера загрузки из файла" +
                "с двумя эпиками полученный список эпиков не верен по своему составу");

        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                () -> FileBackedTasksManager
                        .loadFromFile(new File("./resources/task2222.csv")));
        assertEquals("Can't read from file: task2222.csv", exception.getMessage(),
                "при попытке создания менеджера загрузки из несуществующего файла данных" +
                        " не вызвано исключение с верным сообщением об ошибке");

    }


}