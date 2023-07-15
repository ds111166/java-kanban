package manager.utilities;

import manager.HttpTaskManager;
import manager.TaskManager;
import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;

public class Managers {
    /**
     * Возвращает менеджер задач активный по умолчанию
     *
     * @return - менеджер задач
     */
    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078/");
    }

    /**
     * Возвращает менеджер истории просмотра задач активный по умолчанию
     *
     * @return - менеджер истории просмотра задач
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
