package test.java;

import main.java.http.servers.KVServer;
import main.java.models.Epic;
import main.java.models.StatusTask;
import main.java.models.Subtask;
import main.java.models.Task;
import main.java.service.impl.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    private KVServer server;
    private HttpTaskManager manager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() throws IOException {
        server = new KVServer();
        server.start();
        manager = new HttpTaskManager("http://localhost:8078");

        LocalDateTime now = LocalDateTime.now();
        epic = new Epic("Test epic", "Description");
        manager.create(epic);

        subtask = new Subtask("Test subtask", "Description", StatusTask.NEW,
                epic.getId(), now.plusHours(2), 30);

        task = new Task("Test task", "Description", StatusTask.NEW,
                now, 60);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testSaveAndLoadEpics() {
        manager.create(epic);
        manager.create(subtask);
        manager.save();

        HttpTaskManager newManager = new HttpTaskManager("http://localhost:8078");
        newManager.onload();

        Epic loadedEpic = newManager.getEpic(epic.getId());
        assertNotNull(loadedEpic);
        assertEquals(epic.getTitle(), loadedEpic.getTitle());
        assertEquals(1, loadedEpic.getSubstasks().size());
    }

    @Test
    void testHistoryAfterLoad() {
        manager.create(epic);
        manager.create(subtask);
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());
        manager.save();

        HttpTaskManager newManager = new HttpTaskManager("http://localhost:8078");
        newManager.onload();

        List<Task> history = newManager.history();
        assertEquals(2, history.size());
        assertTrue(history.stream().anyMatch(t -> t.getId() == epic.getId()));
        assertTrue(history.stream().anyMatch(t -> t.getId() == subtask.getId()));
    }
}