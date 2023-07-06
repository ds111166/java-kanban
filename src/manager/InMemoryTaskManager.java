package manager;

import entities.*;
import manager.exceptions.TaskValidationException;
import manager.history.HistoryManager;
import manager.utilities.CSVTaskFormat;
import manager.utilities.Managers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static manager.utilities.CSVTaskFormat.cloneTask;

public class InMemoryTaskManager implements TaskManager {

    protected int generatorId;
    protected final Map<Integer, Task> tasks;
    protected final HistoryManager history;
    protected final TreeSet<Integer> prioritizedTasks;
    protected final Map<LocalDateTime, Integer> busyTimeUnits;

    public InMemoryTaskManager() {
        this.generatorId = 0;
        this.tasks = new HashMap<>();
        this.history = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(TASK_COMPARATOR);
        this.busyTimeUnits = new HashMap<>();
    }

    @Override
    public List<Task> getTasks() {
        return tasks.values()
                .stream()
                .filter(task -> task.getType() == TaskType.TASK)
                .map(CSVTaskFormat::cloneTask)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTasks() {
        Iterator<Integer> iterator = tasks.keySet().iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            Task task = tasks.get(id);
            if (TaskType.TASK == task.getType()) {
                clearingInterval(task);
                prioritizedTasks.remove(id);
                iterator.remove();

            }
        }
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        history.add(task);
        return cloneTask(task);
    }

    @Override
    public Integer createTask(Task createdTask) {
        if (createdTask == null || !isTaskTimingValid(createdTask)) {
            return null;
        }
        int id = ++generatorId;
        Task newTask = cloneTask(createdTask);
        tasks.put(id, newTask);
        newTask.setId(id);
        prioritizedTasks.add(id);
        fillingInterval(newTask);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        final int updatedTaskId = task.getId();
        final Task savedTask = tasks.get(updatedTaskId);

        if (savedTask == null || !isTaskTimingValid(task)) {
            return;
        }
        Task updatedTask = cloneTask(task);
        tasks.put(updatedTaskId, updatedTask);
        prioritizedTasks.add(updatedTaskId);
        clearingInterval(savedTask);
        fillingInterval(updatedTask);
    }

    @Override
    public void deleteTask(int deletedTaskId) {
        final Task deletedTask = tasks.get(deletedTaskId);
        if (deletedTask != null) {
            clearingInterval(deletedTask);
        }
        prioritizedTasks.remove(deletedTaskId);
        tasks.remove(deletedTaskId);
        history.remove(deletedTaskId);

    }

