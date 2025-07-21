package test.java;

import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;
import main.java.service.impl.InMemoryHistoryManager;
import main.java.service.interfaces.HistoryManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class HistoryManagerTest {
    private HistoryManager manager;

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void add() {
        List<Task> tasks = new ArrayList<>();
        Task task = new Task("title", "description", 4);
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic", 3);
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic", 2);
        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1);

        tasks.add(task);
        tasks.add(epic2);
        tasks.add(subtask1);
        tasks.add(epic1);

        manager.add(task);
        manager.add(epic1);
        manager.add(epic2);
        manager.add(subtask1);
        manager.add(epic1);

        List<Task> historyList = manager.getHistory();

        assertEquals(tasks, historyList, "Истории не совпадают.");
    }

    @Test
    void remove() {
        List<Task> tasks = new ArrayList<>();
        Task task = new Task("title", "description", 4);
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic", 2);
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic", 3);
        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1, 1);

        tasks.add(task);
        tasks.add(epic2);
        tasks.add(epic1);

        manager.add(task);
        manager.add(epic1);
        manager.add(epic2);
        manager.add(subtask1);
        manager.add(epic1);

        manager.remove(1);

        List<Task> historyList = manager.getHistory();

        assertEquals(tasks, historyList, "Истории не совпадают.");
    }

    @Test
    void getHistory() {
        Task task = new Task("title", "description", 4);
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic", 3);
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic", 2);
        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1);

        manager.add(task);
        manager.add(epic1);
        manager.add(epic2);
        manager.add(subtask1);
        manager.add(epic1);

        List<Task> historyList = manager.getHistory();

        assertEquals(4, historyList.size(), "История не пустая.");
    }
}
