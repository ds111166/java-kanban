package manager;

import entities.Epic;
import entities.Status;
import entities.Subtask;
import entities.Task;
import manager.history.HistoryManager;
import manager.utilities.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    protected int generatorId;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;

    protected final HistoryManager history;

    public InMemoryTaskManager() {
        this.generatorId = 0;
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.tasks = new HashMap<>();
        this.history = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public Integer createTask(Task newTask) {
        if (newTask != null) {
            int id = ++generatorId;
            tasks.put(id, newTask);
            newTask.setId(id);
            return id;
        }
        return null;
    }

    @Override
    public void updateTask(Task updatedTask) {
        final int id = updatedTask.getId();
        final Task savedTask = tasks.get(id);

        if (savedTask == null) {
            return;
        }
        tasks.put(id, updatedTask);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        history.add(subtask);
        return subtask;
    }

    @Override
    public Integer createSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            return null;
        }
        final int epicId = newSubtask.getEpicId();
        Epic basicEpic = getEpic(epicId);
        if (basicEpic == null) {
            return null;
        }
        int id = ++generatorId;
        subtasks.put(id, newSubtask);
        newSubtask.setId(id);
        basicEpic.addSubtaskId(newSubtask.getId());
        updateEpicStatus(epicId);

        return id;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        final int id = updatedSubtask.getId();
        final int epicId = updatedSubtask.getEpicId();
        final Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtasks.put(id, updatedSubtask);
        updateEpicStatus(epicId);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        history.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpics() {
        this.subtasks.clear();
        this.epics.clear();
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        history.add(epic);
        return epic;
    }

    @Override
    public Integer createEpic(Epic newEpic) {
        if (newEpic == null) {
            return null;
        }
        int id = ++generatorId;
        newEpic.setId(id);
        epics.put(id, newEpic);
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            final Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void deleteEpic(int id) {
        final Epic epic = epics.remove(id);
        history.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
            history.remove(subtaskId);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> resSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        for (int id : epic.getSubtaskIds()) {
            resSubtasks.add(subtasks.get(id));
        }
        return resSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    /**
     * Устанавливает статус эпика в соответствии расчитанным по статусам подзадач эпика
     *
     * @param epicId идентификатор эпика
     */
    protected void updateEpicStatus(int epicId) {
        final Epic epic = epics.get(epicId);
        int countNew = 0;
        int countDone = 0;
        int count = 0;

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds != null) {
            for (Integer id : subtaskIds) {
                count++;
                final Subtask subtask = subtasks.get(id);
                if (subtask == null || subtask.getStatus() == Status.NEW) {
                    countNew++;
                } else if (subtask.getStatus() == Status.DONE) {
                    countDone++;
                }
            }
        }
        if (count == 0 || count == countNew) {
            epic.setStatus(Status.NEW);
        } else if (count == countDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

}