    @Override
    public List<Subtask> getSubtasks() {
        return tasks.values()
                .stream().filter(task -> task.getType() == TaskType.SUBTASK)
                .map(task -> (Subtask) cloneTask(task))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSubtasks() {
        Iterator<Integer> iterator = tasks.keySet().iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            Task task = tasks.get(id);
            switch (task.getType()) {
                case EPIC:
                    Epic epic = (Epic) task;
                    epic.cleanSubtaskIds();
                    updateEpic(id);
                    prioritizedTasks.add(id);
                    break;
                case SUBTASK:
                    clearingInterval(task);
                    prioritizedTasks.remove(id);
                    iterator.remove();
                    break;
            }
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = (Subtask) tasks.get(id);
        history.add(subtask);
        return (Subtask) cloneTask(subtask);
    }

    @Override
    public Integer createSubtask(Subtask createdSubtask) {
        if (createdSubtask == null || !isTaskTimingValid(createdSubtask)) {
            return null;
        }
        final int epicId = createdSubtask.getEpicId();
        Epic basicEpic = (Epic) tasks.get(epicId);
        if (basicEpic == null) {
            return null;
        }

        int id = ++generatorId;
        Subtask newSubtask = (Subtask) cloneTask(createdSubtask);
        tasks.put(id, newSubtask);
        newSubtask.setId(id);
        basicEpic.addSubtaskId(newSubtask.getId());
        updateEpic(epicId);
        prioritizedTasks.add(id);
        prioritizedTasks.add(epicId);
        fillingInterval(newSubtask);
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final int updatedSubtaskId = subtask.getId();
        final int epicId = subtask.getEpicId();
        final Subtask savedSubtask = (Subtask) tasks.get(updatedSubtaskId);
        if (savedSubtask == null || !isTaskTimingValid(subtask)) {
            return;
        }
        final Epic epic = (Epic) tasks.get(epicId);
        if (epic == null) {
            return;
        }
        Subtask updatedSubtask = (Subtask) cloneTask(subtask);
        tasks.put(updatedSubtaskId, updatedSubtask);
        updateEpic(epicId);
        prioritizedTasks.add(updatedSubtaskId);
        prioritizedTasks.add(epicId);
        clearingInterval(savedSubtask);
        fillingInterval(updatedSubtask);
    }

    @Override
    public void deleteSubtask(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        prioritizedTasks.remove(id);
        Subtask subtask = (Subtask) tasks.remove(id);
        clearingInterval(subtask);
        history.remove(id);
        Epic epic = (Epic) tasks.get(subtask.getEpicId());
        epic.removeSubtask(id);
        Integer epicId = epic.getId();
        updateEpic(epicId);
        prioritizedTasks.add(epicId);
    }

    @Override
    public List<Epic> getEpics() {
        return tasks.values()
                .stream().filter(task -> task.getType() == TaskType.EPIC)
                .map(task -> (Epic) cloneTask(task))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEpics() {
        Iterator<Integer> iterator = tasks.keySet().iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            final Task task = tasks.get(id);
            final TaskType taskType = task.getType();
            if (taskType != TaskType.TASK) {
                if (taskType == TaskType.SUBTASK) {
                    clearingInterval(task);
                }
                prioritizedTasks.remove(id);
                iterator.remove();
            }
        }
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = (Epic) tasks.get(id);
        history.add(epic);
        return (Epic) cloneTask(epic);
    }

    @Override
    public Integer createEpic(Epic createdEpic) {
        if (createdEpic == null) {
            return null;
        }
        int epicId = ++generatorId;
        Epic newEpic = (Epic) cloneTask(createdEpic);
        newEpic.setId(epicId);
        tasks.put(epicId, newEpic);
        updateEpic(epicId);
        //updateEpicStatus(epicId);
        prioritizedTasks.add(epicId);
        return epicId;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        final Integer updatedEpicId = epic.getId();
        final Epic savedEpic = (Epic) tasks.get(updatedEpicId);
        if (savedEpic == null) {
            return;
        }
        Epic updatedEpic = (Epic) cloneTask(epic);
        tasks.put(updatedEpicId, updatedEpic);
        updateEpic(updatedEpicId);
        prioritizedTasks.add(updatedEpicId);
    }

    @Override
    public void deleteEpic(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        prioritizedTasks.remove(id);
        final Epic epic = (Epic) tasks.remove(id);
        history.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            final Task task = tasks.get(subtaskId);
            if (task != null) {
                this.clearingInterval(task);
            }
            prioritizedTasks.remove(subtaskId);
            tasks.remove(subtaskId);
            history.remove(subtaskId);

        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> resSubtasks = new ArrayList<>();
        Epic epic = (Epic) tasks.get(epicId);
        if (epic == null) {
            return null;
        }
        for (int id : epic.getSubtaskIds()) {
            resSubtasks.add((Subtask) cloneTask(tasks.get(id)));
        }
        return resSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }


    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks
                .stream()
                .map(id -> cloneTask(tasks.get(id)))
                .collect(Collectors.toList());
    }

