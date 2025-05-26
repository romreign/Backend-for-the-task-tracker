import models.*;
import service.Manager;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("title1", "description1",  Status.NEW);
        Task task2 = new Task("title2", "description2",  Status.NEW);

        Epic epic1 = new Epic("title3", "description3",  Status.NEW);
        Subtask subtask11 = new Subtask("title31", "description31",  Status.NEW, 3);
        Subtask subtask12 = new Subtask("title32", "description32",  Status.NEW, 3);
        epic1.addSubtasks(subtask11);
        epic1.addSubtasks(subtask12);

        Epic epic2 = new Epic("title4", "description4",  Status.NEW);
        Subtask subtask21 = new Subtask("title41", "description21",  Status.NEW, 4);
        epic2.addSubtasks(subtask21);

        manager.create(task1);
        manager.create(task2);
        manager.create(epic1);
        manager.create(subtask11);
        manager.create(subtask12);
        manager.create(epic2);
        manager.create(subtask21);

        System.out.println(manager);

        System.out.println(task1);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println(subtask11);
        System.out.println(subtask12);
        System.out.println(subtask21);

        task1.setStatus(Status.IN_PROGRESS);
        manager.update(task1);
        System.out.println(task1);

        subtask11.setStatus(Status.DONE);
        subtask12.setStatus(Status.DONE);
        manager.update(subtask11);
        manager.update(subtask12);
        manager.update(epic1);
        System.out.println(epic1);

        subtask21.setStatus(Status.IN_PROGRESS);
        manager.update(subtask21);
        System.out.println(subtask21);

        manager.remove(task1.getId(), TypeTask.TASK);
        manager.remove(epic2.getId(), TypeTask.EPIC);

        System.out.println(manager);
    }
}