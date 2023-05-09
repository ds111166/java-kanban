package manager;

import entities.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final int MAXIMUM_HISTORY_LENGTH;
    private final List<Task> tasks;

    public InMemoryHistoryManager() {
        this.MAXIMUM_HISTORY_LENGTH = 10;
        this.tasks = new ArrayList<>();
    }

    public InMemoryHistoryManager(int MAXIMUM_HISTORY_LENGTH) {
        if (MAXIMUM_HISTORY_LENGTH > -1) {
            this.MAXIMUM_HISTORY_LENGTH = MAXIMUM_HISTORY_LENGTH;
        } else {
            this.MAXIMUM_HISTORY_LENGTH = 10;
        }
        this.tasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        tasks.add(task);
        if (tasks.size() > MAXIMUM_HISTORY_LENGTH) {
            tasks.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return tasks;
    }
}
