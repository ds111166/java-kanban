package manager;


import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import entities.Epic;
import entities.Subtask;
import entities.Task;
import entities.TaskType;
import manager.utilities.CSVTaskFormat;
import manager.utilities.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private final static String KEY = "HttpTaskManager";
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        this.client = new KVTaskClient(url);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    protected void save() {

        ;
        String[] packData = new String[]{gson.toJson(tasks),
                gson.toJson(CSVTaskFormat.historyToString(history))};

        String json = gson.toJson(packData);
        client.put(KEY, json);
    }

    protected void load() {
        String data = client.load(KEY);
        if (data == null) {
            return;
        }
        String[] packData = gson.fromJson(data, String[].class);
        if (packData == null) {
            return;
        }
        List<Task> packTasks = parseTasks(packData[0]);
        packTasks.forEach((task) -> {
            Integer id = task.getId();
            if (id > generatorId) {
                generatorId = id;
            }
            tasks.put(id, task);
        });
        for (Subtask subtask : getSubtasks()) {
            Integer subtaskId = subtask.getId();
            if (subtaskId == null) {
                continue;
            }
            final int epicId = subtask.getEpicId();
            final Epic epic = getEpic(epicId);
            if (epic == null) {
                continue;
            }
            epic.addSubtaskId(subtaskId);
        }
        for (Integer id : tasks.keySet()) {
            prioritizedTasks.add(id);
        }

        CSVTaskFormat
                .historyFromString(gson.fromJson(packData[1], String.class))
                .stream()
                .filter(tasks::containsKey)
                .map(tasks::get)
                .forEachOrdered(history::add);
    }

    private List<Task> parseTasks(String json) {
        List<Task> tasks = new ArrayList<>();

        Map<String, JsonElement> map = JsonParser.parseString(json).getAsJsonObject().asMap();
        for (JsonElement jsonElement : map.values()) {
            TaskType type = gson.fromJson(jsonElement.getAsJsonObject().get("type"), TaskType.class);
            switch (type) {
                case TASK:
                    tasks.add(gson.fromJson(jsonElement, Task.class));
                    break;
                case SUBTASK:
                    tasks.add(gson.fromJson(jsonElement, Subtask.class));
                    break;
                case EPIC:
                    tasks.add(gson.fromJson(jsonElement, Epic.class));
                    break;
            }
        }
        return tasks;
    }
}
