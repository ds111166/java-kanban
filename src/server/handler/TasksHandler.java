package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Task;
import manager.TaskManager;
import manager.utilities.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


public class TasksHandler implements HttpHandler {
    protected final TaskManager manager;
    protected final Gson gson;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String method = exchange.getRequestMethod();
        final Pair<Integer, String> result;
        switch (method) {
            case "GET":
                result = doGet(exchange);
                break;
            case "POST":
                result = doPost(exchange);
                break;
            case "DELETE":
                result = doDelete(exchange);
                break;
            default:
                result = new Pair<>(HttpURLConnection.HTTP_BAD_METHOD, "");
        }
        final String response = result.getValue();
        exchange.sendResponseHeaders(result.getKey(), response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected Pair<Integer, String> doDelete(HttpExchange exchange) {
        return new Pair<>(HttpURLConnection.HTTP_BAD_METHOD, "");
    }

    protected Pair<Integer, String> doPost(HttpExchange exchange) {
        return new Pair<>(HttpURLConnection.HTTP_BAD_METHOD, "");
    }

    protected Pair<Integer, String> doGet(HttpExchange exchange) {
        try {
            String json = getJsonEntities();
            if (json == null) {
                return new Pair<>(HttpURLConnection.HTTP_NO_CONTENT, "");
            }
            return new Pair<>(HttpURLConnection.HTTP_OK, json);
        } catch (Exception ex) {
            return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }
    }

    protected String getJsonEntities() {
        final List<Task> tasks = manager.getPrioritizedTasks();
        if (tasks == null) {
            return null;
        }
        return gson.toJson(tasks);
    }

    protected String getJsonEntities(int id) {
        final Task task = manager.getTask(id);
        if (task == null) {
            return null;
        }
        return gson.toJson(task);
    }
}
