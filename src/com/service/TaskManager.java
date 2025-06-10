package com.service;

import com.models.repository.Epic;
import com.models.repository.Subtask;
import com.models.repository.Task;
import com.models.repository.TypeTask;

import java.util.List;
import java.util.Map;

public interface TaskManager {

    List<Task> history();

    Map<Integer, Task> getTasks();

    Task getTask(int id);

    void setTasks(Map<Integer, Task> tasks);

    Map<Integer, Subtask> getSubtasks();

    String toString();

    Map<Integer, Subtask> getSubtasksByEpicId(int id);

    Task getSubtask(int id);

    void setSubtasks(Map<Integer, Subtask> subtasks);

    Map<Integer, Epic> getEpics();

    Task getEpic(int id);

    void setEpics(Map<Integer, Epic> epics);

    boolean remove(int id, TypeTask typeTask);

    void removeTasksOfType (TypeTask typeTask);

    public void removeAll ();

    boolean create(Task inTask);

    boolean update(Task inTask);
}
