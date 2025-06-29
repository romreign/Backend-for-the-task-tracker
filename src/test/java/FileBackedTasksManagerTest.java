package test.java;

import main.java.models.Task;
import main.java.models.Subtask;
import main.java.models.Epic;
import main.java.service.FileBackedTasksManager;
import main.java.service.Manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{
    private final static String somePath = "src" + File.separator + "test" + File.separator
                                                 + "resources" + File.separator + "data.txt";

    @Override
    void initializationManager() {
        this.manager = (FileBackedTasksManager)(Manager.getFileBacked(somePath));
    }

    @BeforeEach
    void beforeEach() {
        initializationManager();
    }

    @AfterEach
    void afterEach() {
        manager.delete();
    }

    @Test
    void SaveAndLoadEmptyTaskList() {
        manager.createFile();
        manager.getEpic(1);
        manager.getTask(2);

        List<Task> historyList = manager.history();
        assertTrue(historyList.isEmpty(), "Список истории не пуст");

        FileBackedTasksManager newManager = (FileBackedTasksManager) (Manager.getFileBacked(somePath));
        newManager.onload();

        List<Task> newHistoryList = newManager.history();
        Map<Integer, Task> newTasks = newManager.getTasks();
        Map<Integer, Subtask> newSubtasks = newManager.getSubtasks();
        Map<Integer, Epic> newEpics = newManager.getEpics();

        assertTrue(newTasks.isEmpty(), "Список задач не пуст.");
        assertTrue(newSubtasks.isEmpty(), "Список плодзадач не пуст.");
        assertTrue(newEpics.isEmpty(), "Список эпиков не пуст.");
        assertTrue(newHistoryList.isEmpty(), "Список новой истории не пуст");
    }

    @Test
    void SaveAndLoadEpicWithoutSubtasks() {
        manager.createFile();
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic");
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic");

        manager.create(epic1);
        manager.create(epic2);

        manager.getEpic(1);
        manager.getEpic(2);

        List<Task> historyList = manager.history();
        assertFalse(historyList.isEmpty(), "Список истории пуст");

        FileBackedTasksManager newManager = (FileBackedTasksManager) (Manager.getFileBacked(somePath));
        newManager.onload();

        List<Task> newHistoryList = newManager.history();
        Map<Integer, Epic> newEpics = newManager.getEpics();

        assertFalse(newEpics.isEmpty(), "Список эпиков пуст.");
        assertFalse(newHistoryList.isEmpty(), "Список истории пуст");
    }

    @Test
    void SaveAndLoadEmptyListHistory() {
        manager.createFile();
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

        List<Task> historyList = manager.history();
        assertTrue(historyList.isEmpty(), "Список истории не пуст");

        FileBackedTasksManager newManager = (FileBackedTasksManager) (Manager.getFileBacked(somePath));
        newManager.onload();


        List<Task> newHistoryList = newManager.history();
        Map<Integer, Task> newTasks = newManager.getTasks();
        Map<Integer, Subtask> newSubtasks = newManager.getSubtasks();
        Map<Integer, Epic> newEpics = newManager.getEpics();

        assertFalse(newTasks.isEmpty(), "Список задач пуст.");
        assertFalse(newSubtasks.isEmpty(), "Список подзадач пуст.");
        assertFalse(newEpics.isEmpty(), "Список эпиков пуст.");
        assertTrue(newHistoryList.isEmpty(), "Список истории не пуст");
    }
}
