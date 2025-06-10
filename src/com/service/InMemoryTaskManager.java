package com.service;

import com.models.repository.Epic;
import com.models.repository.Subtask;
import com.models.repository.Task;
import com.models.repository.TypeTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager{
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Subtask> subtasks;
    protected Map<Integer, Epic> epics;
    protected int nextId;
    protected HistoryManager historyManager;

    protected int generateId() {
        return nextId++;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public int getNextId() {
        return nextId;
    }

    public InMemoryTaskManager() {
        tasks = new HashMap<Integer, Task>();
        subtasks = new HashMap<Integer, Subtask>();
        epics = new HashMap<Integer, Epic>();
        historyManager = new InMemoryHistoryManager();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<Integer, Task>();
        subtasks = new HashMap<Integer, Subtask>();
        epics = new HashMap<Integer, Epic>();
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Task getTask(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задача с идентификатором - " + id + " не найдена");
            return null;
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void setTasks(Map<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager {"
                + "tasks.size=" + tasks.size()
                + ", subtasks.size=" + subtasks.size()
                + ", epics.size=" + epics.size()
                + "}\n";
    }

    @Override
    public Task getSubtask(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Подзадача с идентификатором - " + id + " не найдена");
            return null;
        }
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Map<Integer, Subtask> getSubtasksByEpicId(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с идентификатором - " + id + " не найден");
            return null;
        }
        return epics.get(id).getSubstasks();
    }

    @Override
    public void setSubtasks(Map<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Task getEpic(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с идентификатором - " + id + " не найден");
            return null;
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void setEpics(Map<Integer, Epic> epics) {
        this.epics = epics;
    }

    @Override
    public boolean remove(int id, TypeTask typeTask) {
        if (typeTask == TypeTask.TASK) {
            if (!tasks.containsKey(id)) {
                System.out.println("Задача с идентификатором - " + id + " не найдена");
                return false;
            }

            tasks.remove(id);
            historyManager.remove(id);
            System.out.println("Удалена задача с идентификатором - " + id);
        }
        else if (typeTask == TypeTask.SUBTASK) {
            if (!subtasks.containsKey(id)) {
                System.out.println("Подзадача с идентификатором - " + id + " не найдена");
                return false;
            }

            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getIdEpic());

            if (epic != null) {
                epic.getSubstasks().remove(subtask.getId());
                epic.updateStatus();
            }
            subtasks.remove(id);
            historyManager.remove(id);
            System.out.println("Удалена подзадача с идентификатором - " + id);
        }
        else if (typeTask == TypeTask.EPIC) {
            if (!epics.containsKey(id)) {
                System.out.println("Эпик с идентификатором - " + id + " не найден");
                return false;
            }

            Epic epic = epics.get(id);

            for(Subtask subtask : epic.getSubstasks().values())
                subtasks.remove(subtask.getId());

            epics.remove(id);
            historyManager.remove(id);
            System.out.println("Удален эпик с идентификатором - " + id);
        }

        return true;
    }

    @Override
    public void removeTasksOfType (TypeTask typeTask) {
        if (typeTask == TypeTask.TASK) {
            int taskCount = tasks.size();
            tasks.clear();
            System.out.printf("Удалено задач: %d \n", taskCount);
        }
        else if (typeTask == TypeTask.SUBTASK) {
            int subtaskCount = subtasks.size();

            for (Epic epic : epics.values()) {
                epic.getSubstasks().clear();
                epic.updateStatus();
            }
            subtasks.clear();
            System.out.printf("Удалено подзадач: %d \n", subtaskCount);
        }
        else if (typeTask == TypeTask.EPIC) {
            int epicCount = epics.size();

            for (Epic epic : epics.values())
                for (Subtask subtask : epic.getSubstasks().values())
                    subtasks.remove(subtask.getId());

            epics.clear();
            System.out.printf("Удалено эпиков: %d \n", epicCount);
        }
    }

    @Override
    public void removeAll () {
        removeTasksOfType(TypeTask.TASK);
        removeTasksOfType(TypeTask.EPIC);
        removeTasksOfType(TypeTask.SUBTASK);
    }

    @Override
    public boolean create(Task inTask) {
        if (inTask == null)
            return false;

        if (inTask.getClass() == Task.class) {
            inTask.setId(generateId());
            tasks.put(inTask.getId(), inTask);
        }
        else if (inTask.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) inTask;
            int newId = generateId();
            subtask.setId(newId);
            subtasks.put(newId, subtask);
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null)
                epic.addSubtasks(subtask);
        }
        else if (inTask.getClass() == Epic.class) {
            Epic epic = (Epic) inTask;
            epic.setId(generateId());
            epics.put(epic.getId(), epic);
        }

        return true;
    }

    @Override
    public boolean update(Task inTask) {
        if (inTask == null)
            return false;

        if (inTask.getClass() == Task.class)
            tasks.put(inTask.getId(), inTask);
        else if (inTask.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) inTask;
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getIdEpic());

            if (epic != null)
                epic.updateStatus();
        }
        else if (inTask.getClass() == Epic.class) {
            Epic epic = (Epic) inTask;
            epic.updateStatus();
            epics.put(epic.getId(), epic);
        }

        return true;
    }
}