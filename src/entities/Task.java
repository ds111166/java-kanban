package entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    protected TaskType type;

    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;
    protected long duration;
    protected LocalDateTime startTime;


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.type = getClassType();
    }

    public Task(String name, String description, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.type = getClassType();
    }

    public Task(String name, String description, Status status, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.type = getClassType();
    }

    public Task(Integer id, String name, String description, Status status, int duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.type = getClassType();
    }


    public Task(Task another) {
        this.id = another.id;
        this.name = another.name;
        this.type = another.getType();
        this.description = another.description;
        this.status = another.status;
        this.duration = another.duration;
        this.startTime = another.startTime;

    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (duration != task.duration) return false;
        if (!Objects.equals(id, task.id)) return false;
        if (!Objects.equals(name, task.name)) return false;
        if (!Objects.equals(description, task.description)) return false;
        if (status != task.status) return false;
        return Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plusMinutes(duration);
    }

    private TaskType getClassType() {
        if (this.getClass().equals(Epic.class)) {
            return TaskType.EPIC;
        } else if (this.getClass().equals(Subtask.class)) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }
}

