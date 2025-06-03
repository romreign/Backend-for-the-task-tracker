package service;

import models.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private LinkedList<Task> listHistory;
    private static int LIST_SIZE = 10;

    public InMemoryHistoryManager() {
        listHistory = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        listHistory.add(task);
        if (listHistory.size() == (LIST_SIZE + 1))
            listHistory.remove(0);
    }

    @Override
    public List<Task> getHistory() {
        return listHistory;
    }
}
