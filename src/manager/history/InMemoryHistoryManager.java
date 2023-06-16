package manager.history;

import entities.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node first;
    private Node last;
    private final Map<Integer, Node> history;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    @Override
    public void add(Task task) {

        if (task == null) {
            return;
        }
        final int id = task.getId();
        if (history.containsKey(id)) {
            final Node node = history.remove(id);
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {

        final Node node = history.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    /**
     * Добавляет задачу task в конец списка
     */
    private void linkLast(Task task) {

        final Node node = new Node(task, last, null);
        if (first == null) {
            first = node;
        } else {
            last.setNext(node);
        }
        last = node;
        history.put(task.getId(), node);
    }

    /**
     * Удаляет узел node из списка
     */
    private void removeNode(Node node) {

        if (node == null) {
            return;
        }
        Node nodePrev = node.getPrev();
        Node nodeNext = node.getNext();
        if (nodePrev != null) {
            nodePrev.setNext(nodeNext);
        } else {
            first = nodeNext;
        }
        if (nodeNext != null) {
            nodeNext.setPrev(nodePrev);
        } else {
            last = nodePrev;
        }
    }

    /**
     * Возвращает список задач в истории
     */
    private List<Task> getTasks() {

        List<Task> tasks = new ArrayList<>();
        Node nextNode = first;
        while (nextNode != null) {
            final Task task = nextNode.getData();
            if (task != null) {
                tasks.add(task);
            }
            nextNode = nextNode.getNext();
        }
        return tasks;
    }
}
