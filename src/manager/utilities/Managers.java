package manager.utilities;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;

import java.io.File;

public class Managers {
    /**
     * Возвращает менеджер задач активный по умолчанию
     *
     * @return - менеджер задач
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
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
