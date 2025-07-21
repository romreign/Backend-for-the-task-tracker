package main.java.http.servers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import main.java.models.*;
import main.java.service.Manager;
import main.java.service.interfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int OK_CODE = 200;
    private static final int METHOD_NOT_ALLOWED_CODE = 400;
    private HttpServer httpServer;
    private Gson gson;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = Manager.getDefaultGson();

        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/", this::handle);
        }
        catch (IOException e) {
            System.out.println("При создании сервера произошла ошибка.");
            e.printStackTrace();
        }
    }

    public HttpTaskServer() {
        this(Manager.getDefault());
    }

    private void handle(HttpExchange httpExchange) {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET" :
                    getTasks(httpExchange, pathParts);
                    break;
                case "POST" :
                    postTasks(httpExchange, pathParts);
                    break;
                case "DELETE" :
                    deleteTasks(httpExchange, pathParts);
                    break;
                default :
                    System.out.println("Некорректный метод");
                    httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            httpExchange.close();
        }
    }

    private void getTasks (HttpExchange httpExchange, String[] pathParts) {
        URI uri = httpExchange.getRequestURI();
        String query = uri.getQuery();

        try {
            if (pathParts.length == 3 && pathParts[1].equalsIgnoreCase("tasks"))
                getPrioritizedTasks(httpExchange);
            else if (pathParts.length == 4 && pathParts[2].equalsIgnoreCase("history"))
                getHistoryTasks(httpExchange);
            else if (pathParts.length == 4 && query == null && pathParts[2].equalsIgnoreCase("task"))
                getAllCommonTasks(httpExchange);
            else if (pathParts.length == 4 && query == null && pathParts[2].equalsIgnoreCase("epic"))
                getAllEpics(httpExchange);
            else if (pathParts.length == 4 && query == null && pathParts[2].equalsIgnoreCase("subtask"))
                getAllSubtasks(httpExchange);
            else if (pathParts.length == 4 && query != null && pathParts[2].equalsIgnoreCase("task"))
                getTask(httpExchange, query);
            else if (pathParts.length == 4 && query != null && pathParts[2].equalsIgnoreCase("epic"))
                getEpic(httpExchange, query);
            else if (pathParts.length == 4 && query != null && pathParts[2].equalsIgnoreCase("subtask"))
                getSubtask(httpExchange, query);
            else if (pathParts.length == 4 && query != null && pathParts[2].equalsIgnoreCase("subtaskByEpicId"))
                getSubtaskByEpicId(httpExchange, query);
            else
                httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postTasks (HttpExchange httpExchange, String[] pathParts) {
        try {
            InputStream is = httpExchange.getRequestBody();
            String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
            System.out.println("Тело запроса: " + body);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (pathParts.length == 4 && pathParts[2].equalsIgnoreCase("task"))
                postTask(httpExchange, jsonElement);
            else if (pathParts.length == 4 && pathParts[2].equalsIgnoreCase("subtask"))
                postSubtask(httpExchange, jsonElement);
            else if (pathParts.length == 4 && pathParts[2].equalsIgnoreCase("epic"))
                postEpic(httpExchange, jsonElement);
            else
                httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postTask(HttpExchange httpExchange, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        StatusTask statusTask = jsonObject.has("statusTask") ?
                StatusTask.valueOf((jsonObject.get("statusTask").getAsString())) : StatusTask.NEW;
        long duration = jsonObject.has("duration") ? jsonObject.get("duration").getAsLong() : 0L;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime startTime = jsonObject.has("startTime") ?
                LocalDateTime.parse(jsonObject.get("startTime").getAsString()) : null;

        if (jsonObject.has("id")) {
            int id = jsonObject.get("id").getAsInt();
            taskManager.update(new Task(title, description, id, statusTask, startTime, duration));
        }
        else
            taskManager.create(new Task(title, description, statusTask, startTime, duration));
    }

    private void postSubtask(HttpExchange httpExchange, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        int idEpic = jsonObject.get("idEpic").getAsInt();
        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        StatusTask statusTask = jsonObject.has("statusTask") ?
                StatusTask.valueOf((jsonObject.get("statusTask").getAsString())) : StatusTask.NEW;
        long duration = jsonObject.has("duration") ? jsonObject.get("duration").getAsLong() : 0L;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime startTime = jsonObject.has("startTime") ?
                LocalDateTime.parse(jsonObject.get("startTime").getAsString()) : null;

        if (jsonObject.has("id")) {
            int id = jsonObject.get("id").getAsInt();
            taskManager.update(new Subtask(title, description, id, statusTask, idEpic, startTime, duration));
        }
        else
            taskManager.create(new Subtask(title, description, statusTask, idEpic, startTime, duration));
    }

    private void postEpic(HttpExchange httpExchange, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        StatusTask statusTask = jsonObject.has("statusTask") ?
                StatusTask.valueOf((jsonObject.get("statusTask").getAsString())) : StatusTask.NEW;
        JsonArray jsonArraySubtasks = jsonObject.has("subtasks") ? jsonObject.get("subtasks").getAsJsonArray() : null;
        Map<Integer, Subtask> subtasks = new HashMap<>();
        int id;

        if (jsonArraySubtasks != null) {
            for (JsonElement jsonElement1 : jsonArraySubtasks) {
                Subtask subtask = gson.fromJson(jsonElement1, Subtask.class);
                subtasks.put(subtask.getId(), subtask);
            }
        }

        Epic newEpic = new Epic(title, description, statusTask);
        if (!subtasks.isEmpty())
            newEpic.setSubstasks(subtasks);

        if (jsonObject.has("id")) {
           id = jsonObject.get("id").getAsInt();
            newEpic.setId(id);
            taskManager.update(newEpic);
        }
        else {
            taskManager.create(newEpic);
            id = taskManager.getNextId();
            taskManager.setEpicDateTime(id);
        }
        taskManager.setEpicDateTime(id);
    }

    private void deleteTasks (HttpExchange httpExchange, String[] pathParts) {
        URI uri = httpExchange.getRequestURI();
        String query = uri.getQuery();

        try {
            if (pathParts.length == 3 && pathParts[1].equalsIgnoreCase("tasks"))
                deleteAllTasks(httpExchange);
            else if (pathParts.length == 4 && query == null && pathParts[2].equalsIgnoreCase("task"))
                deleteAllCommonTasks(httpExchange);
            else if (pathParts.length == 4 && query == null && pathParts[2].equalsIgnoreCase("epic"))
                deleteAllEpics(httpExchange);
            else if (pathParts.length == 4 && query == null && pathParts[2].equalsIgnoreCase("subtask"))
                deleteAllSubtasks(httpExchange);
            else if (pathParts.length == 4 && query != null && pathParts[2].equalsIgnoreCase("task"))
                deleteTask(httpExchange, query);
            else if (pathParts.length == 4 && query != null && pathParts[2].equalsIgnoreCase("epic"))
                deleteEpic(httpExchange, query);
            else if (pathParts.length == 4 && query != null && pathParts[2].equalsIgnoreCase("subtask"))
                deleteSubtask(httpExchange, query);
            else
                httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getPrioritizedTasks(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendResponseText(httpExchange, response);
        System.out.println("Задачи по временному приоритету выведены.");
    }

    private void getHistoryTasks(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.history());
        sendResponseText(httpExchange, response);
        System.out.println("История выведена.");
    }

    private void getAllCommonTasks(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        sendResponseText(httpExchange, response);
        System.out.println("Обычные задачи выведены.");
    }

    private void getAllEpics(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getEpics());
        sendResponseText(httpExchange, response);
        System.out.println("Эпики выведены.");
    }

    private void getAllSubtasks(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getSubtasks());
        sendResponseText(httpExchange, response);
        System.out.println("Все подзадачи выведены.");
    }

    private void getTask(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        Task task = taskManager.getTask(id);

        if (task == null) {
            httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
            return;
        }

        String response = gson.toJson(task);
        sendResponseText(httpExchange, response);
        System.out.println("Обычная задача выведена.");
    }

    private void getEpic(HttpExchange httpExchange, String query) throws IOException{
        int id = parseId(query);
        Epic epic = taskManager.getEpic(id);

        if (epic == null) {
            httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
            return;
        }

        String response = gson.toJson(epic);
        sendResponseText(httpExchange, response);
        System.out.println("Эпик выведен.");
    }

    private void getSubtask(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        Subtask subtask = taskManager.getSubtask(id);

        if (subtask == null) {
            httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
            return;
        }

        String response = gson.toJson(subtask);
        sendResponseText(httpExchange, response);
        System.out.println("Подзадача выведена.");
    }

    private void getSubtaskByEpicId(HttpExchange httpExchange, String query) throws IOException{
        int id = parseId(query);
        Map<Integer, Subtask> subtasks = taskManager.getSubtasksByEpicId(id);

        if (subtasks == null) {
            httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
            return;
        }

        String response = gson.toJson(subtasks);
        sendResponseText(httpExchange, response);
        System.out.println("Подзадачи выведены.");
    }

    private void deleteAllTasks(HttpExchange httpExchange) throws IOException {
        String response = "Удалены все задачи.";
        taskManager.removeAll();
        sendResponseText(httpExchange, response);
        System.out.println(response);
    }

    private void deleteAllCommonTasks(HttpExchange httpExchange) throws IOException {
        String response = "Удалены все обычные задачи.";
        taskManager.removeTasksOfType(TypeTask.TASK);
        sendResponseText(httpExchange, response);
        System.out.println(response);
    }

    private void deleteAllEpics(HttpExchange httpExchange) throws IOException {
        String response = "Удалены все эпики.";
        taskManager.removeTasksOfType(TypeTask.EPIC);
        sendResponseText(httpExchange, response);
        System.out.println(response);
    }

    private void deleteAllSubtasks(HttpExchange httpExchange) throws IOException {
        String response = "Удалены все подзадачи.";
        taskManager.removeTasksOfType(TypeTask.SUBTASK);
        sendResponseText(httpExchange, response);
        System.out.println(response);
    }

    private void deleteTask(HttpExchange httpExchange, String query) throws IOException{
        int id = parseId(query);
        boolean flag = taskManager.remove(id, TypeTask.TASK);

        if (!flag) {
            httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
            return;
        }

        String response = "Задача с идентификатором " + id + " удалена.";
        sendResponseText(httpExchange, response);
        System.out.println(response);
    }

    private void deleteSubtask(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        boolean flag = taskManager.remove(id, TypeTask.SUBTASK);

        if (!flag) {
            httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
            return;
        }

        String response = "Подзадача с идентификатором " + id + " удалена.";
        sendResponseText(httpExchange, response);
        System.out.println(response);
    }

    private void deleteEpic(HttpExchange httpExchange, String query) throws IOException{
        int id = parseId(query);
        boolean flag = taskManager.remove(id, TypeTask.EPIC);

        if (!flag) {
            httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED_CODE, 0);
            return;
        }

        String response = "Эпик с идентификатором " + id + " удален.";
        sendResponseText(httpExchange, response);
        System.out.println(response);
    }

    private void sendResponseText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(OK_CODE, 0);
        httpExchange.getResponseBody().write(response);
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер приостановлен");
    }

    private static int parseId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
