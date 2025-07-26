package main.java;

import main.java.http.servers.HttpTaskServer;
import main.java.http.servers.KVServer;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
       /* LocalDateTime localDateTime1 = LocalDateTime.of(2025, 01, 01, 10, 10, 10);
        LocalDateTime localDateTime2 = LocalDateTime.of(2025, 01, 03, 10, 10, 10);
        LocalDateTime localDateTime3 = LocalDateTime.of(2024, 12, 29, 10, 10, 10);
        Task task = new Task("title", "description");
        Epic epic1 = new Epic("titleEpic1", "descriptionEpic");
        Epic epic2 = new Epic("titleEpic2", "descriptionEpic");
        Subtask subtask1 = new Subtask("titleSubtask1", "descriptionSubtask1", 2, localDateTime1,100);
        Subtask subtask2 = new Subtask("titleSubtask2", "descriptionSubtask2", 2, localDateTime2,200);
        Subtask subtask3 = new Subtask("titleSubtask3", "descriptionSubtask3", 3, localDateTime3,300);

        try {
            KVServer kvServer = new KVServer();
            kvServer.start();
            HttpTaskServer httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
            httpTaskServer.getTaskManager().create(task);
            httpTaskServer.getTaskManager().create(epic1);
            httpTaskServer.getTaskManager().create(epic2);
            httpTaskServer.getTaskManager().create(subtask1);
            httpTaskServer.getTaskManager().create(subtask2);
            httpTaskServer.getTaskManager().create(subtask3);
            System.out.println(httpTaskServer.getTaskManager());
        }
        catch (Exception e) {
            System.out.println("KVServer не был запущен!");
        }*/
    }
}