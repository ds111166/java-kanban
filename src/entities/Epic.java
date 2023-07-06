package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtaskIds;
    protected LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, 0, null);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description, Status status, int duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(Epic another) {
        super(another);
        this.endTime = another.endTime;
        this.subtaskIds = new ArrayList<>();
        this.subtaskIds.addAll(another.subtaskIds);
    }


    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;

        Epic epic = (Epic) o;

        if (!subtaskIds.equals(epic.subtaskIds)) return false;
        return Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + subtaskIds.hashCode();
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "endTime=" + endTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    /**
     * Очищает список идентификаторов подзадач
     */
    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    /**
     * добавляет идентификтор подзадачи в список подзадач эпика
     *
     * @param id - идентификтор подзадач
     */
    public void addSubtaskId(Integer id) {
        if (!subtaskIds.contains(id)) {
            subtaskIds.add(id);
        }
    }

    /**
     * удаляет идентификтор подзадачи в список подзадач эпика
     *
     * @param id - идентификтор подзадач
     */
    public void removeSubtask(Integer id) {
        subtaskIds.remove(id);
    }


}
