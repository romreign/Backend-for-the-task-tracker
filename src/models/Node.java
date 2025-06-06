package models;

import java.util.Objects;

public class Node<T> {
    private T data;
    private Node<T> prev;
    private Node<T> next;

    public Node(T data, Node<T> prev, Node<T> next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }

    public Node(T data, Node<T> prev) {
        this(data, prev, null);
    }

    public Node(T data) {
        this(data, null, null);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
       this.next = next;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        Node<T> node = (Node<T>) obj;
        return Objects.equals(data, node.data) && Objects.equals(prev, node.prev) && Objects.equals(next, node.next);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        int prime = 31;

        hash = (data != null) ? hash * prime : hash;
        hash = (prev != null) ? hash * prime : hash;
        hash = (next != null) ? hash * prime : hash;

        return hash;
    }

    @Override
    public String toString() {
        return "Node {" + data.toString() + "}\n";
    }
}
