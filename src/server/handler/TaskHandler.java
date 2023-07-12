package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Epic;
import entities.Subtask;
import entities.Task;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static server.handler.HandlerUtilities.queryToMap;

public class TaskHandler<T extends Task> implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;
    private Function<Integer, T> getEntity;
    private  Consumer<T> updateEntity;
    private  Consumer<Integer> deleteEntity;
    private  Supplier<List<T>> getEntities;
    final Class<?> genericDeclaration

    public TaskHandler (TaskManager manager) {
        this.manager = manager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.create();
        final TypeVariable<? extends Class<?>>[] typeParameters = this.getClass().getTypeParameters();
        genericDeclaration = typeParameters[0].getGenericDeclaration();
        String genericName = genericDeclaration.getCanonicalName();
        if(genericName.contains("Epic")){
            getEntity = id ->  (T) manager.getEpic(id);
            updateEntity = epic -> manager.updateEpic((Epic)epic);
            deleteEntity = manager::deleteEpic;
            getEntities = () -> (List<T>)manager.getEpics();
        } else if (genericName.contains("Subtask")) {
            getEntity = id ->  (T) manager.getSubtask(id);
            updateEntity = epic -> manager.updateSubtask((Subtask)epic);
            deleteEntity = manager::deleteSubtask;
            getEntities = () -> (List<T>)manager.getSubtasks();
        } else {
            getEntity = id ->  (T) manager.getTask(id);
            updateEntity = epic -> manager.updateTask((Task)epic);
            deleteEntity = manager::deleteTask;
            getEntities = () -> (List<T>)manager.getTasks();
        }

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String requestMethod = exchange.getRequestMethod();
        final InputStream requestBody = exchange.getRequestBody();
        final String requestQuery = exchange.getRequestURI().getQuery();
        final Map<String, String> parametrs = queryToMap(requestQuery);

        if ("GET".equals(requestMethod)) {
            final String paramId = parametrs.get("id");
            if(paramId == null) {
                final List<T> ts = getEntities.get();
                gson.fromJson(ts, genericDeclaration);


                return;
            }

        } else if ("POST".equals(requestMethod)) {
            doPost(exchange);
        } else if("DELETE".equals(requestMethod)) {
            doDelete(exchange);
        } else {
            exchange.sendResponseHeaders(405, 0);
        }
    }

    private void doGet(HttpExchange exchange) {

    }

}
/*

 */