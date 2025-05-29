package service;

import model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * Указатель на первый элемент списка. Он же first
     */
    private Node head;

    /**
     * Указатель на последний элемент списка. Он же last
     */
    private Node tail;
    private int size = 0;
    private final Map<Integer, Node> historyTasks = new HashMap<>();

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
        size++;
    }

    public Node getLast() {
        final Node curTail = tail;

        if (curTail == null)
            throw new NoSuchElementException();

        return tail;
    }

    public int getSize() {
        return this.size;
    }

    public List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node curNode = head;

        while (curNode != null) {
            tasksList.add(curNode.data);
            curNode = curNode.next;
        }

        return tasksList;
    }

    public void removeNode(Node node) {
        if (node != null) {
            Node prev = node.prev;
            Node next = node.next;

            if (next == null) { // если это хвост, у предыдущего удаляем next, и он становится хвостом
                if (prev != null) {
                    prev.next = null;
                    tail = prev;
                } else {
                    tail = null;
                    head = null;
                }
            } else if (prev == null) { // если это голова, у следующего удаляем prev, и он становится головой
                next.prev = null;
                head = next;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (historyTasks.containsKey(task.getId())) {
            remove(task.getId());
        }

        linkLast(task);
        historyTasks.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        if (historyTasks.containsKey(id)) {
            removeNode(historyTasks.get(id));
            historyTasks.remove(id);
        }
    }
}
