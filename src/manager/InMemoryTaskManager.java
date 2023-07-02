package manager;

import entities.*;
import manager.history.HistoryManager;
import manager.utilities.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected int generatorId;
    protected final Map<Integer, Task> tasks;
    protected final HistoryManager history;
    protected final TreeSet<Integer> sortedTaskIds;
    protected final TreeSet<LocalDateTime> busyTimeUnits;

    public InMemoryTaskManager() {
        this.generatorId = 0;
        this.tasks = new HashMap<>();
        this.history = Managers.getDefaultHistory();
        this.sortedTaskIds = new TreeSet<>((id1, id2) -> {
            Task task1 = tasks.get(id1);
            Task task2 = tasks.get(id2);
            LocalDateTime startTime1 = task1.getStartTime();
            LocalDateTime startTime2 = task1.getStartTime();
            if (startTime1 == null && startTime2 == null) {
                return Integer.compare(task1.getId(), task2.getId());
            } else if (startTime1 == null) {
                return -1;
            } else if (startTime2 == null) {
                return 1;
            } else if (startTime1.isBefore(startTime2)) {
                return 1;
            } else if (startTime1.isAfter(startTime2)) {
                return -1;
            } else {
                return 0;
            }
        });
        this.busyTimeUnits = new TreeSet<>((dt1, dt2) -> {
            if (dt1.isBefore(dt1)) {
                return 1;
            } else if (dt1.isAfter(dt2)) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
        sortedTaskIds.clear();
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public Integer createTask(Task newTask) {
        if (newTask == null || !isTheTaskTimingValid(newTask) ) {
            return null;
        }
            int id = ++generatorId;
            tasks.put(id, newTask);
            newTask.setId(id);
            sortedTaskIds.add(id);
            return id;
    }

    @Override
    public void updateTask(Task updatedTask) {
        final int id = updatedTask.getId();
        final Task savedTask = tasks.get(id);

        if (savedTask == null || !isTheTaskTimingValid(savedTask)) {
            return;
        }
        tasks.put(id, updatedTask);
        sortedTaskIds.add(id);
    }

    @Override
    public void deleteTask(int id) {
        sortedTaskIds.remove(id);
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return tasks.values()
                .stream().filter(task -> task.getType() == TaskType.SUBTASK)
                .map(task -> (Subtask) task)
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
                    updateEpicStatus(id);
                    sortedTaskIds.remove(id);
                    break;
                case SUBTASK:
                    iterator.remove();
                    sortedTaskIds.remove(id);
                    break;
            }
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = (Subtask) tasks.get(id);
        history.add(subtask);
        return subtask;
    }

    @Override
    public Integer createSubtask(Subtask newSubtask) {
        if (newSubtask == null || !isTheTaskTimingValid(newSubtask)) {
            return null;
        }
        final int epicId = newSubtask.getEpicId();
        Epic basicEpic = getEpic(epicId);
        if (basicEpic == null) {
            return null;
        }

        int id = ++generatorId;
        tasks.put(id, newSubtask);
        newSubtask.setId(id);
        basicEpic.addSubtaskId(newSubtask.getId());
        updateEpicStatus(epicId);
        sortedTaskIds.add(id);
        sortedTaskIds.add(epicId);
        return id;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        final int id = updatedSubtask.getId();
        final int epicId = updatedSubtask.getEpicId();
        final Subtask savedSubtask = (Subtask) tasks.get(id);
        if (savedSubtask == null || !isTheTaskTimingValid(updatedSubtask)) {
            return;
        }
        final Epic epic = (Epic) tasks.get(epicId);
        if (epic == null) {
            return;
        }
        tasks.put(id, updatedSubtask);
        updateEpicStatus(epicId);
        sortedTaskIds.add(id);
        sortedTaskIds.add(epicId);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = (Subtask) tasks.remove(id);
        if (subtask == null) {
            return;
        }
        history.remove(id);
        sortedTaskIds.remove(id);
        Epic epic = (Epic) tasks.get(subtask.getEpicId());
        epic.removeSubtask(id);
        Integer epicId = epic.getId();
        updateEpicStatus(epicId);
        sortedTaskIds.add(epicId);
    }

    @Override
    public List<Epic> getEpics() {
        return tasks.values()
                .stream().filter(task -> task.getType() == TaskType.EPIC)
                .map(task -> (Epic) task)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEpics() {
        Iterator<Integer> iterator = tasks.keySet().iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            if (tasks.get(id).getType() != TaskType.TASK) {
                iterator.remove();
                sortedTaskIds.remove(id);
            }
        }
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = (Epic) tasks.get(id);
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
        tasks.put(id, newEpic);
        sortedTaskIds.add(id);
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            Integer epicId = epic.getId();
            final Epic savedEpic = (Epic) tasks.get(epicId);
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            sortedTaskIds.add(epicId);
        }
    }

    @Override
    public void deleteEpic(int id) {
        final Epic epic = (Epic) tasks.remove(id);
        history.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            tasks.remove(subtaskId);
            history.remove(subtaskId);
            sortedTaskIds.remove(subtaskId);
        }
        sortedTaskIds.remove(id);
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> resSubtasks = new ArrayList<>();
        Epic epic = (Epic) tasks.get(epicId);
        if (epic == null) {
            return null;
        }
        for (int id : epic.getSubtaskIds()) {
            resSubtasks.add((Subtask) tasks.get(id));
        }
        return resSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }


    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> collect = sortedTaskIds
                .stream()
                .map(id -> tasks.get(id))
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * Устанавливает статус эпика в соответствии расчитанным по статусам подзадач эпика
     *
     * @param epicId идентификатор эпика
     */
    protected void updateEpicStatus(int epicId) {
        final Epic epic = (Epic) tasks.get(epicId);
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

    private boolean isTheTaskTimingValid(Task task) {
        LocalDateTime startTime = task.getStartTime();
        if (startTime == null) {
            return true;
        }
        startTime = LocalDateTime.of(startTime.getYear()
                , startTime.getMonth()
                , startTime.getDayOfMonth()
                , startTime.getHour()
                , startTime.getMinute());

        if (busyTimeUnits.isEmpty() || busyTimeUnits.last().isBefore(startTime)) {
            for (int m = 0; m < task.getDuration(); m++) {
                LocalDateTime ldt = startTime.plusMinutes(m);
                busyTimeUnits.add(ldt);
            }
            LocalDateTime now = LocalDateTime.now();
            Iterator<LocalDateTime> iterator = busyTimeUnits.iterator();
            while (iterator.hasNext()) {
                LocalDateTime ldt = iterator.next();
                Duration duration = Duration.between(ldt, now);
                if (duration.toDays() > 365) {
                    iterator.remove();
                } else {
                    break;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    protected boolean isIntersection(Task task, LocalDateTime startTime){
        for (int m = 0; m < task.getDuration(); m++) {

            busyTimeUnits.add(ldt);
        }
    }

}
