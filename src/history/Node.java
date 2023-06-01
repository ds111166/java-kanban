package history;

import entities.Task;

import java.util.Objects;

/**
 * Класс узла двунаправленного списка
 */
public class Node {

    private Task data;
    private Node prev;
    private Node next;


    public Node(Task data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public Node(Task data, Node prev, Node next) {
        this.prev = prev;
        this.data = data;
        this.next = next;

    }

    public Task getData() {
        return data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.data);
        hash = 89 * hash + Objects.hashCode(this.next);
        hash = 89 * hash + Objects.hashCode(this.prev);
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
        final Node other = (Node) obj;
        if (!Objects.equals(this.data, other.data)) {
            return false;
        }
        if (!Objects.equals(this.next, other.next)) {
            return false;
        }
        if (!Objects.equals(this.prev, other.prev)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Node{" + "data=" + data + '}';
    }
}
