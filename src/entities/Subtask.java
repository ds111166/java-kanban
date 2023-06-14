package entities;

/**
 * @author ds111166
 */
public class Subtask extends Task {
    protected int epicId;

    public Subtask(int epicId, String title, String description) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(int epicId, int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String strSubtask) {
        super(strSubtask);
        if(strSubtask != null) {
            final String[] split = strSubtask.split(";");
            if(split.length > 5)
            this.epicId = Integer.parseInt(split[5]);
        }
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + this.epicId;
        return hash;
    }

    @Override
    public String toString() {
        return "SubTask{" + "id=" + id + ", name=" + name + ", description=" + description + ", status=" + status + ", epicId=" + epicId + '}';
    }
}
