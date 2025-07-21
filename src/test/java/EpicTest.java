package test.java;

import main.java.service.Manager;
import main.java.service.interfaces.TaskManager;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.StatusTask;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

class EpicTest {
    private final TaskManager manager = Manager.getDefault();

    @Test
    public void epicHasNewStatusWhenSubTaskListIsEmpty() {
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);
        List<Subtask> subtaskList = new ArrayList<>(manager.getSubtasksByEpicId(1).values());

        assertTrue(subtaskList.isEmpty(), "Список подзадач не пуст");
        assertEquals(StatusTask.NEW, epic.getStatus());
    }

    @Test
    public void epicHasNewStatusWhenAllSubTasksAreNew() {
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);

        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 1);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 1);

        manager.create(subtask1);
        manager.create(subtask2);

        List<Subtask> subtaskList = new ArrayList<>(manager.getSubtasks().values());

        assertNotNull(subtaskList, "Список подзадач пуст");
        assertEquals(StatusTask.NEW, subtask1.getStatus());
        assertEquals(StatusTask.NEW, epic.getStatus());
    }

    @Test
    public void epicHasDoneStatusWhenAllSubTasksAreDone() {
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);

        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", StatusTask.DONE,1);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", StatusTask.DONE,1);

        manager.create(subtask1);
        manager.create(subtask2);

        List<Subtask> subtaskList = new ArrayList<>(manager.getSubtasks().values());

        assertNotNull(subtaskList, "Список подзадач пуст");
        assertEquals(StatusTask.DONE, epic.getStatus());
    }

    @Test
    public void epicHasInProgressStatusWhenSubTasksAreDoneAndNew() {
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);

        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", StatusTask.DONE,1);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", StatusTask.NEW,1);

        manager.create(subtask1);
        manager.create(subtask2);

        List<Subtask> subtaskList = new ArrayList<>(manager.getSubtasks().values());

        assertNotNull(subtaskList, "Список подзадач пуст");
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicHasInProgressStatusWhenSubTasksAreInProgress() {
        Epic epic = new Epic("titleEpic", "descriptionEpic");

        manager.create(epic);

        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", StatusTask.IN_PROGRESS,1);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", StatusTask.IN_PROGRESS,1);

        manager.create(subtask1);
        manager.create(subtask2);

        List<Subtask> subtaskList = new ArrayList<>(manager.getSubtasks().values());

        assertNotNull(subtaskList, "Список подзадач пуст");
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatus());
    }
}