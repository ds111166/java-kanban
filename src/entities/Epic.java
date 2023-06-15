package entities;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected List<Integer> subtaskIds;

    public Epic(String title, String description) {
        super(title, description);
        subtaskIds = new ArrayList<>();
    }

    public Epic(List<Integer> subtaskIds, int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String strEic) {
        super(strEic);
        subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }


    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 29 * hash + Objects.hashCode(this.subtaskIds);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Epic other = (Epic) obj;
        if (!this.id.equals(other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        return Objects.equals(this.subtaskIds, other.subtaskIds);
    }


    @Override
    public String toString() {
        return id +
                ";EPIC;\"" +
                name +
                "\";" +
                status +
                ";\"" +
                description +
                "\";";
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
