package manager;


import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entities.Task;
import manager.utilities.LocalDateTimeAdapter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    static class PackData {
        Map<Integer, Task> packTasks;
        List<Task> packHistory;

        public PackData(Map<Integer, Task> packTasks, List<Task> packHistory) {
            this.packTasks = packTasks;
            this.packHistory = packHistory;
        }
    }

    private final static String KEY = "HttpTaskManager";
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        this.client = new KVTaskClient(url);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        load();
    }

    @Override
    protected void save() {
        PackData packData = new PackData(tasks, history.getHistory());
        Type packingDataType = new TypeToken<PackData>() {
        }.getType();
        String json = gson.toJson(packData, packingDataType);
        client.put(KEY, json);
    }

    private void load() {
        String data = client.load(KEY);
        if (data == null) {
            return;
        }
        Type packingDataType = new TypeToken<PackData>() {
        }.getType();
        PackData packingData = gson.fromJson(data, packingDataType);
        if (packingData == null) {
            return;
        }
        generatorId = -1;
        packingData.packTasks.forEach((id, task) -> {
            if (id > generatorId) {
                generatorId = id;
            }
            tasks.put(id, task);
        });
        prioritizedTasks.addAll(tasks.keySet());
        packingData.packHistory.forEach(history::add);
    }
}
