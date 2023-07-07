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
                    if (taskType == TaskType.EPIC) {
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
            for (Integer id : idsEpic) {
                fileManager.updateEpic(id);
            }
            fileManager.tasks.values().stream().map(Task::getId).forEach(fileManager.prioritizedTasks::add);

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

}
