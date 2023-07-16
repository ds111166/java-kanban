package manager.utilities;

import entities.*;
import manager.history.HistoryManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {
    /**
     * преобразует задачу в строку
     */
    public static String toString(Task task) {
        final TaskType taskType = task.getType();
        //id,type,name,status,description,duration,startTime,endTime,epic"
        StringBuilder sb = new StringBuilder()
                .append(task.getId())
                .append(",")
                .append(taskType)
                .append(",")
                .append(task.getName())
                .append(",")
                .append(task.getStatus())
                .append(",")
                .append(task.getDescription())
                .append(",")
                .append(task.getDuration())
                .append(",")
                .append(task.getStartTime())
                .append(",")
                .append(taskType.equals(TaskType.EPIC) ? ((Epic) task).getEndTime() : "")
                .append(",")
                .append(taskType.equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : "");
        return sb.toString();
    }

    /**
     * Преобразует стоку в задачу
     */
    public static Task taskFromString(String value) {
        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = values[2];
        final Status status = parseStatus(values[3]);
        final String description = values[4];
        final int duration = Integer.parseInt(values[5]);

        final LocalDateTime startTime = parseDateTime(values[6]);

        if (type == TaskType.TASK) {
            return new Task(id, name, description, status, duration, startTime);
        }
        if (type == TaskType.SUBTASK) {
            final int epicId = Integer.parseInt(values[8]);
            return new Subtask(epicId, id, name, description, status, duration, startTime);
        }

        final LocalDateTime endTime = parseDateTime(values[7]);
        final Epic epic = new Epic(id, name, description, status, duration, startTime);
        epic.setEndTime(endTime);
        return epic;
    }

    /**
     * Получает из строки с идентификаторами разделенными ',' список идентификаторов
     */
    public static List<Integer> historyFromString(String value) {
        final String[] values = value.split(",");
        final ArrayList<Integer> ids = new ArrayList<>(values.length);
        for (String id : values) {
            if (!id.isEmpty()) {
                ids.add(Integer.parseInt(id));
            }
        }
        return ids;
    }

    /**
     * получает из менеджера истории задач строку с идентификаторами задач, разделенными ','
     */
    public static String historyToString(HistoryManager historyManager) {
        final List<Task> history = historyManager.getHistory();
        int size = history.size();
        if (size == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(history.get(0).getId());
        for (int i = 1; i < size; i++) {
            Task task = history.get(i);
            sb.append(",");
            sb.append(task.getId());
        }
        return sb.toString();
    }


    public static String getHeader() {
        return "id,type,name,status,description,duration,startTime,endTime,epic";
    }

    public static Task cloneTask(Task task) {
        if (task == null) {
            return null;
        }
        final TaskType type = task.getType();
        switch (type) {
            case TASK:
                return new Task(task);
            case SUBTASK:
                return new Subtask((Subtask) task);
            case EPIC:
                return new Epic((Epic) task);
            default:
                return null;
        }
    }

    private static LocalDateTime parseDateTime(String textDateTime) {
        try {
            if (textDateTime == null
                    || textDateTime.isEmpty()
                    || textDateTime.isBlank()
                    || "null".equals(textDateTime.toLowerCase())) {
                return null;
            }
            return LocalDateTime.parse(textDateTime);
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Status parseStatus(String textStatus) {
        try {
            if (textStatus == null
                    || textStatus.isEmpty()
                    || textStatus.isBlank()
                    || "null".equals(textStatus.toLowerCase())) {
                return null;
            }
            return Status.valueOf(textStatus);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
