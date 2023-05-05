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
public interface TaskManager {





    /**
     * получить список всех задач
     *
     * @return список всех задач
     */
    public ArrayList<Task> getTasks();

    /**
     * удалить все задачи
     */
    public void deleteTasks();

    /**
     * получение задачи по идентификатору
     *
     * @param id идентификатор задачи
     * @return задача
     */
    public Task getTask(int id);

    /**
     * создать задачу
     *
     * @param newTask задача
     * @return идентификатор задачи
     */
    public Integer createTask(Task newTask);

    /**
     * обновить объкт Задача
     *
     * @param updatedTask Задача
     */
    public void updateTask(Task updatedTask);

    /**
     * удалить задачу по идентификатору
     *
     * @param id идентификатор задачи
     */
    public void deleteTask(int id);

    /**
     * получить все подзадачи
     *
     * @return список подзадач
     */
    public ArrayList<Subtask> getSubtasks();

    /**
     * Удаляет все подзадачи
     */
    public void deleteSubtasks();

    /**
     * получение подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     * @return Подзадача
     */
    public Subtask getSubtask(int id);

    /**
     * создание подзадачи
     *
     * @param newSubtask новая подзадача
     * @return идентификатор подзадачи
     */
    public Integer createSubtask(Subtask newSubtask);

    /**
     * обновить объкт Задача
     *
     * @param updatedSubtask обновляемая подзадача
     */
    public void updateSubtask(Subtask updatedSubtask);

    /**
     * удаление подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     */
    public void deleteSubtask(int id);

    /**
     * получить все эпики
     *
     * @return список всех эпиков
     */
    public ArrayList<Epic> getEpics();

    /**
     * Удаление всех эпиков
     */
    public void deleteEpics();

    /**
     * получение эпика по идентификатору
     *
     * @param id идентификатор эпика
     * @return эпик
     */
    public Epic getEpic(int id);

    /**
     * создание эпика
     *
     * @param newEpic новый эпик
     * @return идентификатор созданного эпика
     */
    public Integer createEpic(Epic newEpic);

    /**
     * Обновление эпика
     *
     * @param epic эпик
     */
    public void updateEpic(Epic epic);

    /**
     * удаление эпика по идентификатору
     *
     * @param id идентификатор
     */
    public void deleteEpic(int id);

    /**
     * получение списка всех подзадач эпика
     *
     * @param epicId идентификатор эпика
     * @return список всех подзадач
     */
    public ArrayList<Subtask> getEpicSubtasks(int epicId);



}

