package manager;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import manager.history.HistoryManager;
import manager.exceptions.ManagerSaveException;
import manager.utilities.CSVTaskFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File taskStore;

    public FileBackedTasksManager(File taskStore) {
        this.taskStore = taskStore;
    }

    public static void main(String[] args) throws IOException {
        /*
        Для этого создайте метод static void main(String[] args) в классе FileBackedTasksManager и реализуйте небольшой сценарий:
        Заведите несколько разных задач, эпиков и подзадач.
        Запросите некоторые из них, чтобы заполнилась история просмотра.
        Создайте новый FileBackedTasksManager менеджер из этого же файла.
        Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи, которые были в старом, есть в новом менеджере.
        */
        final File file = new File(System.getProperty("user.dir") + File.separator + "tasks.store");
        TaskManager taskManager = new FileBackedTasksManager(file);
        int numberTasks = 10;
        List<Integer> taskIds = new ArrayList<>();
        for (int i = 0; i < numberTasks; i++) {
            taskIds.add(taskManager.createTask(new Task("Задача " + (i + 1), String.format("Это \"Задача %s\"", i + 1))));
        }
        List<Integer> epicIds = new ArrayList<>();
        for (int i = 0; i < numberTasks; i++) {
            epicIds.add(taskManager.createEpic(new Epic("Эпик " + (i + 1), String.format("Это \"Эпик %s\"", i + 1))));
        }

        //получаем новый менеджер задач с хранилищем в том же файле
        final FileBackedTasksManager fileBackedTasksManager0 = FileBackedTasksManager.
                loadFromFile(file);

        System.out.println("\nПросмотр истории просмотров задач из файла1: ");
        for (Task task : fileBackedTasksManager0.getHistory()) {
            System.out.println(task);
        }

        List<Integer> subtaskIds = new ArrayList<>();
        for (int i = 0, count = 1; i < numberTasks; i++) {
            final Integer epicId = epicIds.get(i);
            int number = 1;
            if (epicId % 2 == 0) {
                number = 2;
            } else if (epicId % 3 == 0) {
                number = 3;
            }
            for (; number > 0; number--) {
                subtaskIds.add(taskManager.createSubtask(new Subtask(epicId, "Подзадача " + count, String.format("Подзадача %s эпика %s", count++, epicId - 10))));
            }
        }


        for (int i = 0, size = java.lang.Math.min(taskIds.size(), epicIds.size()); i < size; i++) {
            if (i % 2 == 0) {
                taskManager.getTask(taskIds.get(i));
            } else {
                final Epic epic = taskManager.getEpic(epicIds.get(i));
                for (final Integer id : epic.getSubtaskIds()) {
                    taskManager.getSubtask(id);
                }
            }
        }
        System.out.println("\nПросмотр истории просмотров задач: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        //получаем новый менеджер задач с хранилищем в том же файле
        final FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.
                loadFromFile(file);

        System.out.println("\nПросмотр истории просмотров задач из файла: ");
        for (Task task : fileBackedTasksManager1.getHistory()) {
            System.out.println(task);
        }

    }

    /*public FileBackedTasksManager() throws IOException {
        super();
        final Path path = Paths.get(System.getProperty("user.dir") + File.separator + "tasks.store");

        if (Files.exists(path)) {
            if (Files.readAttributes(path, BasicFileAttributes.class).isDirectory()) {
                this.taskStore = Files.createFile(path).toFile();
            } else {
                this.taskStore = path.toFile();
            }
        } else {
            this.taskStore = Files.createFile(path).toFile();
        }
        loadDataFromFile();
    }*/



   /* public FileBackedTasksManager(File taskStore) throws IOException {
        super();
        final Path path = Paths.get(taskStore.getAbsolutePath());

        if (Files.exists(path)) {
            if (Files.readAttributes(path, BasicFileAttributes.class).isDirectory()) {
                this.taskStore = Files.createFile(path).toFile();
            } else {
                this.taskStore = path.toFile();
            }
        } else {
            this.taskStore = Files.createFile(path).toFile();
        }
        loadDataFromFile();
    }

    */

    /*public File getTaskStore() {
        return taskStore;
    }*/

    /**
     * Создает экземпляр мененджера и восстанавливает данные менеджера из файла file
     */
    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        return new FileBackedTasksManager(file);
    }

    /**
     * получает из менеджера истории задач строку с идентификаторами задач, разделенными ';'
     */
    static String historyToString(HistoryManager history) {
        if (history == null) {
            return "";
        }
        List<String> ids = new ArrayList<>();
        for (final Task task : history.getHistory()) {
            final Integer id = task.getId();
            ids.add(String.valueOf(id));
        }
        return String.join(";", ids);
    }

    /**
     * Получает из строки с идентификаторами разделенными ';' список идентификаторов
     */
    static List<Integer> historyFromString(String value) {
        List<Integer> ids = new ArrayList<>();
        if (value == null || value.isEmpty()) {
            return ids;
        }
        final String[] split = value.split(";");
        for (String str : split) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
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
            for (final Task task : super.tasks.values())  {
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }
            for (final Epic epic : super.epics.values()) {
                writer.write(CSVTaskFormat.toString(epic));
                writer.newLine();
            }
            for (final Subtask subtask : super.subtasks.values()) {
                writer.write(CSVTaskFormat.toString(subtask));
                writer.newLine();
            }
            writer.newLine();
            writer.write(CSVTaskFormat.historyToString(super.history));
            writer.newLine();
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Can't save to file: " + taskStore.getName(), e);
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: " + taskStore.getName(), e);
        }
    }

}

    /**
     * Загружает данные из файла
     */
    protected void loadDataFromFile() {
        try {
            Path path = Paths.get(this.taskStore.getAbsolutePath());
            final List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            final Set<Integer> idsTaskHistory = new HashSet<>();
            for (int i = 0; i < lines.size(); i++) {
                final String line = lines.get(i);
                if (i == 0) {
                    if (line == null || line.isEmpty()) {
                        super.generatorId = 1;
                    } else {
                        super.generatorId = Integer.parseInt(line);
                    }
                    continue;
                }
                if (i == 1) {
                    idsTaskHistory.addAll(historyFromString(line));
                    continue;
                }

                final String[] split = line.split(";");
                if ("TASK".equals(split[1])) {
                    Task task = new Task(line);
                    final Integer id = task.getId();
                    super.tasks.put(id, task);
                    if (idsTaskHistory.contains(id)) {
                        super.history.add(task);
                    }
                } else if ("EPIC".equals(split[1])) {
                    Epic epic = new Epic(line);
                    final Integer id = epic.getId();
                    super.epics.put(id, epic);
                    if (idsTaskHistory.contains(id)) {
                        super.history.add(epic);
                    }
                } else if ("SUBTASK".equals(split[1])) {
                    Subtask subtask = new Subtask(line);
                    final int epicId = subtask.getEpicId();
                    Epic basicEpic = getEpic(epicId);
                    if (basicEpic != null) {
                        final Integer id = subtask.getId();
                        super.subtasks.put(id, subtask);
                        basicEpic.addSubtaskId(id);
                        //super.updateEpicStatus(epicId); это не надо
                        if (idsTaskHistory.contains(id)) {
                            super.history.add(subtask);
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            throw new ManagerSaveException("Can't read from file: " + taskStore.getName(), e);
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read from file: " + taskStore.getName(), e);
        }

    }
}
