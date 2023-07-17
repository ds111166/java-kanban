package server.handler;

import entities.Epic;
import entities.Task;
import javafx.util.Pair;
import manager.TaskManager;

import java.net.HttpURLConnection;
import java.util.List;

public class EpicHandler extends TaskHandler {
    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected Pair<Integer, String> updateEntity(Task task) {
        Epic updateEpic = (Epic) task;
        manager.updateEpic(updateEpic);
        return new Pair<>(HttpURLConnection.HTTP_OK, "");
    }

    @Override
    protected Pair<Integer, String> createEntity(Task task) {
        Epic newEpic = (Epic) task;
        final Integer id = manager.createEpic(newEpic);
        if (id == null) {
            return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }
        newEpic.setId(id);
        final String json = gson.toJson(newEpic.getId());
        return new Pair<>(HttpURLConnection.HTTP_CREATED, json);
    }

    @Override
    protected void deleteEntities() {
        manager.deleteEpics();
    }

    @Override
    protected void deleteEntities(int id) {
        manager.deleteEpic(id);
    }

    @Override
    protected String getJsonEntities() {
        final List<Epic> epics = manager.getEpics();
        if (epics == null) {
            return null;
        }
        return gson.toJson(epics);
    }

    @Override
    protected String getJsonEntities(int id) {
        final Epic epic = manager.getEpic(id);
        if (epic == null) {
            return null;
        }
        return gson.toJson(epic);
    }
}
