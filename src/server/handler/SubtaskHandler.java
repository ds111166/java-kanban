package server.handler;

import entities.Subtask;
import entities.Task;
import javafx.util.Pair;
import manager.TaskManager;

import java.net.HttpURLConnection;
import java.util.List;

public class SubtaskHandler extends TaskHandler {
    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected Pair<Integer, String> updateEntity(Task task) {
        Subtask updateSubtask = (Subtask) task;
        manager.updateSubtask(updateSubtask);
        return new Pair<>(HttpURLConnection.HTTP_OK, "");
    }

    @Override
    protected Pair<Integer, String> createEntity(Task task) {
        Subtask newSubtask = (Subtask) task;
        final Integer id = manager.createSubtask(newSubtask);
        if (id == null) {
            return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }
        newSubtask.setId(id);
        final String json = gson.toJson(newSubtask.getId());
        return new Pair<>(HttpURLConnection.HTTP_CREATED, json);
    }

    @Override
    protected void deleteEntities() {
        manager.deleteSubtasks();
    }

    @Override
    protected void deleteEntities(int id) {
        manager.deleteSubtask(id);
    }

    @Override
    protected String getJsonEntities() {
        final List<Subtask> subtasks = manager.getSubtasks();
        if (subtasks == null) {
            return null;
        }
        return gson.toJson(subtasks);
    }

    @Override
    protected String getJsonEntities(int id) {
        final Subtask subtask = manager.getSubtask(id);
        if (subtask == null) {
            return null;
        }
        return gson.toJson(subtask);
    }
}
