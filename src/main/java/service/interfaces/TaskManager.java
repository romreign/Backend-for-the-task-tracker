package main.java.service.interfaces;

import main.java.exceptions.CollisionTaskException;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;
import main.java.models.TypeTask;

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

    Subtask getSubtask(int id);

    void setSubtasks(Map<Integer, Subtask> subtasks);

    Map<Integer, Epic> getEpics();

    Epic getEpic(int id);

    void setEpics(Map<Integer, Epic> epics);

    boolean remove(int id, TypeTask typeTask);

    void removeTasksOfType (TypeTask typeTask);

    void removeAll ();

    boolean create(Task inTask);

    boolean update(Task inTask);

    void setEpicDateTime(int id);

    List<Task> getPrioritizedTasks();

    void validate(Task newTask) throws CollisionTaskException;

    int getNextId();
}
