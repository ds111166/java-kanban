package server.handler;

import entities.Task;
import manager.TaskManager;

import java.util.List;

public class HistoryHandler extends TasksHandler{

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected String getJsonEntities() {
        final List<Task> tasks = manager.getHistory();
        if(tasks == null) {
            return null;
        }
        return gson.toJson(tasks);
    }
}
