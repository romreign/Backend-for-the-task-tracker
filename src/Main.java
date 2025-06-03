import models.*;
import service.InMemoryTaskManager;
import service.Manager;
import service.TaskManager;

import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        testTaskCreationAndDeletion();
        testEpicAndSubtaskStatusFlow();
        testHistoryManagerFunctionality();
        testManagerUtilityClass();
    }

    private static void testTaskCreationAndDeletion() {
        System.out.println("\n=== Тест 1: Создание и удаление задач ===");
        TaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Task 1", "Description", Status.NEW);
        manager.create(task);
        System.out.println("Создана задача: " + task);

        Task retrieved = manager.getTask(task.getId());
        assertEqual(task, retrieved, "Задача должна быть найдена");

        manager.remove(task.getId(), TypeTask.TASK);
        assertNull(manager.getTask(task.getId()), "Задача должна быть удалена");
    }

    private static void testEpicAndSubtaskStatusFlow() {
        System.out.println("\n=== Тест 2: Проверка статусов эпика ===");
        TaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description", Status.NEW);
        manager.create(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId());
        manager.create(subtask);

        assertEqual(Status.NEW, epic.getStatus(), "Эпик должен быть NEW");

        subtask.setStatus(Status.DONE);
        manager.update(subtask);
        assertEqual(Status.DONE, epic.getStatus(), "Эпик должен быть DONE");
    }

    private static void testHistoryManagerFunctionality() {
        System.out.println("\n=== Тест 3: Проверка истории просмотров ===");
        TaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description", Status.NEW);
        Task task2 = new Task("Task 2", "Description", Status.NEW);
        manager.create(task1);
        manager.create(task2);

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());

        List<Task> history = manager.history();
        assertEqual(2, history.size(), "История должна содержать 2 задачи");
        assertEqual(task1, history.get(0), "Первая задача в истории не совпадает");
        assertEqual(task2, history.get(1), "Вторая задача в истории не совпадает");

        for (int i = 0; i < 15; i++) {
            manager.getTask(task1.getId());
        }
        assertEqual(10, manager.history().size(), "История должна ограничиваться 10 элементами");
    }

    private static void testManagerUtilityClass() {
        System.out.println("\n=== Проверка класса Manager ===");
        TaskManager manager = Manager.getDefault();

        Task task = new Task("Task", "Description", Status.NEW);
        manager.create(task);

        Task retrievedTask = manager.getTask(task.getId());
        if (retrievedTask != null) {
            System.out.println("Задача успешно добавлена: " + retrievedTask.getTitle());
        } else {
            System.out.println("Ошибка: Задача не найдена.");
        }

        if (manager.history().contains(task)) {
            System.out.println("История содержит задачу: " + task.getTitle());
        } else {
            System.out.println("Ошибка: История не содержит задачу.");
        }
    }

    private static void assertEqual(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + ". Ожидалось: " + expected + ", получено: " + actual);
        }
        System.out.println("[OK] " + message);
    }

    private static void assertNull(Object object, String message) {
        if (object != null) {
            throw new AssertionError(message);
        }
        System.out.println("[OK] " + message);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        System.out.println("[OK] " + message);
    }
}