package manager.history;

import entities.Task;

import java.util.List;

public interface HistoryManager {
    /**
     * Добавляет задачу в историю
     *
     * @param task - задача
     */
    void add(Task task);

    /**
     * Удаляет задачу с идентификаторм id из истории
     */
    void remove(int id);

    /**
     * Возвращает истрию просмотров задач
     *
     * @return - список задач
     */
    List<Task> getHistory();

}
