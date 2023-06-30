package manager.utilities;

import entities.*;
import manager.history.HistoryManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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
     *  Преобразует стоку в задачу
     */
    public static Task taskFromString(String value) {
        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = values[2];
        final Status status = Status.valueOf(values[3]);
        final String description = values[4];
        final int duration = Integer.parseInt(values[5]);
        final LocalDateTime startTime = LocalDateTime.parse(values[6]);
        if (type == TaskType.TASK) {
            return new Task(id, name, description, status, duration, startTime);
        }
        if (type == TaskType.SUBTASK) {
            final int epicId = Integer.parseInt(values[8]);
            return new Subtask(epicId, id, name, description, status, duration, startTime);
        }

        final LocalDateTime endTime = LocalDateTime.parse(values[7]);
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
            ids.add(Integer.parseInt(id));
        }
        return ids;
    }

    /**
     * получает из менеджера истории задач строку с идентификаторами задач, разделенными ','
     */
    public static String historyToString(HistoryManager historyManager) {
        final List<Task> history = historyManager.getHistory();
        int size = history.size();
        if( size == 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(history.get(0).getId());
        for(int i=1;  i < size; i++) {
            Task task = history.get(i);
            sb.append(",");
            sb.append(task.getId());
        }
        return sb.toString();
    }


    public static String getHeader() {
        return "id,type,name,status,description,duration,startTime,endTime,epic";
    }
}