    /**
     * Устанавливает статус эпика в соответствии расчитанным по статусам подзадач эпика
     *
     */
    protected void updateEpicStatus(Epic epic) {
        //final Epic epic = (Epic) tasks.get(epicId);
        int countNew = 0;
        int countDone = 0;
        int count = 0;

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds != null) {
            for (Integer id : subtaskIds) {
                count++;
                final Subtask subtask = (Subtask) tasks.get(id);
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
     * Расчет времени начала, конца и прдолжительность выполненя эпика
     * в соответствии с показателями подзадач
     */
    protected void updateExecutionTimeEpic(Epic epic) {
        //final Epic epic = (Epic) tasks.get(epicId);
        int duration = 0;
        LocalDateTime endTime = null;
        LocalDateTime startTime = null;
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds != null) {
            for (Integer id : subtaskIds) {
                final Subtask subtask = (Subtask) tasks.get(id);
                if (subtask == null) {
                    continue;
                }
                final long durationSubtask = subtask.getDuration();
                final LocalDateTime startTimeSubtask = subtask.getStartTime();
                final LocalDateTime endTimeSubtask = (startTimeSubtask == null)
                        ? null
                        : startTimeSubtask.plusMinutes(durationSubtask);
                duration += durationSubtask;
                if (startTime == null || (startTimeSubtask != null && startTime.isAfter(startTimeSubtask))) {
                    startTime = startTimeSubtask;
                }
                if (endTime == null || (endTimeSubtask != null && endTime.isBefore(endTimeSubtask))) {
                    endTime = endTimeSubtask;
                }
            }
            epic.setDuration(duration);
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
        }
    }

    protected void updateEpic(int epicId) {
        final Epic epic = (Epic) tasks.get(epicId);
        updateEpicStatus(epic);
        updateExecutionTimeEpic(epic);
    }

    /**
     * проверяет валидность задачи по времени начала выполнения и продолжительности
     * Если интервал выполнения задачи перескается с интервалами выполнения др задач
     * возвращает FALSE и возвращает TRUE в противном случае
     */
    protected boolean isTaskTimingValid(Task task) {
        LocalDateTime startTime = task.getStartTime();
        if (startTime == null) {
            return true;
        }
        LocalDateTime endTime = startTime.plusMinutes(task.getDuration());
        final Integer taskId = task.getId();
        Integer taskIdStartTime = busyTimeUnits.get(startTime);
        Integer taskIdEndTime = busyTimeUnits.get(endTime);

        if (taskIdStartTime != null && !taskIdStartTime.equals(taskId)) {
            final Task t = tasks.get(taskIdStartTime);
            throw new TaskValidationException("Задача пересекается с id="
                    + t.getId() + " c " + t.getStartTime() + " по " + t.getEndTime());
        }
        if (taskIdEndTime != null && !taskIdEndTime.equals(taskId)) {
            final Task t = tasks.get(taskIdEndTime);
            throw new TaskValidationException("Задача пересекается с id="
                    + t.getId() + " c " + t.getStartTime() + " по " + t.getEndTime());
        }
        return true;
    }

    /**
     * Заполнение интервала, сответствующего интервалу времени выполнения задачи
     */
    protected void fillingInterval(Task task) {
        if (task == null) {
            return;
        }
        final Integer taskId = task.getId();
        if (taskId == null) {
            return;
        }
        LocalDateTime startTime = task.getStartTime();
        if (startTime == null) {
            return;
        }
        startTime = LocalDateTime.of(startTime.getYear()
                , startTime.getMonth()
                , startTime.getDayOfMonth()
                , startTime.getHour()
                , startTime.getMinute());
        final long duration = task.getDuration();
        for (int m = 0; m < duration + 1; m++) {
            busyTimeUnits.put(startTime.plusMinutes(m), taskId);
        }
    }

    /**
     * Очистка интервала, сответствующего интервалу времени выполнения задачи
     */
    protected void clearingInterval(Task task) {
        if (task == null) {
            return;
        }

        LocalDateTime startTime = task.getStartTime();
        if (startTime == null) {
            return;
        }
        startTime = LocalDateTime.of(startTime.getYear()
                , startTime.getMonth()
                , startTime.getDayOfMonth()
                , startTime.getHour()
                , startTime.getMinute());
        final long duration = task.getDuration();
        for (int m = 0; m < duration + 1; m++) {
            busyTimeUnits.remove(startTime.plusMinutes(m));
        }
    }

    private final Comparator<Integer> TASK_COMPARATOR = new Comparator<>() {
        @Override
        public int compare(Integer id1, Integer id2) {
            if (id1 == null || id2== null) {
                return 0;
            }
            Task task1 = tasks.get(id1);
            Task task2 = tasks.get(id2);
            if (task1 == null || task2 == null) {
                return 0;
            }
            LocalDateTime startTime1 = task1.getStartTime();
            if (startTime1 == null) {
                startTime1 = LocalDateTime.MAX;
            }
            LocalDateTime startTime2 = task2.getStartTime();
            if (startTime2 == null) {
                startTime2 = LocalDateTime.MAX;
            }
            final int result = startTime1.compareTo(startTime2);
            return (result == 0) ? Integer.compare(id1, id2) : result;
        }
    };

}
