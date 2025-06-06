package service;

import models.Epic;
import models.Subtask;
import models.Task;
import models.TypeTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TaskManager {

    List<Task> history();

    Map<Integer, Task> getTasks();

    Task getTask(int id);

    void setTasks(HashMap<Integer, Task> tasks);

    Map<Integer, Subtask> getSubtasks();

    String toString();

    Map<Integer, Subtask> getSubtasksByEpicId(int id);

    Task getSubtask(int id);

    void setSubtasks(HashMap<Integer, Subtask> subtasks);

    Map<Integer, Epic> getEpics();

    Task getEpic(int id);

    void setEpics(HashMap<Integer, Epic> epics);

    void remove(int id, TypeTask typeTask);

    void removeAll(TypeTask typeTask);

    boolean create(Object object);

    boolean update(Object object);
}
