package manager;

import entities.Task;
import java.util.List;

public interface HistoryManager {
    /**
     * Добавляет задачу в историю
     * @param task - задача
     */
    void add(Task task);

    /**
     * Возвращает истрию просмотров задач
     * @return - список задач
     */
    List<Task> getHistory();

}
