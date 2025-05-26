package service;

import models.Epic;
import models.Subtask;
import models.Task;
import models.TypeTask;

import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;
    private int nextId;

    private int generateId() {
        return nextId++;
    }

    public Manager() {
        tasks = new HashMap<Integer, Task>();
        subtasks = new HashMap<Integer, Subtask>();
        epics = new HashMap<Integer, Epic>();
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public Task getTask(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задача с идентификатором - " + id + " не найдена");
            return null;
        }
        return tasks.get(id);
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Manager {"
                + "tasks.size=" + tasks.size()
                + ", subtasks.size=" + subtasks.size()
                + ", epics.size=" + epics.size()
                + "}\n";
    }

    public Task getSubtask(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Подзадача с идентификатором - " + id + " не найдена");
            return null;
        }
        return subtasks.get(id);
    }

    public HashMap<Integer, Subtask> getSubtasksByEpicId(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с идентификатором - " + id + " не найден");
            return null;
        }
        return epics.get(id).getSubstasks();
    }

    public void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public Task getEpic(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с идентификатором - " + id + " не найден");
            return null;
        }
        return epics.get(id);
    }

    public void setEpics(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    public void remove(int id, TypeTask typeTask) {
        if (typeTask == TypeTask.TASK) {
            if (!tasks.containsKey(id)) {
                System.out.println("Задача с идентификатором - " + id + " не найдена");
                return;
            }
            tasks.remove(id);
            System.out.println("Удалена задача с идентификатором - " + id);
        }
        else if (typeTask == TypeTask.SUBTASK) {
            if (!subtasks.containsKey(id)) {
                System.out.println("Подзадача с идентификатором - " + id + " не найдена");
                return;
            }
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null) {
                epic.getSubstasks().remove(subtask.getId());
                epic.updateStatus();
            }
            subtasks.remove(id);
            System.out.println("Удалена подзадача с идентификатором - " + id);
        }
        else if (typeTask == TypeTask.EPIC) {
            if (!epics.containsKey(id)) {
                System.out.println("Эпик с идентификатором - " + id + " не найден");
                return;
            }
            Epic epic = epics.get(id);
            for(Subtask subtask : epic.getSubstasks().values()) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
            System.out.println("Удален эпик с идентификатором - " + id);
        }
    }

    public void removeAll(TypeTask typeTask) {
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

            for (Epic epic : epics.values()) {
                for (Subtask subtask : epic.getSubstasks().values()) {
                    subtasks.remove(subtask.getId());
                }
            }
            epics.clear();
            System.out.printf("Удалено эпиков: %d \n", epicCount);
        }
    }

    public boolean create(Object object) {
        if (object == null)
            return false;

        if (object.getClass() == Task.class) {
            Task task = (Task) object;
            task.setId(generateId());
            tasks.put(task.getId(), task);
        }
        else if (object.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) object;
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null)
                epic.addSubtasks(subtask);
        }
        else if (object.getClass() == Epic.class) {
            Epic epic = (Epic) object;
            epic.setId(generateId());
            epics.put(epic.getId(), epic);
        }
        return true;
    }

    public boolean update(Object object) {
        if (object == null)
            return false;

        if (object.getClass() == Task.class) {
            Task task = (Task) object;
            tasks.put(task.getId(), task);
        }
        else if (object.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) object;
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null)
                epic.updateStatus();
        }
        else if (object.getClass() == Epic.class) {
            Epic epic = (Epic) object;
            epic.updateStatus();
            epics.put(epic.getId(), epic);
        }
        return true;
    }
}