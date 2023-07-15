package server.handler;


import com.sun.net.httpserver.HttpExchange;
import entities.Task;
import javafx.util.Pair;
import manager.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static server.handler.HandlerUtilities.getParametrId;
import static server.handler.HandlerUtilities.queryToMap;


public class TaskHandler extends TasksHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected Pair<Integer, String> doGet(HttpExchange exchange) {
        try {
            final Integer id = getParametrId(exchange);
            final String json = (id == null) ? getJsonEntities() : getJsonEntities(id);
            if (json == null) {
                return new Pair<>(HttpURLConnection.HTTP_NO_CONTENT, "");
            }
            return new Pair<>(HttpURLConnection.HTTP_OK, json);
        } catch (Exception ex) {
            return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }
    }

    @Override
    protected Pair<Integer, String> doDelete(HttpExchange exchange) {
        try {
            final Map<String, String> queriedToMap = queryToMap(exchange.getRequestURI().getQuery());
            final Integer id = getParametrId(exchange);
            if (id == null && queriedToMap != null && !queriedToMap.isEmpty()) {
                return new Pair<>(HttpURLConnection.HTTP_BAD_METHOD, "");
            }
            if (id != null && id < 1) {
                return new Pair<>(HttpURLConnection.HTTP_NOT_FOUND, "");
            }
            if (id == null) {
                deleteEntities();
            } else {
                deleteEntities(id);
            }
            return new Pair<>(HttpURLConnection.HTTP_OK, "");
        } catch (Exception ex) {
            return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }
    }

    @Override
    protected Pair<Integer, String> doPost(HttpExchange exchange) {
        try {
            final Map<String, String> queriedToMap = queryToMap(exchange.getRequestURI().getQuery());
            if (queriedToMap != null && !queriedToMap.isEmpty()) {
                return new Pair<>(HttpURLConnection.HTTP_BAD_METHOD, "");
            }
            try {
                return handlePost(exchange);
            } catch (IOException ex) {
                return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
            }

        } catch (Exception ex) {
            return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }
    }

    private Pair<Integer, String> handlePost(HttpExchange exchange) throws IOException {
        final byte[] bytes = exchange.getRequestBody().readAllBytes();
        final String s = new String(bytes, StandardCharsets.UTF_8);
        final Task task = gson.fromJson(s, Task.class);
        if (task == null) {
            return new Pair<>(HttpURLConnection.HTTP_BAD_METHOD, "");
        }
        if (task.getId() == null) {
            return createEntity(task);
        }
        return updateEntity(task);
    }

    protected Pair<Integer, String> updateEntity(Task task) {
        manager.updateTask(task);
        return new Pair<>(HttpURLConnection.HTTP_OK, "");
    }

    protected Pair<Integer, String> createEntity(Task task) {
        final Integer id = manager.createTask(task);
        if (id == null) {
            return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }
        task.setId(id);
        final String json = gson.toJson(task);
        return new Pair<>(HttpURLConnection.HTTP_CREATED, json);
    }

    protected void deleteEntities() {
        manager.deleteTasks();
    }

    protected void deleteEntities(int id) {
        manager.deleteTask(id);
    }

    @Override
    protected String getJsonEntities() {
        final List<Task> tasks = manager.getTasks();
        if (tasks == null) {
            return null;
        }
        return gson.toJson(tasks);
    }

}
