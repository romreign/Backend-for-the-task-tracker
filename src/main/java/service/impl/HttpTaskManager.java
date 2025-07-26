package main.java.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.config.GsonConfig;
import main.java.http.clients.KVTaskClient;
import main.java.models.*;
import main.java.service.interfaces.HistoryManager;

import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        super();
        this.client = new KVTaskClient(url);
        this.gson = GsonConfig.getDefaultGson();
    }

    @Override
    public void save() {
        try {
            client.put("tasks", gson.toJson(getTasks()));
            client.put("epics", gson.toJson(getEpics()));
            client.put("subtasks", gson.toJson(getSubtasks()));
            client.put("history", gson.toJson(history()));

            List<Task> prioritized = new ArrayList<>();
            for (Task task : getPrioritizedTasks()) {
                if (!(task instanceof Epic))
                    prioritized.add(task);
            }
            client.put("prioritized", gson.toJson(prioritized));
            client.put("nextId", gson.toJson(getNextId()));

        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения данных", e);
        }
    }

    @Override
    public void onload() {
        try {
            loadTasks();
            loadEpics();
            loadSubtasks();
            restoreEpicSubtasksRelations();
            loadHistory();
            loadPrioritizedTasks();
            restoreNextId();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки данных", e);
        }
    }

    private void loadTasks() {
        String tasksJson = client.load("tasks");
        if (tasksJson != null && !tasksJson.isEmpty()) {
            Map<Integer, Task> loadedTasks = gson.fromJson(tasksJson, new TypeToken<Map<Integer, Task>>(){}.getType());
            if (loadedTasks != null) {
                this.tasks.clear();
                for (Task task : loadedTasks.values()) {
                    if (task != null) {
                        this.tasks.put(task.getId(), task);
                        if (task.getStartTime() != null)
                            this.prioritizedTasks.add(task);
                    }
                }
            }
        }
    }

    private void loadEpics() {
        String epicsJson = client.load("epics");
        if (epicsJson != null && !epicsJson.isEmpty()) {
            Map<Integer, Epic> loadedEpics = gson.fromJson(epicsJson,
                    new TypeToken<Map<Integer, Epic>>(){}.getType());
            if (loadedEpics != null) {
                this.epics.clear();
                this.epics.putAll(loadedEpics);
            }
        }
    }

    private void loadSubtasks() {
        String subtasksJson = client.load("subtasks");
        if (subtasksJson != null && !subtasksJson.isEmpty()) {
            Map<Integer, Subtask> loadedSubtasks = gson.fromJson(subtasksJson,
                    new TypeToken<Map<Integer, Subtask>>(){}.getType());
            if (loadedSubtasks != null) {
                this.subtasks.clear();
                this.subtasks.putAll(loadedSubtasks);
            }
        }
    }

    private void restoreEpicSubtasksRelations() {
        for (Subtask subtask : subtasks.values()) {
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null) {
                epic.addSubtasks(subtask);
                epic.updateStatus();
                setEpicDateTime(epic.getId());
            }
        }
    }

    private void loadHistory() {
        String historyJson = client.load("history");
        if (historyJson != null && !historyJson.isEmpty()) {
            List<Task> history = gson.fromJson(historyJson,
                    new TypeToken<List<Task>>(){}.getType());
            if (history != null) {
                for (Task task : history) {
                    if (task != null) {
                        if (tasks.containsKey(task.getId())) {
                            historyManager.add(tasks.get(task.getId()));
                        } else if (epics.containsKey(task.getId())) {
                            historyManager.add(epics.get(task.getId()));
                        } else if (subtasks.containsKey(task.getId())) {
                            historyManager.add(subtasks.get(task.getId()));
                        }
                    }
                }
            }
        }
    }

    private void loadPrioritizedTasks() {
        String prioritizedJson = client.load("prioritized");
        if (prioritizedJson != null && !prioritizedJson.isEmpty()) {
            List<Task> prioritized = gson.fromJson(prioritizedJson,
                    new TypeToken<List<Task>>(){}.getType());
            if (prioritized != null) {
                this.prioritizedTasks.clear();
                for (Task task : prioritized) {
                    if (task != null) {
                        if (tasks.containsKey(task.getId()) || subtasks.containsKey(task.getId())) {
                            this.prioritizedTasks.add(task);
                        }
                    }
                }
            }
        }
    }

    private void restoreNextId() {
        String nextIdJson = client.load("nextId");
        if (nextIdJson != null && !nextIdJson.isEmpty()) {
            int loadedNextId = gson.fromJson(nextIdJson, Integer.class);
            if (loadedNextId > getNextId()) {
                setNextId(loadedNextId);
            }
        }
    }
}