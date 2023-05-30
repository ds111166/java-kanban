package history;

import entities.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    static class CustomLinkedList {
        private final Node head;
        private final Node tail;
        private final Map<Integer, Node> idToNodeMap;

        public CustomLinkedList() {
            this.idToNodeMap = new HashMap<>();
            this.head = new Node(null);
            this.tail = new Node(null);
            this.head.setNext(this.tail);
            this.tail.setPrev(this.head);
        }

        /**
         * Добавляет задачу task в список
         */
        public void add(Task task) {
            if (task == null) return;
            final Integer id = task.getId();
            Node node = idToNodeMap.get(id);
            if (node != null) {
                removeNode(node);
            }
            linkLast(task);
        }

        /**
         * Удаляет задачу с идентификатором int id из списка
         */
        public void remove(int id) {
            Node node = idToNodeMap.get(id);
            if (node == null) return;
            removeNode(node);
            idToNodeMap.remove(id);
        }

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node nextNode = this.head.getNext();
            while (nextNode != null ) {
                final Task task = nextNode.getData();
                tasks.add(task);
                nextNode = nextNode.getNext();
            }
            return tasks;
        }


        @Override
        public String toString() {
            return "CustomLinkedList{" +
                    "head=" + head +
                    ", tail=" + tail +
                    ", idToNodeMap=" + idToNodeMap +
                    '}';
        }

        /**
         * Добавляет задачу task в конец списка
         */
        private void linkLast(Task task) {
            final Node prev = tail.getPrev();
            final Node node = new Node(task);
            node.setPrev(prev);
            prev.setNext(node);
            node.setNext(tail);
            tail.setPrev(node);
            idToNodeMap.put(task.getId(), node);
        }

        /**
         * Удаляет узел node из списка
         */
        private void removeNode(Node node) {
            if (node == null) return;
            final Node nodePrev = node.getPrev();
            final Node nodeNext = node.getNext();
            if(nodePrev !=null){
                nodePrev.setNext(nodeNext);
            }
            if(nodeNext != null){
                nodeNext.setPrev(nodePrev);
            }
        }
    }

    private final CustomLinkedList history;

    public InMemoryHistoryManager() {
        this.history = new CustomLinkedList();
    }

    @Override
    public void add(Task task) {
        history.add(task);
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }
}
