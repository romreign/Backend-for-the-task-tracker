package main.java.service.impl;

import main.java.exceptions.CollisionTaskException;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;
import main.java.models.TypeTask;
import main.java.service.interfaces.HistoryManager;
import main.java.service.interfaces.TaskManager;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Subtask> subtasks;
    protected Map<Integer, Epic> epics;
    protected int nextId = 1;
    protected HistoryManager historyManager;

    protected static final Comparator<Task> TASKS_COMPORATOR = new Comparator<>() {
        @Override
        public int compare(Task task1, Task task2) {
            Comparator<Task> startTimeComparator = Comparator.comparing(
                    Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );

            int timeComparison = startTimeComparator.compare(task1, task2);
            if (timeComparison != 0) {
                return timeComparison;
            }

            return Comparator.nullsLast(Comparator.comparingInt(Task::getId)).compare(task1, task2);
        }
    };
    protected TreeSet<Task> prioritizedTasks= new TreeSet<>(TASKS_COMPORATOR);

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
    public Subtask getSubtask(int id) {
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
    public Epic getEpic(int id) {
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
            prioritizedTasks.remove(tasks.get(id));
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
                setEpicDateTime(epic.getId());
            }
            prioritizedTasks.remove(subtask);
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

            for (Integer idTask : tasks.keySet())
                prioritizedTasks.remove(tasks.get(idTask));

            tasks.clear();
            System.out.printf("Удалено задач: %d \n", taskCount);
        }
        else if (typeTask == TypeTask.SUBTASK) {
            int subtaskCount = subtasks.size();

            for (Integer idSubtask : subtasks.keySet())
                prioritizedTasks.remove(subtasks.get(idSubtask));

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
                for (Subtask subtask : epic.getSubstasks().values()) {
                    prioritizedTasks.remove(subtask);
                    subtasks.remove(subtask.getId());
                }

            epics.clear();
            System.out.printf("Удалено эпиков: %d \n", epicCount);
        }
    }

    @Override
    public void removeAll () {
        removeTasksOfType(TypeTask.TASK);
        removeTasksOfType(TypeTask.EPIC);
        removeTasksOfType(TypeTask.SUBTASK);
        prioritizedTasks.clear();
    }

    @Override
    public boolean create(Task inTask) {
        if (inTask == null)
            return false;

        if (inTask.getClass() == Task.class) {
            validate(inTask);
            inTask.setId(generateId());
            tasks.put(inTask.getId(), inTask);
            prioritizedTasks.add(inTask);
        }
        else if (inTask.getClass() == Subtask.class) {
            validate(inTask);
            Subtask subtask = (Subtask) inTask;
            int newId = generateId();
            subtask.setId(newId);
            subtasks.put(newId, subtask);
            prioritizedTasks.add(subtask);
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null) {
                epic.addSubtasks(subtask);
                epic.updateStatus();
                setEpicDateTime(epic.getId());
            }
        }
        else if (inTask.getClass() == Epic.class) {
            Epic epic = (Epic) inTask;
            epic.setId(generateId());
            setEpicDateTime(epic.getId());
            epics.put(epic.getId(), epic);
        }

        return true;
    }

    @Override
    public boolean update(Task inTask) {
        if (inTask == null)
            return false;

        if (inTask.getClass() == Task.class) {
            validate(inTask);
            prioritizedTasks.remove(tasks.get(inTask.getId()));
            tasks.put(inTask.getId(), inTask);
            prioritizedTasks.add(inTask);
        }
        else if (inTask.getClass() == Subtask.class) {
            validate(inTask);
            Subtask subtask = (Subtask) inTask;
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            Epic epic = epics.get(subtask.getIdEpic());

            if (epic != null) {
                epic.updateStatus();
                setEpicDateTime(epic.getId());
            }
        }
        else if (inTask.getClass() == Epic.class) {
            Epic epic = (Epic) inTask;
            epic.updateStatus();
            setEpicDateTime(epic.getId());
            epics.put(epic.getId(), epic);
        }

        return true;
    }

    @Override
    public void setEpicDateTime(int id) {
        Epic epic = epics.get(id);
        if (epic == null)
            return;

        List<Subtask> subtasks = new ArrayList<>(epic.getSubstasks().values());
        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(0L);
            epic.setEndTime(null);
        }

        long duration = 0L;

        for (Subtask subtask : subtasks) {
            LocalDateTime tmpStartTime = subtask.getStartTime();
            LocalDateTime tmpEndTime = subtask.getEndTime();
            duration += subtask.getDuration();

            if (tmpStartTime != null && (epic.getStartTime() == null
                    || subtask.getStartTime().isBefore(epic.getStartTime())))
                epic.setStartTime(tmpStartTime);

            if (tmpEndTime != null && (epic.getEndTime() == null
                    || subtask.getEndTime().isAfter(epic.getEndTime())))
                epic.setEndTime(tmpEndTime);
        }

        epic.setDuration(duration);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void validate(Task newTask) {
        if (newTask.getStartTime() == null)
            return;

        List<Task> prioritizedTasksList = getPrioritizedTasks();
        LocalDateTime startTime = newTask.getStartTime();
        LocalDateTime endTime = newTask.getEndTime();

        for (Task task : prioritizedTasksList) {
            if (task.getStartTime() == null)
                return;

            if (!(task.getStartTime().isAfter(endTime)
                    || task.getEndTime().isBefore(startTime)))
                throw new CollisionTaskException("Время выполнения пересекается с уже существующей задачей.");
        }
    }
}