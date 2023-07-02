package manager;

import entities.Epic;
import entities.Subtask;
import entities.Task;

import java.util.List;

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
    List<Task> getTasks();

    /**
     * удалить все задачи
     */
    void deleteTasks();

    /**
     * получение задачи по идентификатору
     *
     * @param id идентификатор задачи
     * @return задача
     */
    Task getTask(int id);

    /**
     * создать задачу
     *
     * @param newTask задача
     * @return идентификатор задачи
     */
    Integer createTask(Task newTask);

    /**
     * обновить объкт Задача
     *
     * @param updatedTask Задача
     */
    void updateTask(Task updatedTask);

    /**
     * удалить задачу по идентификатору
     *
     * @param id идентификатор задачи
     */
    void deleteTask(int id);

    /**
     * получить все подзадачи
     *
     * @return список подзадач
     */
    List<Subtask> getSubtasks();

    /**
     * Удаляет все подзадачи
     */
    void deleteSubtasks();

    /**
     * получение подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     * @return Подзадача
     */
    Subtask getSubtask(int id);

    /**
     * создание подзадачи
     *
     * @param newSubtask новая подзадача
     * @return идентификатор подзадачи
     */
    Integer createSubtask(Subtask newSubtask);

    /**
     * обновить объкт Задача
     *
     * @param updatedSubtask обновляемая подзадача
     */
    void updateSubtask(Subtask updatedSubtask);

    /**
     * удаление подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     */
    void deleteSubtask(int id);

    /**
     * получить все эпики
     *
     * @return список всех эпиков
     */
    List<Epic> getEpics();

    /**
     * Удаление всех эпиков
     */
    void deleteEpics();

    /**
     * получение эпика по идентификатору
     *
     * @param id идентификатор эпика
     * @return эпик
     */
    Epic getEpic(int id);

    /**
     * создание эпика
     *
     * @param newEpic новый эпик
     * @return идентификатор созданного эпика
     */
    Integer createEpic(Epic newEpic);

    /**
     * Обновление эпика
     *
     * @param epic эпик
     */
    void updateEpic(Epic epic);

    /**
     * удаление эпика по идентификатору
     *
     * @param id идентификатор
     */
    void deleteEpic(int id);

    /**
     * получение списка всех подзадач эпика
     *
     * @param epicId идентификатор эпика
     * @return список всех подзадач
     */
    List<Subtask> getEpicSubtasks(int epicId);

    /**
     * Возвращает историю задач
     *
     */
    List<Task> getHistory();

    /**
     * Возвращает список задач в порядке возрастания startTime
     */
    List<Task> getPrioritizedTasks();

}

