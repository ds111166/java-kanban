package manager;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import history.HistoryManager;
import manager.exceptions.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    protected String taskStore;

    public FileBackedTasksManager() {
        new FileBackedTasksManager("tasks.store");
    }

    public static void main(String[] args) {
        /*
        Для этого создайте метод static void main(String[] args) в классе FileBackedTasksManager и реализуйте небольшой сценарий:
        Заведите несколько разных задач, эпиков и подзадач.
        Запросите некоторые из них, чтобы заполнилась история просмотра.
        Создайте новый FileBackedTasksManager менеджер из этого же файла.
        Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи, которые были в старом, есть в новом менеджере.
        */
    }

    public FileBackedTasksManager(String taskStore) {
        super();
        this.taskStore = System.getProperty("user.dir") + File.separator + taskStore;
        loadDataFromFile();
    }

    /**
     * Создает экземпляр мененджера и восстанавливает данные менеджера из файла file
     */
    public static FileBackedTasksManager loadFromFile(File file) {
        return new FileBackedTasksManager(file.getAbsolutePath());
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
        if (value != null) {
            final String[] split = value.split(";");
            for (String str : split) {
                ids.add(Integer.valueOf(str));
            }
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
        try {
            final List<String> contentToWrite = new ArrayList<>();

            contentToWrite.add(String.valueOf(super.generatorId));
            contentToWrite.add(historyToString(super.history));

            for (final Task task : super.tasks.values()) {
                contentToWrite.add(task.toString());
            }
            for (final Epic epic : super.epics.values()) {
                contentToWrite.add(epic.toString());
            }
            for (final Subtask subtask : super.subtasks.values()) {
                contentToWrite.add(subtask.toString());
            }
            final Path tempTaskStore = Files.createTempFile("task_store_", null);
            Files.write(tempTaskStore, contentToWrite, StandardCharsets.UTF_8);
            Files.move(tempTaskStore, Paths.get(this.taskStore), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    /**
     * Загружает данные из файла
     */
    protected void loadDataFromFile() {
        try {
            Path path = Paths.get(this.taskStore);
            if (!Files.exists(path)) {
                return;
            }
            final List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            final Set<Integer> idsTaskHistory = new HashSet<>();
            for (int i = 0; i < lines.size(); i++) {
                final String line = lines.get(i);
                if (i == 0) {
                    super.generatorId = Integer.parseInt(line);
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
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
