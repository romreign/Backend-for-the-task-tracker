package service;

import models.Epic;
import models.Subtask;
import models.Task;
import models.TypeTask;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    List<Task> history();

    HashMap<Integer, Task> getTasks();

    Task getTask(int id);

    void setTasks(HashMap<Integer, Task> tasks);

    HashMap<Integer, Subtask> getSubtasks();

    String toString();

    HashMap<Integer, Subtask> getSubtasksByEpicId(int id);

    Task getSubtask(int id);

    void setSubtasks(HashMap<Integer, Subtask> subtasks);

    HashMap<Integer, Epic> getEpics();

    Task getEpic(int id);

    void setEpics(HashMap<Integer, Epic> epics);

    void remove(int id, TypeTask typeTask);

    void removeAll(TypeTask typeTask);

    boolean create(Object object);

    boolean update(Object object);
}
