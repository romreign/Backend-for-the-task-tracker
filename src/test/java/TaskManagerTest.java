package test.java;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import main.java.exceptions.CollisionTaskException;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;
import main.java.models.TypeTask;
import main.java.service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    abstract void initializationManager();

    @Test
    void history() {
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);

        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 1);
        Task task = new Task("titleTask", "descriptionTask");
        List<Task> checkList = new ArrayList<>();

        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(task);

        checkList.add(epic);
        checkList.add(task);
        checkList.add(subtask2);
        checkList.add(subtask1);

        manager.getEpic(1);
        manager.getTask(4);
        manager.getSubtask(3);
        manager.getSubtask(2);

        List<Task> historyList = manager.history();

        assertEquals(historyList, checkList, "Списки историй различны.");

        manager.getEpic(1);
        historyList = manager.history();
        checkList.removeFirst();
        checkList.add(epic);
        assertEquals(historyList, checkList, "Списки историй различны. Повтор не удален.");

    }

    @Test
    void getTasks() {
        Task task1 = new Task("task1", "description1");
        Task task2 = new Task("task2", "description2");

        manager.create(task1);
        manager.create(task2);

        Map<Integer, Task> TasksMap = manager.getTasks();
        Map<Integer, Task> checkMap = new HashMap<>();
        checkMap.put(task1.getId(), task1);
        checkMap.put(task2.getId(), task2);

        assertEquals(TasksMap, checkMap, "Таблицы различны");
    }

    @Test
    void getTask() {
        int id = 2;
        Task task1 = new Task("task1", "description1");
        Task task2 = new Task("task2", "description2");
        Task task3 = new Task("task3", "description3");

        manager.create(task1);
        manager.create(task2);
        manager.create(task3);

        Task TaskIn = manager.getTask(id);

        assertEquals(TaskIn, task2, "Задачи различны.");
    }

    @Test
    void setTasks() {
        Task task1 = new Task("task1", "description1");
        Task task2 = new Task("task2", "description2");
        Task task3 = new Task("task3", "description3");

        manager.create(task1);
        manager.create(task2);
        Map<Integer, Task> tasks = manager.getTasks();

        Map<Integer, Task> check = new HashMap<>();
        check.put(1, task1);
        check.put(2, task2);
        check.put(3, task3);

        assertNotEquals(tasks, check, "Таблцы равны.");

        manager.setTasks(check);
        tasks = manager.getTasks();

        assertEquals(tasks, check, "Таблицы не равны.");
    }

    @Test
    void getSubtasks() {
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);

        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 1);
        HashMap<Integer, Subtask> check = new HashMap<>();

        manager.create(subtask1);
        manager.create(subtask2);

        check.put(2, subtask1);
        check.put(3, subtask2);

        Map<Integer, Subtask> subtasks = manager.getSubtasks();

        assertEquals(subtasks, check, "Списки не равны.");
    }

    @Test
    void getSubtasksByEpicId() {
        int id = 1;
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic1");
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic2");

        manager.create(epic1);
        manager.create(epic2);

        Subtask subtask1 = new Subtask("subtask1", "description1", 1);
        Subtask subtask2 = new Subtask("subtask2", "description2", 1);
        Subtask subtask3 = new Subtask("subtask3", "description3", 1);

        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        Map<Integer, Subtask> subtasks = manager.getSubtasksByEpicId(id);
        Map<Integer, Subtask> check = new HashMap<>();
        check.put(3, subtask1);
        check.put(4, subtask2);
        check.put(5, subtask3);

        assertEquals(subtasks, check, "Подзадачи различны.");
    }

    @Test
    void getSubtask() {
        int id = 3;
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic1");

        manager.create(epic1);

        Subtask subtask1 = new Subtask("subtask1", "description1", 1);
        Subtask subtask2 = new Subtask("subtask2", "description2", 1);
        Subtask subtask3 = new Subtask("subtask3", "description3", 1);

        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        Subtask subtask = manager.getSubtask(id);


        assertEquals(subtask, subtask2, "Подзадачи различны.");
    }

    @Test
    void setSubtasks() {
        Subtask subtask1 = new Subtask("subtask1", "description1", 0);
        Subtask subtask2 = new Subtask("subtask2", "description2", 0);
        Subtask subtask3 = new Subtask("subtask3", "description3", 0);

        manager.create(subtask1);
        manager.create(subtask2);
        Map<Integer, Subtask> subtasks = manager.getSubtasks();

        Map<Integer, Subtask> check = new HashMap<>();
        check.put(1, subtask1);
        check.put(2, subtask2);
        check.put(3, subtask3);

        assertNotEquals(subtasks, check, "Таблцы равны.");

        manager.setSubtasks(check);
        subtasks = manager.getSubtasks();

        assertEquals(subtasks, check, "Таблицы не равны.");

    }

    @Test
    void getEpics() {
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic");
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic");
        Epic epic3 = new Epic("titleEpic3", "descriptionEpic");
        Map<Integer, Epic> check = new HashMap<>();

        manager.create(epic1);
        manager.create(epic2);
        manager.create(epic3);

        check.put(1, epic1);
        check.put(2, epic2);
        check.put(3, epic3);

        Map<Integer, Epic> epics = manager.getEpics();

        assertEquals(epics, check, "Таблицы не равны.");
    }

    @Test
    void getEpic() {
        int id = 2;
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic");
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic");
        Epic epic3 = new Epic("titleEpic3", "descriptionEpic");

        manager.create(epic1);
        manager.create(epic2);
        manager.create(epic3);

        Epic epic = manager.getEpic(id);

        assertEquals(epic, epic2, "Эпики не равны.");
    }

    @Test
    void setEpics() {
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic", 1);
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic", 2);
        Epic epic3 = new Epic("titleEpic3", "descriptionEpic", 3);
        Map<Integer, Epic> check = new HashMap<>();

        check.put(1, epic1);
        check.put(2, epic2);
        check.put(3, epic3);

        manager.setEpics(check);

        Map<Integer, Epic> epics = manager.getEpics();

        assertEquals(epics, check, "Таблицы не равны.");
    }

    @Test
    void remove() {
        int idTask = 3;
        int idEpic = 1;
        int idSubtask = 4;
        Task task = new Task("title", "description");
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic");
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic");
        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 1);
        Subtask subtask3 = new Subtask("titleSubtask3", "descriptionSubtask3", 2);

        manager.create(epic1);
        manager.create(epic2);
        manager.create(task);
        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        Map<Integer, Task> tasks = manager.getTasks();
        Map<Integer, Subtask> subtasks = manager.getSubtasks();
        Map<Integer, Epic> epics = manager.getEpics();

        assertEquals(1, tasks.size(), "Список задач не содержит нужное количество");
        assertEquals(3, subtasks.size(), "Список подзадач не содержит нужное количество");
        assertEquals(2, epics.size(), "Список эпиков не содержит нужное количество");

        manager.remove(3, TypeTask.TASK);
        tasks = manager.getTasks();
        assertTrue(tasks.isEmpty(), "Список задач не удалил задачу");

        manager.remove(4, TypeTask.SUBTASK);
        subtasks = manager.getSubtasks();
        Epic epic = manager.getEpic(1);
        assertEquals(2, subtasks.size(), "Список подзадач не удалил задачу");
        assertEquals(1, epic.getSubstasks().size(), "Подзадача в эпике не удалилась.");

        manager.remove(1, TypeTask.EPIC);
        epics = manager.getEpics();
        subtasks = manager.getSubtasks();
        assertEquals(1, epics.size(), "Список эпиков не удалил задачу");
        assertEquals(1, subtasks.size(), "Список подзадач не удалил задачу после удаления эпика");
    }

    @Test
    void removeTasksOfType () {
        Task task = new Task("title", "description");
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic");
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic");
        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 1);
        Subtask subtask3 = new Subtask("titleSubtask3", "descriptionSubtask3", 2);

        manager.create(epic1);
        manager.create(epic2);
        manager.create(task);
        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        Map<Integer, Task> tasks = manager.getTasks();
        Map<Integer, Subtask> subtasks = manager.getSubtasks();
        Map<Integer, Epic> epics = manager.getEpics();

        assertFalse(tasks.isEmpty(), "Таблица задач пуста.");
        assertFalse(subtasks.isEmpty(), "Таблица подзадач пуста.");
        assertFalse(epics.isEmpty(), "Таблица эпиков пуста.");

        manager.removeTasksOfType(TypeTask.SUBTASK);

        assertFalse(tasks.isEmpty(), "Таблица задач пуста.");
        assertTrue(subtasks.isEmpty(), "Таблица подзадач не пуста.");
        assertFalse(epics.isEmpty(), "Таблица эпиков пуста.");

        manager.removeTasksOfType(TypeTask.EPIC);

        assertFalse(tasks.isEmpty(), "Таблица задач пуста.");
        assertTrue(epics.isEmpty(), "Таблица эпиков не пуста.");

        manager.removeTasksOfType(TypeTask.TASK);

        assertTrue(tasks.isEmpty(), "Таблица задач не пуста.");
    }

    @Test
    void removeAll () {
        Task task = new Task("title", "description");
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic");
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic");
        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 1);
        Subtask subtask3 = new Subtask("titleSubtask3", "descriptionSubtask3", 2);

        manager.create(epic1);
        manager.create(epic2);
        manager.create(task);
        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        Map<Integer, Task> tasks = manager.getTasks();
        Map<Integer, Subtask> subtasks = manager.getSubtasks();
        Map<Integer, Epic> epics = manager.getEpics();

        assertFalse(tasks.isEmpty(), "Таблица задач пуста.");
        assertFalse(subtasks.isEmpty(), "Таблица подзадач пуста.");
        assertFalse(epics.isEmpty(), "Таблица эпиков пуста.");

        manager.removeAll();

        assertTrue(tasks.isEmpty(), "Таблица задач не пуста.");
        assertTrue(subtasks.isEmpty(), "Таблица подзадач не пуста.");
        assertTrue(epics.isEmpty(), "Таблица эпиков не пуста.");
    }

    @Test
    void create() {
        Task task = new Task("title", "description");
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);
        manager.create(task);

        Subtask subtask = new Subtask("titleSubtask", "descriptionSubtask", 1);

        manager.create(subtask);

        Map<Integer, Task> tasks = manager.getTasks();
        Map<Integer, Subtask> subtasks = manager.getSubtasks();
        Map<Integer, Epic> epics = manager.getEpics();

        assertTrue(tasks.containsValue(task), "Создана другая задача.");
        assertTrue(subtasks.containsValue(subtask), "Создана другая подзадача.");
        assertTrue(epics.containsValue(epic), "Создан другой эпик.");
    }

    @Test
    void update() {
        Task task = new Task("title", "description");
        Task newTask = new Task("newTitle", "newDescription", 2);
        Epic epic = new Epic("titleEpic", "descriptionEpic");
        Epic newEpic = new Epic("newTitleEpic", "newDescriptionEpic", 1);

        manager.create(epic);
        manager.create(task);

        Subtask subtask = new Subtask("titleSubtask", "descriptionSubtask", 1);
        Subtask newSubtask = new Subtask("newTitleSubtask", "newDescriptionSubtask", 1, 3);

        manager.create(subtask);

        manager.update(newTask);
        manager.update(newEpic);
        manager.update(newSubtask);

        Map<Integer, Task> tasks = manager.getTasks();
        Map<Integer, Subtask> subtasks = manager.getSubtasks();
        Map<Integer, Epic> epics = manager.getEpics();

        assertTrue(tasks.containsValue(newTask), "Задача не обновилась.");
        assertTrue(subtasks.containsValue(newSubtask), "Подзадача не обновилась.");
        assertTrue(epics.containsValue(newEpic), "Эпик не обновился.");
    }

    @Test
    void setEpicDateTime() {
        int id = 1;
        LocalDateTime dateTime1 = LocalDateTime.of(2025, 6, 5, 10, 50);
        LocalDateTime dateTime2 = LocalDateTime.of(2025, 6, 13, 10, 50);
        LocalDateTime dateTime3 = LocalDateTime.of(2025, 6, 22, 10, 50);
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);

        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1, dateTime3, 100L);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 1, dateTime1, 200L);
        Subtask subtask3 = new Subtask("titleSubtask3", "descriptionSubtask3", 1, dateTime2, 300L);

        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        manager.setEpicDateTime(id);
        List<Task> check = manager.getPrioritizedTasks();

        assertEquals(3, check.size(), "Список пустой или не содержит нужное количество.");
    }

    @Test
    void getPrioritizedTasks() {
        LocalDateTime dateTime1 = LocalDateTime.of(2025, 6, 5, 10, 50);
        LocalDateTime dateTime2 = LocalDateTime.of(2025, 6, 13, 10, 50);
        LocalDateTime dateTime3 = LocalDateTime.of(2025, 6, 22, 10, 50);
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);

        List<Task> subtasks = new ArrayList<>();
        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1, dateTime3, 100L);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 1, dateTime1, 200L);
        Subtask subtask3 = new Subtask("titleSubtask3", "descriptionSubtask3", 1, dateTime2, 300L);

        subtasks.add(subtask2);
        subtasks.add(subtask3);
        subtasks.add(subtask1);

        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        manager.setEpicDateTime(1);
        List<Task> check = manager.getPrioritizedTasks();

        assertEquals(subtasks, check, "Списки не совпадают.");
    }

    @Test
    void validate() {
        LocalDateTime dateTime1 = LocalDateTime.of(2025, 6, 5, 10, 50);
        LocalDateTime dateTime2 = LocalDateTime.of(2025, 6, 13, 10, 50);
        LocalDateTime dateTime3 = LocalDateTime.of(2025, 6, 22, 10, 50);
        LocalDateTime dateTime4 = LocalDateTime.of(2025, 6, 22, 10, 55);
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);

        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1, dateTime3, 100L);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 1, dateTime1, 200L);
        Subtask subtask3 = new Subtask("titleSubtask3", "descriptionSubtask3", 1, dateTime2, 300L);
        Subtask subtask4 = new Subtask("titleSubtask4", "descriptionSubtask4", 1, dateTime4, 300L);

        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        assertThrows(CollisionTaskException.class, () -> manager.create(subtask4));
    }
}
