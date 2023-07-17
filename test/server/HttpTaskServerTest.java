package server;

import com.google.gson.*;
import entities.*;
import manager.*;
import manager.utilities.LocalDateTimeAdapter;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class HttpTaskServerTest<T extends FileBackedTasksManager> {
    private static final String ADDR = "http://localhost:8080";

    private FileBackedTasksManager taskManager;
    private Integer taskId1, taskId2, taskId3, epicId1, epicId2, epicId3;
    private Integer subId11, subId12, subId21, subId22, subId23, subId31;

    private  HttpTaskServer server;
    private  HttpClient client;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    @BeforeEach
    public void createServerForTest() {
        taskManager = new FileBackedTasksManager();
        createTasks();

        try {
            server = new HttpTaskServer(taskManager);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client = HttpClient.newHttpClient();

    }
    @AfterEach
    public void clearManagerForTest() {
        clearManager();
        server.stop();
    }
    @Test
    void tasksEndpointTest () throws IOException, InterruptedException {
        URI uri = URI.create(ADDR+"/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString("rewrwe"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode(), "Неверный статус обработки POST запроса: " + uri.getPath());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode(), "Неверный статус обработки DELETE запроса: " + uri.getPath());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString("qeqwe"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode(), "Неверный статус обработки PUT запроса: " + uri.getPath());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: " + uri.getPath());
        final List<Task> tasks = parseTasks(response.body());
        final List<Integer> tasksIds = tasks.stream()
                .filter(task->task.getType()==TaskType.TASK)
                .map(Task::getId)
                .collect(Collectors.toList());
        final List<Integer> epicsIds = tasks.stream()
                .filter(task->task.getType()==TaskType.EPIC)
                .map(Task::getId)
                .collect(Collectors.toList());
        final List<Integer> subtasksIds = tasks.stream()
                .filter(task->task.getType()==TaskType.SUBTASK)
                .map(Task::getId)
                .collect(Collectors.toList());
        assertTrue(tasksIds.size() == 3
                        & tasksIds.containsAll(Set.of(taskId1, taskId2, taskId3)),
                "После загрузки с HttpServer'а список Задач имеет не верный размер и состав");
        assertTrue(epicsIds.size() == 3
                        & epicsIds.containsAll(Set.of(epicId1, epicId2, epicId3)),
                "после загрузки с HttpServer'а список Эпиков имеет не верный размер и состав");
        assertTrue(subtasksIds.size() == 6
                        & subtasksIds.containsAll(Set.of(subId11, subId12, subId21, subId22, subId23, subId31)),
                "после загрузки с HttpServer'а список Подзадач имеет не верный размер и состав");

        clearManager();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        final List<Task> tasks1 = parseTasks(response.body());
        assertTrue(tasks1.isEmpty(), "после удаления всех задач полученный список задач не пуст");
    }

    @Test
    void taskEndpointTest() throws IOException, InterruptedException {
        URI uri = URI.create(ADDR+"/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString("qeqwe"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode(), "Неверный статус обработки PUT запроса: " + uri.getPath());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: " + uri.getPath());
        final List<Task> tasks = parseTasks(response.body());
        final List<Integer> tasksIds = tasks.stream()
                .filter(task->task.getType()==TaskType.TASK)
                .map(Task::getId)
                .collect(Collectors.toList());
        assertTrue(tasksIds.size() == 3
                        & tasksIds.containsAll(Set.of(taskId1, taskId2, taskId3)),
                "После загрузки с HttpServer'а список Задач имеет не верный размер и состав");

        uri = URI.create(ADDR+uri.getPath()+"?id="+taskId2);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: "
                + uri.getPath());
        final Task task = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getId(), taskId2, "Получена задача с неверным идентификатором " + task.getId() );
        uri = URI.create(ADDR+uri.getPath()+"?id="+1000);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode(),
                "Не верный статус ответа при запросе задачи по неверному идентификатору: "
                        + response.statusCode());

        //Создание задач
        Task newTask = new Task("task1", "this task1", 12,
                LocalDateTime.parse("2222-10-02T10:11:01"));
        uri = URI.create(ADDR+"/tasks/task/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode(),
                "Ошибка при обработки запроса на создание задачи: "
                + response.statusCode());
        final Integer newTaskId = gson.fromJson(response.body(), Integer.class);
        assertNotNull(newTaskId,
                "При создании задачи возвращен пустой идентификатор");

        uri = URI.create(ADDR+uri.getPath()+"?id="+newTaskId);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Ошибка при обработки GET запроса созданной задачм: "
                + uri.getPath());
        Task newTask1 = gson.fromJson(response.body(), Task.class);

        assertEquals(newTask1.getId(), newTaskId, "Получена созданная задача с неверным идентификатором "
                + task.getId() );
        //Обновление задач
        final Status status = newTask1.getStatus();
        newTask1.setStatus(Status.DONE);
        uri = URI.create(ADDR+"/tasks/task/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Ошибка при обработки запроса на обновление задачи: "
                        + response.statusCode());
        uri = URI.create(ADDR+uri.getPath()+"?id="+newTaskId);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Ошибка при обработки GET запроса созданной задачм: "
                + uri.getPath());
        Task newTask2 = gson.fromJson(response.body(), Task.class);
        assertEquals(newTask2.getStatus(), Status.DONE, "Возвращена обновленная задача с неверным статусом: "
                +newTask2.getStatus());
        //Удаление задач
        uri = URI.create(ADDR+uri.getPath()+"?id="+taskId2);
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при удалении задачи c id: "
                + taskId2);
        uri = URI.create(ADDR+uri.getPath()+"?id="+taskId2);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode(), "Ошибка при получении удаленной задачи: "
                + response.statusCode());
        uri = URI.create(ADDR+uri.getPath());
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "не верный код возврата при удалении всех задач: "
                + response.statusCode());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: " + uri.getPath());
        final List<Task> delTasks = parseTasks(response.body());
        assertEquals(delTasks.size(), 0, "После удаления всех задач получен не пустой список задач" );
    }
    @Test
    void epicEndpointTest() throws IOException, InterruptedException {
        URI uri = URI.create(ADDR+"/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString("qeqwe"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode(), "Неверный статус обработки PUT запроса: " + uri.getPath());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: " + uri.getPath());
        final List<Task> epics = parseTasks(response.body());
        final List<Integer> epicsIds = epics.stream()
                .filter(task->task.getType()==TaskType.EPIC)
                .map(Task::getId)
                .collect(Collectors.toList());
        assertTrue(epicsIds.size() == 3
                        & epicsIds.containsAll(Set.of(epicId1, epicId2, epicId3)),
                "После загрузки с HttpServer'а список Эпиков имеет не верный размер и состав");

        uri = URI.create(ADDR+uri.getPath()+"?id="+epicId2);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: "
                + uri.getPath());
        final Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic.getId(), epicId2, "Получен эпик с неверным идентификатором " + epic.getId() );
        uri = URI.create(ADDR+uri.getPath()+"?id="+1000);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode(),
                "Не верный статус ответа при запросе эпика по неверному идентификатору: "
                        + response.statusCode());

        //Создание эпика
        Epic newEpic = new Epic("epic33", "this epic33");
        uri = URI.create(ADDR+"/tasks/epic/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newEpic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode(),
                "Ошибка при обработки запроса на создание эпика: "
                        + response.statusCode());
        final Integer newEpicId = gson.fromJson(response.body(), Integer.class);
        assertNotNull(newEpicId,
                "При создании эпика возвращен пустой идентификатор");

        uri = URI.create(ADDR+uri.getPath()+"?id="+newEpicId);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Ошибка при обработки GET запроса созданного эпика: "
                        + uri.getPath());
        Epic newEpic1 = gson.fromJson(response.body(), Epic.class);

        assertEquals(newEpic1.getId(), newEpicId, "Получена созданный эпик с неверным идентификатором "
                + newEpic1.getId() );
        //Обновление эпика
        final String name = newEpic1.getName();
        newEpic1.setName(name + " new !");
        uri = URI.create(ADDR+"/tasks/epic/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newEpic1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Ошибка при обработки запроса на обновление 'эпика': "
                        + response.statusCode());
        uri = URI.create(ADDR+uri.getPath()+"?id="+newEpicId);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Ошибка при получении обновленного эпика: "
                        + uri.getPath());
        Epic newEpic2 = gson.fromJson(response.body(), Epic.class);
        assertEquals(newEpic2.getName(), name + " new !", "Возвращена обновленный эпик с неверным именем: "
                +newEpic2.getName());
        //Удаление эпика
        uri = URI.create(ADDR+uri.getPath()+"?id="+epicId2);
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при удалении эпика c id: "
                + epicId2);
        uri = URI.create(ADDR+uri.getPath()+"?id="+epicId2);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode(), "не верный код при получении удаленного эпика: "
                + response.statusCode());
        uri = URI.create(ADDR+uri.getPath());
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "не верный код возврата при удалении всех эпиков: "
                + response.statusCode());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: " + uri.getPath());
        final List<Task> delEpics = parseTasks(response.body());
        assertEquals(delEpics.size(), 0, "После удаления всех эпиков получен не пустой список эпиков" );
    }

    @Test
    void subtaskEndpointTest() throws IOException, InterruptedException {
        URI uri = URI.create(ADDR+"/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString("qeqwe"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode(), "Неверный статус обработки PUT запроса: " + uri.getPath());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: " + uri.getPath());
        final List<Task> subtasks = parseTasks(response.body());
        final List<Integer> subtasksIds = subtasks.stream()
                .filter(task->task.getType()==TaskType.SUBTASK)
                .map(Task::getId)
                .collect(Collectors.toList());
        assertTrue(subtasksIds.size() == 6
                        & subtasksIds.containsAll(Set.of(subId11, subId12, subId21, subId22, subId23, subId31)),
                "После загрузки с HttpServer'а список Подзадач имеет не верный размер и состав");

        uri = URI.create(ADDR+uri.getPath()+"?id="+subId22);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: "
                + uri.getPath());
        final Subtask subtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask.getId(), subId22, "Получена подзадача с неверным идентификатором " + subtask.getId() );
        uri = URI.create(ADDR+uri.getPath()+"?id="+1000);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode(),
                "Не верный статус ответа при запросе подзадачи по неверному идентификатору: "
                        + response.statusCode());

        //Создание подзадачи
        Subtask newSubtask = new Subtask(epicId1, "subtask33", "this subtask33");
        uri = URI.create(ADDR+"/tasks/subtask/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newSubtask)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode(),
                "Ошибка при обработки запроса на создание подзадачи: "
                        + response.statusCode());
        final Integer newSubtaskId = gson.fromJson(response.body(), Integer.class);
        assertNotNull(newSubtaskId,
                "При создании подзадачи возвращен пустой идентификатор");

        uri = URI.create(ADDR+uri.getPath()+"?id="+newSubtaskId);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Ошибка при получении созданной подзадачи: "
                        + uri.getPath());
        Subtask newSubtask1 = gson.fromJson(response.body(), Subtask.class);
        assertEquals(newSubtask1.getId(), newSubtaskId, "Получен созданная подзадача с неверным идентификатором "
                + newSubtask1.getId() );

        //Обновление подзадач
        final String name = newSubtask1.getName();
        newSubtask1.setName(name + " new !");
        uri = URI.create(ADDR+"/tasks/subtask/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newSubtask1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Ошибка при обработки запроса на обновление подзадачи: "
                        + response.statusCode());
        uri = URI.create(ADDR+uri.getPath()+"?id="+newSubtaskId);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Ошибка при получении обновленной подзадачм: "
                        + uri.getPath());
        Subtask newSubtask2 = gson.fromJson(response.body(), Subtask.class);
        assertEquals(newSubtask2.getName(), name + " new !", "Возвращена обновленная подзадача с неверным именем: "
                +newSubtask2.getName());
        //Удаление подзадачи
        uri = URI.create(ADDR+uri.getPath()+"?id="+subId22);
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при удалении подзадачи c id: "
                + subId22);
        uri = URI.create(ADDR+uri.getPath()+"?id="+subId22);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode(), "не верный код при получении удаленной подзадачи: "
                + response.statusCode());
        uri = URI.create(ADDR+uri.getPath());
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "не верный код возврата при удалении всех подзадач: "
                + response.statusCode());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(), "Ошибка при обработки GET запроса: " + uri.getPath());
        final List<Task> delSubtasks = parseTasks(response.body());
        assertEquals(delSubtasks.size(), 0, "После удаления всех подзадач получен не пустой список подзадач" );
    }

    @Test
    void epicSubtaskEndpointTest() throws IOException, InterruptedException {
        URI uri = URI.create(ADDR+"/tasks/subtask/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.statusCode(),
                "Неверный статус обработки запроса на получения списка подзадач " +
                        "эпика без указания идентификатора: " + uri.getPath());
        uri = URI.create(ADDR+uri.getPath()+"?id="+10000);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode(),
                "Неверный статус обработки запроса на получения списка подзадач " +
                        "эпика с неверным идентификатором: " + uri.getPath());
        uri = URI.create(ADDR+uri.getPath()+"?id="+epicId1);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode(),
                "Неверный статус обработки запроса на получения списка подзадач " +
                        "эпика с правильным идентификатором: " + uri.getPath());

    }

    private void createTasks() {
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
    private List<Task> parseTasks(String json) {
        List<Task> tasks = new ArrayList<>();
        final JsonElement jsonElement = JsonParser.parseString(json);
        if(jsonElement.isJsonNull()){
            return tasks;
        }
        final Collection<JsonElement> JsonElements;
        if(jsonElement.isJsonArray()){
            JsonElements = jsonElement.getAsJsonArray().asList();
        } else {
            JsonElements = jsonElement.getAsJsonObject().asMap().values();
        }
        for (JsonElement element : JsonElements) {
            if(element.isJsonNull()){
                continue;
            }
            TaskType type = gson.fromJson(element.getAsJsonObject().get("type"), TaskType.class);
            switch (type) {
                case TASK:
                    tasks.add(gson.fromJson(element, Task.class));
                    break;
                case SUBTASK:
                    tasks.add(gson.fromJson(element, Subtask.class));
                    break;
                case EPIC:
                    tasks.add(gson.fromJson(element, Epic.class));
                    break;
            }
        }
        return tasks;
    }

    private void clearManager() {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
    }
}