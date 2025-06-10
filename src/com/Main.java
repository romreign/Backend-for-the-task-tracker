package com;

import com.models.repository.*;
import com.service.InMemoryTaskManager;
import com.service.Manager;
import com.service.TaskManager;

import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

            String file = "data.txt";
         //   testManagerUtilityClass();
       //    testTaskCreationAndDeletion();
        //    testEpicAndSubtaskStatusFlow();
        //    testHistoryManagerFunctionality();
          // testFileBackedTasksManagerWithoutHistory(file);
          testFileBackedTasksManager(file);
            //testOnload(file);

    }

    private static void testOnload(String fileName) {
        TaskManager manager = Manager.getFileBacked(fileName);

        System.out.println(manager);

        Task subtask = new Subtask("title", "description", 4, StatusTask.IN_PROGRESS, 2);
        manager.update(subtask);


        System.out.println(manager);
    }

    private static void testFileBackedTasksManagerWithoutHistory(String fileName) {
        TaskManager manager = Manager.getFileBacked(fileName);

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        manager.create(task1);
        manager.create(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        manager.create(epic1);
        manager.create(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId());

        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

    }

    private static void testFileBackedTasksManager(String fileName) {
        TaskManager manager = Manager.getFileBacked(fileName);

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        manager.create(task1);
        manager.create(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        manager.create(epic1);
        manager.create(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId());


        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());
        manager.getTask(task2.getId());
        manager.getSubtask(subtask2.getId());
        manager.getEpic(epic2.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask3.getId());

    }

    private static void testManagerUtilityClass() {
        System.out.println("\n=== Проверка класса Manager ===");
        TaskManager manager = Manager.getDefault();

        Task task = new Task("Task", "Description", StatusTask.NEW);
        manager.create(task);

        Task retrievedTask = manager.getTask(task.getId());
        if (retrievedTask != null)
            System.out.println("Задача успешно добавлена: " + retrievedTask.getTitle());
        else
            System.out.println("Ошибка: Задача не найдена.");


        if (manager.history().contains(task))
            System.out.println("История содержит задачу: " + task.getTitle());
        else
            System.out.println("Ошибка: История не содержит задачу.");

    }

    private static void testTaskCreationAndDeletion() {
        System.out.println("\n=== Создание и удаление задач ===");
        TaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Task 1", "Description", StatusTask.NEW);
        manager.create(task);
        System.out.println("Создана задача: " + task);

        Task retrieved = manager.getTask(task.getId());
        assertEqual(task, retrieved, "Задача должна быть найдена");

        manager.remove(task.getId(), TypeTask.TASK);
        assertNull(manager.getTask(task.getId()), "Задача должна быть удалена");
    }

    private static void testEpicAndSubtaskStatusFlow() {
        System.out.println("\n=== Проверка статусов эпика ===");
        TaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description", StatusTask.NEW);
        manager.create(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", StatusTask.NEW, epic.getId());
        manager.create(subtask);

        assertEqual(StatusTask.NEW, epic.getStatus(), "Эпик должен быть NEW");

        subtask.setStatus(StatusTask.DONE);
        manager.update(subtask);
        assertEqual(StatusTask.DONE, epic.getStatus(), "Эпик должен быть DONE");
    }

    private static void testHistoryManagerFunctionality() {
        System.out.println("\n=== Тестирование менеджера истории ===");
        TaskManager manager = Manager.getDefault();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        manager.create(task1);
        manager.create(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        manager.create(epic1);
        manager.create(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId());

        manager.create(subtask1);
        manager.create(subtask2);
        manager.create(subtask3);

        System.out.println("\nФормируем историю просмотров:");
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());
        manager.getTask(task2.getId());
        manager.getSubtask(subtask2.getId());
        manager.getEpic(epic2.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask3.getId());

        printHistory(manager.history());


        System.out.println("\nУдаляем задачу 1:");
        manager.remove(task1.getId(), TypeTask.TASK);
        printHistory(manager.history());


        System.out.println("\nУдаляем эпик 1 (с подзадачами):");
        manager.remove(epic1.getId(), TypeTask.EPIC);
        printHistory(manager.history());


        System.out.println("\nФинальное состояние истории:");
        printHistory(manager.history());
    }

    private static void printHistory(List<Task> history) {
        if (history.isEmpty()) {
            System.out.println("История пуста.");
            return;
        }

        System.out.println("Текущая история (" + history.size() + " элементов):");
        for (Task task : history) {
            String type = task instanceof Epic ? "Эпик" : task instanceof Subtask ? "Подзадача" : "Задача";
            System.out.printf("- %s: %s (ID: %d)\n", type, task.getTitle(), task.getId());
        }
    }

    private static void assertEqual(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual))
            throw new AssertionError(message + ". Ожидалось: " + expected + ", получено: " + actual);

        System.out.println("[OK] " + message);
    }

    private static void assertNull(Object object, String message) {
        if (object != null)
            throw new AssertionError(message);

        System.out.println("[OK] " + message);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition)
            throw new AssertionError(message);

        System.out.println("[OK] " + message);
    }
}