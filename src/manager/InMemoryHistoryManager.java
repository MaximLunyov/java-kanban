package manager;

import tasks.Task;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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

    private final HashMap<Integer, Node> receivedTasks;
    public Node head;
    public Node tail;

    public InMemoryHistoryManager() {
        this.receivedTasks = new HashMap<>();
    }

    public void linkLast(Task task) {
        final Node node = new Node(tail, task,  null);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
            final Task data = node.data;
            final Node next = node.next;
            final Node prev = node.prev;
            node.data = null;

            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node && !(tail == node)) {
                head = next;
                head.prev = null;
            } else if (!(head == node) && tail == node) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }



    @Override
    public void add(Task task) {
        /*if (node == null) { //Приветствую, Патимат! Заменил блок if, как ты указала в ревью,
            return;           //но блок кода в таком случае не выполняется, IDEA подсказывает,
        }                     //что не может распознать node. Я не понимаю, как реализовать проверку.
        remove(task.getId()); //Подскажи пожалуйста, как правильно это сделать?
        linkLast(task);
        receivedTasks.put(task.getId(), tail);*/

        if (!(task == null)) {
            remove(task.getId());
            linkLast(task);
            receivedTasks.put(task.getId(), tail);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(receivedTasks.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void removeAllHistory() {
        for (Integer ids : receivedTasks.keySet()) {
            remove(ids);
        }

    }
}