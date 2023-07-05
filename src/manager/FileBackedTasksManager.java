package manager;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import entities.TaskType;
import manager.exceptions.ManagerSaveException;
import manager.utilities.CSVTaskFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File taskStore;

    public FileBackedTasksManager(File taskStore) {
        this.taskStore = taskStore;
    }

    /**
     * Создает экземпляр мененджера и восстанавливает данные менеджера из файла taskStore
     */
    public static FileBackedTasksManager loadFromFile(File taskStore) {

        FileBackedTasksManager fileManager = new FileBackedTasksManager(taskStore);

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(taskStore), StandardCharsets.UTF_8))
        ) {
            String line;
            reader.readLine();
            Set<Integer> idsSubtatsk = new HashSet<>();
            Set<Integer> idsEpic = new HashSet<>();
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    final Task task = CSVTaskFormat.taskFromString(line);
                    final int id = task.getId();
                    if (id > fileManager.generatorId) {
                        fileManager.generatorId = id;
                    }
                    fileManager.tasks.put(id, task);

                    final TaskType taskType = task.getType();
                    if (taskType != TaskType.EPIC) {
                        fileManager.fillingInterval(task);
                    } else {
                        idsEpic.add(id);
                    }
                    if (taskType == TaskType.SUBTASK) {
                        idsSubtatsk.add(task.getId());
                    }
                } else {
                    line = reader.readLine();
                    for (final Integer id : CSVTaskFormat.historyFromString(line)) {
                        if (fileManager.tasks.containsKey(id)) {
                            fileManager.history.add(fileManager.tasks.get(id));
                        }
                    }
                }
            }
            for (Integer idSubtask : idsSubtatsk) {
                Subtask subtask = (Subtask) fileManager.tasks.get(idSubtask);
                final int epicId = subtask.getEpicId();
                Epic basicEpic = fileManager.getEpic(epicId);
                if (basicEpic == null) {
                    continue;
                }
                basicEpic.addSubtaskId(idSubtask);
            }
            idsEpic.forEach(id -> {
                fileManager.updateEpicStatus(id);
                fileManager.updateExecutionTimeEpic(id);
            });
            fileManager.tasks.values().stream().map(Task::getId).forEach(fileManager.sortedTaskIds::add);

            return fileManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read from file: " + taskStore.getName(), e);
        }

    }


    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public Integer createTask(Task newTask) {
        final Integer id = super.createTask(newTask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public Task getTask(int id) {
        final Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Integer createSubtask(Subtask newSubtask) {
        final Integer id = super.createSubtask(newSubtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public Integer createEpic(Epic newEpic) {
        final Integer id = super.createEpic(newEpic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    /**
     * Записывает текущее состояние менеджера в файл
     */
    protected void save() {
        try (
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(taskStore), StandardCharsets.UTF_8))
        ) {
            writer.write(CSVTaskFormat.getHeader());
            writer.newLine();
            for (final Task task : super.tasks.values()) {
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }

            writer.newLine();
            writer.write(CSVTaskFormat.historyToString(super.history));
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: " + taskStore.getName(), e);
        }
    }


    public static void main(String[] args) {
        /*
        Для этого создайте метод static void main(String[] args) в классе FileBackedTasksManager и реализуйте небольшой сценарий:
        Заведите несколько разных задач, эпиков и подзадач.
        Запросите некоторые из них, чтобы заполнилась история просмотра.
        Создайте новый FileBackedTasksManager менеджер из этого же файла.
        Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи, которые были в старом, есть в новом менеджере.
        */
        /*TaskManager taskManager = Managers.getDefault();
        //создидим три задачи
        Integer taskId1 = taskManager.createTask(new Task("Задача 1", "Это \"Задача 1\""));
        Integer taskId2 = taskManager.createTask(new Task("Задача 2", "Это \"Задача 2\""));
        Integer taskId3 = taskManager.createTask(new Task("Задача 3", "Это \"Задача 3\""));
        //создидим два эпика
        Integer epicId1 = taskManager.createEpic(new Epic("Эпик 1", "Это Эпик 1!"));
        Integer epicId2 = taskManager.createEpic(new Epic("Эпик 2", "Это Эпик 2!"));
        //для 1-го эпика создаим 2 подзадачи
        Integer subtaskId1 = taskManager.createSubtask(new Subtask(epicId1, "Подзадача 1", "Подзадача 1 эпика 1"));
        Integer subtaskId2 = taskManager.createSubtask(new Subtask(epicId1, "Подзадача 2", "Подзадача 2 эпика 1"));
        //для 2-го эпика создаим одну подзадачу
        Integer subtaskId3 = taskManager.createSubtask(new Subtask(epicId2, "Подзадача 3", "Подзадача 1 эпика 2"));
        //Поменяем статусы
        Task task2 = taskManager.getTask(taskId1);
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task2);

        Subtask subtask2 = taskManager.getSubtask(subtaskId2);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        //запросим несколько задач
        taskManager.getTask(taskId1);
        taskManager.getTask(taskId2);
        taskManager.getEpic(epicId1);
        taskManager.getSubtask(subtaskId2);
        System.out.println("\nПросмотр истории просмотров задач: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        //получаем новый менеджер задач с хранилищем в том же файле
        final FileBackedTasksManager fileManager = FileBackedTasksManager.
                loadFromFile(new File("./resources/task.csv"));

        System.out.println("\nПросмотр истории просмотров задач из созданного менеджера: ");
        for (Task task : fileManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("\nСписок задач, эпиков, подзадач из СТАРОГО менеджера:");
        System.out.println("Список задач:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("Список подзадач:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nСписок задач, эпиков, подзадач из НОВОГО менеджера:");
        System.out.println("Список задач:");
        for (Task task : fileManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков:");
        for (Epic epic : fileManager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("Список подзадач:");
        for (Subtask subtask : fileManager.getSubtasks()) {
            System.out.println(subtask);
        }
    */

    }

}
