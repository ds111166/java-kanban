package entities;

import java.time.LocalDateTime;

/**
 * @author ds111166
 */
public class Subtask extends Task {
    protected int epicId;

    public Subtask(int epicId, String title, String description) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(int epicId, String title, String description, int duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int epicId, int id, String title, String description, Status status, int duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(Subtask another) {
        super(another);
        this.epicId = another.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;

        Subtask subtask = (Subtask) o;

        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + epicId;
        return result;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

}
