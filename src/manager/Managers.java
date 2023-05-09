package manager;

import entities.Task;

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
