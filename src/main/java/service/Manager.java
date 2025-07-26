package main.java.service;

import main.java.service.impl.FileBackedTasksManager;
import main.java.service.impl.HttpTaskManager;
import main.java.service.impl.InMemoryHistoryManager;
import main.java.service.impl.InMemoryTaskManager;
import main.java.service.interfaces.TaskManager;


public class Manager {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getFileBacked(String fileName) {
        return new FileBackedTasksManager(fileName, getDefaultHistory());
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static HttpTaskManager getDefaultHttpManager(String url) {
        return new HttpTaskManager(url);
    }

}







