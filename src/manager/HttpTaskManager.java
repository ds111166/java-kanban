package manager;


import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Task;
import manager.exceptions.ManagerSaveException;
import manager.utilities.CSVTaskFormat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class HttpTaskManager extends FileBackedTasksManager {
    private final static String KEY = "HttpTaskManager";
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        this.client = new KVTaskClient(url);
        this.gson = new GsonBuilder().create();
        load();
    }

    @Override
    protected void save() {
        StringBuilder sb = new StringBuilder();
        sb.append(CSVTaskFormat.getHeader()).append("\n");
        for (final Task task : super.tasks.values()) {
            sb.append(CSVTaskFormat.toString(task))
                    .append("\n");
        }
        sb.append("\n");
        sb.append(CSVTaskFormat.historyToString(super.history));
        sb.append("\n");
        client.put(KEY, gson.toJson(sb.toString()));
    }

    private void load() {
        final String data = gson.fromJson(client.load(KEY), String.class);
        if (data == null) {
            return;
        }
        final byte[] bytesData = data.getBytes(StandardCharsets.UTF_8);
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new ByteArrayInputStream(bytesData)))
        ) {
            FileBackedTasksManager.fillingData(reader, this);
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read the data from the server", e);
        }
    }
}
