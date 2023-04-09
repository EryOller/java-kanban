package service;

import java.util.Objects;

import static service.InMemoryHistoryManager.head;
import static service.InMemoryHistoryManager.tail;

public class Node <Task> {
    private Node<Task> nextElement;
    private Node<Task> previusElement;
    private Task currentElement;

    public Node(Node<Task> previusElement, Task currentElement, Node<Task> nextElement) {
        this.previusElement = previusElement;
        this.currentElement = currentElement;
        this.nextElement = nextElement;
    }

    public Node<Task> getNextElement() {
        return nextElement;
    }

    public void setNextElement(Node<Task> nextElement) {
        this.nextElement = nextElement;
    }

    public Node<Task> getPreviusElement() {
        return previusElement;
    }

    public void setPreviusElement(Node<Task> previusElement) {
        this.previusElement = previusElement;
    }

    public Task getCurrentElement() {
        return currentElement;
    }

    public void setCurrentElement(Task currentElement) {
        this.currentElement = currentElement;
    }

    public static Node removeNode(Node<model.Task> node) {
        if (head.equals(node) && tail.equals(node)) {
            head = null;
            tail = null;
            return node;
        }
        if (!head.equals(node) && !tail.equals(node)) {
            node.getPreviusElement().setNextElement(node.getNextElement());
            node.getNextElement().setPreviusElement(node.getPreviusElement());
            node.setNextElement(null);
            node.setPreviusElement(null);
            return node;
        }
        if (head.equals(node)) {
                node.getNextElement().setPreviusElement(null);
                head = node.getNextElement();
                node.setNextElement(null);
            return node;
        }
        if (tail.equals(node)) {
            node.getPreviusElement().setNextElement(null);
            tail = node.getPreviusElement();
            node.setPreviusElement(null);
            return node;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(nextElement, node.nextElement) && Objects.equals(previusElement, node.previusElement)
                && Objects.equals(currentElement, node.currentElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nextElement, previusElement, currentElement);
    }

    @Override
    public String toString() {
        return "Node{" +
                "nextElement=" + nextElement +
                ", previusElement=" + previusElement +
                ", currentElement=" + currentElement +
                '}';
    }
}
