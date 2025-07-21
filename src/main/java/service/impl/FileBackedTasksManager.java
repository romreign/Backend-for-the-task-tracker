package main.java.service.impl;

import java.io.IOException;

import main.java.exceptions.CollisionTaskException;
import main.java.models.*;
import main.java.service.interfaces.HistoryManager;
import main.java.util.CsvFileReader;
import main.java.util.CsvFileWriter;
import main.java.util.CsvFileDeletor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager{
    private Path filePath;
    private final static int minLineFile = 4;

    public FileBackedTasksManager(String fileName) {
        super();
        this.filePath = Paths.get(fileName);
    }

    public FileBackedTasksManager(String fileName, HistoryManager historyManager) {
        super(historyManager);
        this.filePath = Paths.get(fileName);
    }

    private String[] generateArrayTasks() {
        if (tasks.isEmpty())
            return null;

        String[] arr_tasks = new String[tasks.size()];
        StringBuilder line = new StringBuilder();
        int index = 0;

        for (Task task : tasks.values()) {
            String id = (task.getId() != 0) ? task.getId() + "" : "-";
            String type = (TypeTask.TASK != null) ? TypeTask.TASK.toString() : "-";
            String title = (task.getTitle() != null) ? task.getTitle() : "-";
            String status = (task.getStatus() != null) ? task.getStatus().toString() : "-";
            String description = (task.getDescription() != null) ? task.getDescription() : "-";
            String startTime = (task.getStartTime() != null) ? task.getStartTime().toString() : "-";
            String duration = (task.getDuration() != 0) ? task.getDuration() + "" : "-";

            arr_tasks[index++] = String.join(",",
                    id, type, title, status, description, startTime, duration
            );
        }

        return arr_tasks;
    }

    private String[] generateArrayEpics() {
        if (epics.isEmpty())
            return null;

        String[] arr_epics = new String[epics.size()];
        StringBuilder line = new StringBuilder();
        int index = 0;

        for (Epic epic : epics.values()) {
            String id = (epic.getId() != 0) ? epic.getId() + "" : "-";
            String type = (TypeTask.EPIC != null) ? TypeTask.EPIC.toString() : "-";
            String title = (epic.getTitle() != null) ? epic.getTitle() : "-";
            String status = (epic.getStatus() != null) ? epic.getStatus().toString() : "-";
            String description = (epic.getDescription() != null) ? epic.getDescription() : "-";
            String startTime = (epic.getStartTime() != null) ? epic.getStartTime().toString() : "-";
            String duration = (epic.getDuration() != 0) ? epic.getDuration() + "" : "-";

            arr_epics[index++] = String.join(",",
                    id, type, title, status, description, startTime, duration
            );
        }

        return arr_epics;
    }

    private String[] generateArraySubtasks() {
        if (subtasks.isEmpty())
            return null;

        String[] arr_subtasks = new String[subtasks.size()];
        StringBuilder line = new StringBuilder();
        int index = 0;

        for (Subtask subtask : subtasks.values()) {
            String id = (subtask.getId() != 0) ? subtask.getId() + "" : "-";
            String type = (TypeTask.SUBTASK != null) ? TypeTask.SUBTASK.toString() : "-";
            String title = (subtask.getTitle() != null) ? subtask.getTitle() : "-";
            String status = (subtask.getStatus() != null) ? subtask.getStatus().toString() : "-";
            String description = (subtask.getDescription() != null) ? subtask.getDescription() : "-";
            String startTime = (subtask.getStartTime() != null) ? subtask.getStartTime().toString() : "-";
            String duration = (subtask.getDuration() != 0) ? subtask.getDuration() + "" : "-";
            String idEpic = (subtask.getIdEpic() != 0) ? subtask.getIdEpic() + "" : "-";

            arr_subtasks[index++] = String.join(",",
                    id, type, title, status, description, startTime, duration, idEpic
            );
        }

        return arr_subtasks;
    }

    private void saveTasks() {
        String[] arr_tasks = generateArrayTasks();
        String[] arr_epics = generateArrayEpics();
        String[] arr_subtasks = generateArraySubtasks();

        final String  ATTRIBUTE_COLUMN = "id,type,title,status,description,epic,startTime,duration";
        CsvFileWriter.writeFileContestsOrNull(filePath, ATTRIBUTE_COLUMN + "\n", false);

        if (arr_tasks != null)
            for (String arrTask : arr_tasks)
                CsvFileWriter.writeFileContestsOrNull(filePath, arrTask + "\n", true);

        if (arr_epics != null)
            for (String arrEpic : arr_epics)
                CsvFileWriter.writeFileContestsOrNull(filePath, arrEpic + "\n", true);

        if (arr_subtasks != null)
            for (String arrSubtask : arr_subtasks)
                CsvFileWriter.writeFileContestsOrNull(filePath, arrSubtask + "\n", true);
    }

    private void saveHistory() {
        List<Task> historyTasks = history();

        if (historyTasks.isEmpty()) {
            CsvFileWriter.writeFileContestsOrNull(filePath, "-", true);
            return;
        }

        String line = null;
        StringBuilder stringBuilder = new StringBuilder();

        for (Task currTask : historyTasks)
            stringBuilder.append(currTask.getId()).append(",");

        line = stringBuilder.substring(0, stringBuilder.length() - 1);
        CsvFileWriter.writeFileContestsOrNull(filePath, line, true);
    }

    private void save() {
        try {
            saveTasks();
            CsvFileWriter.writeFileContestsOrNull(filePath, "\n", true);
            saveHistory();
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    public void delete() {
        CsvFileDeletor.deleteFile(filePath);
    }

    public void createFile() {
        if (Files.notExists(filePath)) {
            try {
                Files.createFile(filePath);
            }
            catch (IOException e) {
                System.out.println("Файл уже существует.");
            }
        }
    }

    public void onload() {
        String fileContent = CsvFileReader.readFileContestsOrNull(filePath);

        if (fileContent == null || fileContent.trim().isEmpty())
            return;

        String[] lines = fileContent.split("\n");

        tasks.clear();
        epics.clear();
        subtasks.clear();

        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().isEmpty())
                break;

            String[] line = lines[i].split(",");
            try {
                int id = line[0].equals("-") ? 0 : Integer.parseInt(line[0]);
                TypeTask typeTask = TypeTask.valueOf(line[1]);
                String title = line[2].equals("-") ? null : line[2];
                StatusTask statusTask = line[3].equals("-") ? StatusTask.NEW : StatusTask.valueOf(line[3]);
                String description = line[4].equals("-") ? null : line[4];
                LocalDateTime startTime = line[5].equals("-") ? null : LocalDateTime.parse(line[5]);
                long duration = line[6].equals("-") ? 0L : Long.parseLong(line[6]);

                if (typeTask == TypeTask.TASK) {
                    Task task = new Task(title, description, id, statusTask, startTime, duration);
                    tasks.put(task.getId(), task);
                }
                else if (typeTask == TypeTask.EPIC) {
                    Epic epic = new Epic(title, description, id, statusTask, startTime, duration);
                    epics.put(epic.getId(), epic);
                }
                else if (typeTask == TypeTask.SUBTASK && line.length >= 8) {
                    int idEpic = line[7].equals("-") ? 0 : Integer.parseInt(line[7]);
                    Subtask subtask = new Subtask(title, description, id, statusTask, idEpic, startTime, duration);
                    subtasks.put(subtask.getId(), subtask);

                    if (idEpic != 0 && epics.containsKey(idEpic))
                        epics.get(idEpic).addSubtasks(subtask);
                }
            } catch (Exception e) {
                System.err.println("Ошибка при чтении строки: " + lines[i]);
                e.printStackTrace();
            }
        }

        if (lines.length > 1 && !lines[lines.length - 1].trim().isEmpty()) {
            String[] historyLine = lines[lines.length - 1].split(",");
            if (!historyLine[0].equals("-")) {
                for (String idStr : historyLine) {
                    try {
                        int id = Integer.parseInt(idStr);
                        if (tasks.containsKey(id))
                            historyManager.add(tasks.get(id));
                        else if (epics.containsKey(id))
                            historyManager.add(epics.get(id));
                        else if (subtasks.containsKey(id))
                            historyManager.add(subtasks.get(id));
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Некорректный ID в истории: " + idStr);
                    }
                }
            }
        }

        int maxId = 0;
        if (!tasks.isEmpty())
            maxId = Math.max(maxId, tasks.keySet().stream().max(Integer::compare).orElse(0));
        if (!epics.isEmpty())
            maxId = Math.max(maxId, epics.keySet().stream().max(Integer::compare).orElse(0));
        if (!subtasks.isEmpty())
            maxId = Math.max(maxId, subtasks.keySet().stream().max(Integer::compare).orElse(0));

        setNextId(maxId + 1);
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public Path getFilePath() {
        return filePath;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public String toString() {
        return "FileBackedTasksManager {"
                + "tasks.size=" + tasks.size()
                + ", subtasks.size=" + subtasks.size()
                + ", epics.size=" + epics.size()
                + ", file path=" + filePath.toString()
                + "}\n";
    }

    @Override
    public boolean remove(int id, TypeTask typeTask) {
        boolean flag = super.remove(id, typeTask);

        if (flag)
            save();
        return flag;
    }

    @Override
    public void removeTasksOfType (TypeTask typeTask) {
        super.removeTasksOfType(typeTask);
        save();
    }

    @Override
    public void removeAll () {
        super.removeAll();
        save();
    }

    @Override
    public boolean create(Task inTask) {
        boolean flag = super.create(inTask);

        if (flag)
            save();
        return flag;
    }

    @Override
    public boolean update(Task inTask) throws CollisionTaskException {
        boolean flag = super.update(inTask);

        if (flag)
            save();
        return flag;
    }
}
