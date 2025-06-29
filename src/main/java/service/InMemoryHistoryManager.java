package main.java.service;

import java.util.*;

import main.java.dataStructure.Node;
import main.java.models.Task;

public class InMemoryHistoryManager implements HistoryManager{

    private CustomLinkedList history;

     static private class CustomLinkedList {
        private final Map<Integer, Node<Task>> mapTasks;
        private Node<Task> tail;
        private Node<Task> head;

        public CustomLinkedList() {
            mapTasks = new HashMap<>();
            tail = null;
            head = null;
        }

        public void removeNode(int id) {
            Node<Task> hideTask = mapTasks.get(id);
            if (hideTask == null) {
                System.out.println("Задача с таким идентификатором не найдена");
                return;
            }
            removeNode(hideTask);
        }

        public void removeNode(Node<Task> nodeTask) {
            Node<Task> prev = nodeTask.getPrev();
            Node<Task> next = nodeTask.getNext();

            if (prev == null)
                head = next;
            else {
                prev.setNext(next);
                nodeTask.setPrev(null);
            }

            if (next == null)
                tail = prev;
            else {
                next.setPrev(prev);
                nodeTask.setNext(null);
            }
            mapTasks.remove(nodeTask.getData().getId());
            nodeTask.setData(null);
        }

        public List<Task> getTasks() {
            List<Task> listHistory = new ArrayList<>();

            for (Node<Task> curr = head; curr != null; curr = curr.getNext())
                listHistory.add(curr.getData());

            return listHistory;
        }

        public void linkLast(Task task) {
            if (task == null)
                return;

            int idTask = task.getId();
            if (mapTasks.containsKey(idTask))
                removeNode(idTask);

            Node<Task> newNode = new Node<>(task, tail, null);

            if (head == null)
                head = newNode;

            if (tail != null)
                tail.setNext(newNode);
            tail = newNode;

            mapTasks.put(idTask, newNode);
        }

    }

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
    }

    @Override
    public void remove(int id) {
        history.removeNode(id);
    }

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return Collections.unmodifiableList(history.getTasks());
    }
}
