package server.handler;

import com.sun.net.httpserver.HttpExchange;
import entities.Subtask;
import javafx.util.Pair;
import manager.TaskManager;

import java.net.HttpURLConnection;
import java.util.List;

import static server.handler.HandlerUtilities.getParametrId;

public class EpicSubtasksHandler extends TasksHandler {
    public EpicSubtasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected Pair<Integer, String> doGet(HttpExchange exchange) {
        try {
            final Integer parametrId = getParametrId(exchange);
            if (parametrId == null) {
                return new Pair<>(HttpURLConnection.HTTP_BAD_REQUEST, "");
            }
            String json = getJsonEntities(parametrId);
            if (json == null) {
                return new Pair<>(HttpURLConnection.HTTP_NO_CONTENT, "");
            }
            return new Pair<>(HttpURLConnection.HTTP_OK, json);
        } catch (Exception ex) {
            return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }
    }

    @Override
    protected String getJsonEntities(int epicId) {
        final List<Subtask> epicSubtasks = manager.getEpicSubtasks(epicId);
        if (epicSubtasks == null) {
            return null;
        }
        return gson.toJson(epicSubtasks);
    }
}
