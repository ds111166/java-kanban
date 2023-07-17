package server;

import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTasksManager;
import manager.TaskManager;
import server.handler.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    final static File FILE = new File("./resources/HttpTaskServer.csv");
    private final HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer() throws IOException {
        this.manager = new FileBackedTasksManager(FILE);
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        initContext();
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        initContext();
    }

    public void start() {
        System.out.println("Запускаем сервер HttpTaskServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    private void initContext() {
        server.createContext("/tasks/", new TasksHandler(manager));
        server.createContext("/tasks/task/", new TaskHandler(manager));
        server.createContext("/tasks/epic/", new EpicHandler(manager));
        server.createContext("/tasks/subtask/", new SubtaskHandler(manager));
        server.createContext("/tasks/history/", new HistoryHandler(manager));
        server.createContext("/tasks/subtask/epic/", new EpicSubtasksHandler(manager));
    }
}
