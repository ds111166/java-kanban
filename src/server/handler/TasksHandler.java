package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Task;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class TasksHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;
    public TasksHandler(TaskManager manager) {
        this.manager = manager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, 0);
            return;
        }
        final List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        final byte[] response = gson.toJson(prioritizedTasks).getBytes();

        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
