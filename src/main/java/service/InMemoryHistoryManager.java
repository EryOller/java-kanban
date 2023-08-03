package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static service.Node.removeNode;

public class InMemoryHistoryManager implements HistoryManager {
    private static Map<Integer, Node<Task>> listHistory = new HashMap<>();
    public static Node<Task> head = null;
    public static Node<Task> tail = null;

    private Node<Task> linkLast(Task task) {
        Node<Task> lastNode;
        if (!listHistory.isEmpty()) {
            lastNode = new Node<>(tail, task, null);
            tail.setNextElement(lastNode);
            tail = lastNode;
            listHistory.put(task.getId(), lastNode);
        } else {
            lastNode = new Node<>(null, task, null);
            listHistory.put(task.getId(), lastNode);
            head = lastNode;
            tail = lastNode;
        }
        return lastNode;
    }

    private List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        if (head != null && tail != null) {
            getNextTask(head, list);
        }
        return list;
    }

    private void getNextTask(Node<Task> node, List<Task> list) {
        list.add(node.getCurrentElement());
        if (!tail.equals(node)) {
            getNextTask(node.getNextElement(),list);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (listHistory.containsKey(id)) {
            Node<Task> node = listHistory.remove(id);
            removeNode(node);
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (listHistory.get(task.getId()) != null && listHistory.containsKey(task.getId())) {
                removeNode(listHistory.get(task.getId()));
                listHistory.remove(task.getId());
            }
            Node<Task> node = linkLast(task);
            listHistory.put(task.getId(), node);
        }
    }

    public static void clearHistory() {
        listHistory.clear();
        head = null;
        tail = null;
    }
}
