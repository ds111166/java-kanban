package entities;

import java.util.Objects;

/**
 * @author ds111166
 */
public class Subtask extends Task {
    protected int idEpic;

    public Subtask(int idEpic, String title, String description) {
        super(title, description);
        this.idEpic = idEpic;
    }

    public Subtask(int idEpic, int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + this.idEpic;
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
        final Subtask other = (Subtask) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        if (this.idEpic != other.idEpic) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SubTask{" + "id=" + id + ", title=" + title + ", description=" + description + ", status=" + status + ", idEpic=" + idEpic + '}';
    }
}
