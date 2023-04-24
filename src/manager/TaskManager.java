package manager;

import entities.Epic;
import entities.Status;
import entities.Subtask;
import entities.Task;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Хранилище данных
 *
 * @author ds111166
 */
public class TaskManager {

    private int generatorId;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        this.generatorId = 0;
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.tasks = new HashMap<>();
    }

    /**
     * получить список всех задач
     *
     * @return список всех задач
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * удалить все задачи
     */
    public void deleteTasks() {
        tasks.clear();
    }

    /**
     * получение задачи по идентификатору
     *
     * @param id дентификатор задачи
     * @return задача
     */
    public Task getTask(int id) {
        return tasks.get(id);
    }

    /**
     * создать задачу
     *
     * @param newTask задача
     * @return дентификатор задачи
     */
    public Integer createTask(Task newTask) {
        if (newTask != null) {
            int id = ++generatorId;
            tasks.put(id, newTask);
            newTask.setId(id);
            return id;
        }
        return null;
    }

    /**
     * обновить объкт Задача
     *
     * @param updatedTask Задача
     */
    public void updateTask(Task updatedTask) {
        final int id = updatedTask.getId();
        final Task savedTask = tasks.get(id);

        if (savedTask == null) {
            return;
        }
        tasks.put(id, updatedTask);
    }

    /**
     * удалить задачу по идентификатору
     *
     * @param id идентификатор задачи
     */
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    /**
     * получить все подзадачи
     *
     * @return список подзадач
     */
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Удаляет все подзадачи
     */
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    /**
     * получение подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     * @return Подзадача
     */
    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    /**
     * создание подзадачи
     *
     * @param newSubtask новая подзадача
     * @return идентификатор подзадачи
     */
    public Integer createSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            return null;
        }
        final int epicId = newSubtask.getEpicId();
        Epic basicEpic = getEpic(epicId);
        if(basicEpic == null){
            return null;
        }
        int id = ++generatorId;
        subtasks.put(id, newSubtask);
        newSubtask.setId(id);
        basicEpic.addSubtaskId(newSubtask.getId());
        updateEpicStatus(epicId);

        return id;
    }

    /**
     * обновить объкт Задача
     *
     * @param updatedSubtask обновляемая подзадача
     */
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

    /**
     * удаление подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     */
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    /**
     * получить все эпики
     *
     * @return список всех эпиков
     */
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Удаление всех эпиков
     */
    public void deleteEpics() {
        this.subtasks.clear();
        this.epics.clear();
    }

    /**
     * получение эпика по идентификатору
     *
     * @param id идентификатор эпика
     * @return эпик
     */
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    /**
     * создание эпика
     *
     * @param newEpic новый эпик
     * @return идентификатор созданного эпика
     */
    public Integer createEpic(Epic newEpic) {
        if(newEpic == null){
           return null;
        }
        int id = ++generatorId;
        newEpic.setId(id);
        epics.put(id, newEpic);
        return id;
    }

    /**
     * Обновление эпика
     *
     * @param epic эпик
     */
    public void updateEpic(Epic epic) {
        if (epic != null) {
            final Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    /**
     * удаление эпика по идентификатору
     *
     * @param id идентификатор
     */
    public void deleteEpic(int id) {
        final Epic epic = epics.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
    }


    /**
     * Устанавливает статус эпика в соответствии расчитанным по статусам подзадач эпика
     * @param epicId идентификатор эпика
     */
    public void updateEpicStatus(int epicId) {
        final Epic epic = epics.get(epicId);
        int countNew = 0;
        int countDone = 0;
        int count = 0;


        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
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

    /**
     * получение списка всех подзадач эпика
     *
     * @param epicId идентификатор эпика
     * @return список всех подзадач
     */
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> tasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        for (int id : epic.getSubtaskIds()) {
            tasks.add(subtasks.get(id));
        }
        return tasks;
    }



}

