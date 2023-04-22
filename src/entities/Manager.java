package entities;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Хранилище данных
 *
 * @author ds111166
 */
public class Manager {

    int primaryKeyOfTasks;
    int primaryKeyOfSubtasks;
    int primaryKeyOfEpiks;
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;

    public Manager() {

        this.primaryKeyOfEpiks = 1;
        this.primaryKeyOfSubtasks = 1;
        this.primaryKeyOfTasks = 1;

        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.tasks = new HashMap<>();
    }

    /**
     * получить список всех задач
     *
     * @return
     */
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * удалить все задачи
     */
    public void deleteAllTasks() {
        tasks.clear();
    }

    /**
     * получение задачи по идентификатору
     *
     * @param id
     * @return
     */
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    /**
     * создать задачу
     *
     * @param task
     * @return
     */
    public Task createTask(Task task) {
        if (task != null && task.getId() == null) {
            tasks.put(primaryKeyOfTasks, task);
            task.setId(primaryKeyOfTasks);
            primaryKeyOfTasks++;
            return task;
        }
        return null;
    }

    /**
     * обновить объкт Задача
     *
     * @param task
     * @return
     */
    public Task updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return tasks.get(task.getId());
        }
        return null;
    }

    /**
     * удалить задачу по идентификатору
     *
     * @param id
     * @return
     */
    public boolean deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    /**
     * получить все подзадачи
     *
     * @return
     */
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Удаляет все подзадачи
     */
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            Epic epic = epics.get(id);
            if (epic != null && epic.getSubtaskIds() != null) {
                epic.getSubtaskIds().clear();
                Epic updateEpic = updateEpic(epic);
            }
        }
    }

    /**
     * получение подзадачи по идентификатору
     *
     * @param id
     * @return
     */
    public Subtask getSubTaskById(int id) {
        return subtasks.get(id);
    }

    /**
     * создание подзадачи
     *
     * @param subtask
     * @return
     */
    public Subtask createSubtask(Subtask subtask) {

        if (subtask != null && subtask.getId() == null) {
            Epic epic = getEpicById(subtask.getIdEpic());
            if (epic != null) {
                subtask.setId(primaryKeyOfSubtasks);
                subtask.setStatus(Status.NEW);
                subtasks.put(primaryKeyOfSubtasks, subtask);
                primaryKeyOfSubtasks++;
                Subtask newSubTask = subtasks.get(subtask.getId());
                if (newSubTask != null) {
                    epic.getSubtaskIds().add(newSubTask.getId());
                    Epic updateEpic = updateEpic(epic);
                    return newSubTask;
                }
            }
        }
        return null;
    }

    /**
     * обновить объкт Задача
     *
     * @param subtask
     * @return
     */
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null && epic.getSubtaskIds().contains(subtask.getId())) {
                subtasks.put(subtask.getId(), subtask);
                Subtask updateSubtask = subtasks.get(subtask.getId());
                if (updateSubtask != null) {
                    updateEpic(epic);
                    return updateSubtask;
                }
            }
        }
        return null;
    }

    /**
     * удаление подзадачи по идентификатору
     *
     * @param id
     * @return
     */
    public boolean deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                Epic epic = epics.get(subtask.getIdEpic());
                if (epic != null && epic.getSubtaskIds().contains(subtask.getId())) {
                    Integer idSubtask = subtask.getId();
                    subtasks.remove(idSubtask);
                    epic.getSubtaskIds().remove(idSubtask);
                    updateEpic(epic);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ***************
     */
    /**
     * получить все эпики
     *
     * @return
     */
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Удаление всех эпиков
     */
    public void deleteAllEpics() {
        this.subtasks.clear();
        this.epics.clear();
    }

    /**
     * получение эпика по идентификатору
     *
     * @param id
     * @return
     */
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    /**
     * создание эпика
     *
     * @param epic
     * @return
     */
    public Epic createEpic(Epic epic) {
        if (epic != null && epic.getId() == null) {
            epic.setId(primaryKeyOfEpiks);
            epic.setStatus(Status.IN_PROGRESS);
            epics.put(primaryKeyOfEpiks, epic);
            return epics.get(primaryKeyOfEpiks++);
        }
        return null;
    }

    /**
     * Обновление эпика
     *
     * @param epic
     * @return
     */
    public Epic updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            Status calculationStatus = calculationStatus(epic);
            epic.setStatus(calculationStatus);
            epics.put(epic.getId(), epic);
            return epics.get(epic.getId());
        }
        return null;
    }

    /**
     * удаление эпика по идентификатору
     *
     * @param id
     * @return
     */
    public boolean deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            if (epic != null) {
                ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
                if (subtaskIds != null) {
                    for (Integer idSubtask : subtaskIds) {
                        subtasks.remove(idSubtask);
                    }
                }
            }
            epics.remove(id);
            return true;
        }
        return false;
    }

    /**
     * Расчет статуса эпика
     *
     * @param epic
     * @return
     */
    public Status calculationStatus(Epic epic) {
        boolean isNew = true;
        boolean isDone = true;
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds == null || subtaskIds.isEmpty()) {
            isDone = false;
        } else {
            for (Integer idSubtask : subtaskIds) {
                Subtask subtask = subtasks.get(idSubtask);
                if (subtask == null) {
                    isDone = false;
                } else {
                    switch (subtask.getStatus()) {
                        case NEW:
                            isDone = false;
                            break;
                        case IN_PROGRESS:
                            isNew = false;
                            isDone = false;
                            break;
                        case DONE:
                            isNew = false;
                            break;
                        default:
                            isDone = false;
                    }

                }
            }
        }
        if (isNew) {
            return Status.NEW;
        } else if (isDone) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }

    /**
     * получение списка всех подзадач эпика
     *
     * @param epic
     * @return
     */
    public ArrayList<Subtask> getAllTheEpicSubtasks(Epic epic) {
        if (epic != null) {
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds != null) {
                ArrayList<Subtask> listSubtask = new ArrayList<>();
                for (Integer idSubtask : subtaskIds) {
                    Subtask subtask = subtasks.get(idSubtask);
                    listSubtask.add(subtask);
                }
                return listSubtask;
            }
        }
        return null;
    }

}

