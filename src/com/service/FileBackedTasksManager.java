package com.service;

import java.io.IOException;

import com.models.repository.*;
import com.models.util.CsvFileReader;
import com.models.util.CsvFileWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager{
    private Path filePath;
    private final static int minLineFile = 4;

    public FileBackedTasksManager(String fileName) {
        super();
        this.filePath = Paths.get(fileName);
        createFile();
        onload();
    }

    public FileBackedTasksManager(String fileName, HistoryManager historyManager) {
        super(historyManager);
        this.filePath = Paths.get(fileName);
        createFile();
        onload();
    }

    private void createFile() {
        if (Files.notExists(filePath)) {
            try {
                Files.createFile(filePath);
            }
            catch (IOException e) { }
        }
    }

    private String[] generateArrayTasks() {
        String[] arr_tasks = tasks.isEmpty() ? null : new String[tasks.size()];
        StringBuilder line = new StringBuilder();
        int index = 0;

        for (Task lineTask : tasks.values()) {
            line.append(lineTask.getId() + "," + TypeTask.TASK.toString() + "," + lineTask.getTitle() + "," + lineTask.getStatus() + "," + lineTask.getDescription());
            arr_tasks[index++] = line.toString();
            line.setLength(0);
        }

        return arr_tasks;
    }

    private String[] generateArrayEpics() {
        String[] arr_epics = epics.isEmpty() ? null : new String[epics.size()];
        StringBuilder line = new StringBuilder();
        int index = 0;

        for (Epic lineEpic : epics.values()) {
            line.append(lineEpic.getId() + "," + TypeTask.EPIC.toString() + "," + lineEpic.getTitle() + "," + lineEpic.getStatus() +  "," + lineEpic.getDescription());
            arr_epics[index++] = line.toString();
            line.setLength(0);
        }

        return arr_epics;
    }

    private String[] generateArraySubtasks() {
        String[] arr_subtasks = subtasks.isEmpty() ? null : new String[subtasks.size()];
        StringBuilder line = new StringBuilder();
        int index = 0;

        for (Subtask lineSubtask : subtasks.values()) {
            line.append(lineSubtask.getId() + "," + TypeTask.SUBTASK.toString() + "," + lineSubtask.getTitle() +  "," + lineSubtask.getStatus() + "," + lineSubtask.getDescription() + "," + lineSubtask.getIdEpic());
            arr_subtasks[index++] = line.toString();
            line.setLength(0);
        }

        return arr_subtasks;
    }

    private void saveTasks() {
        String[] arr_tasks = generateArrayTasks();
        String[] arr_epics = generateArrayEpics();
        String[] arr_subtasks = generateArraySubtasks();

        String attributeColumn = "id,type,title,status,description,epic";
        CsvFileWriter.writeFileContestsOrNull(filePath, attributeColumn + "\n", false);

        if (arr_tasks != null)
            for (int i = 0; i < arr_tasks.length; i++)
                CsvFileWriter.writeFileContestsOrNull(filePath, arr_tasks[i] + "\n", true);

        if (arr_epics != null)
            for (int i = 0; i < arr_epics.length; i++)
                CsvFileWriter.writeFileContestsOrNull(filePath, arr_epics[i] + "\n", true);

        if (arr_subtasks != null)
            for (int i = 0; i < arr_subtasks.length; i++)
                CsvFileWriter.writeFileContestsOrNull(filePath, arr_subtasks[i] + "\n", true);
    }

    private void saveHistory() {
        List<Task> historyTasks = history();

        if (historyTasks.isEmpty()) {
            CsvFileWriter.writeFileContestsOrNull(filePath, "-1, -1", true);
            return;
        }

        String line = null;
        StringBuilder stringBuilder = new StringBuilder("");

        for (Task currTask : historyTasks)
            stringBuilder.append(currTask.getId() + ",");

        line = stringBuilder.substring(0, stringBuilder.length() - 1);
        CsvFileWriter.writeFileContestsOrNull(filePath, line, true);
    }

    private void save() {
        saveTasks();
        CsvFileWriter.writeFileContestsOrNull(filePath, "\n", true);
        saveHistory();
    }

    private void onload() {
        String fileContent = CsvFileReader.readFileContestsOrNull(filePath);
        if (fileContent != null || !fileContent.trim().isEmpty()) {
            String[] lines = fileContent.split("\n");

            if (lines.length < minLineFile)
                return;

            for (int i = 1; i < lines.length - 2; i++) { // tasks
                String[] line = lines[i].split(",");

                Task task = null;
                int id = Integer.parseInt(line[0]);
                TypeTask typeTask = TypeTask.valueOf(line[1]);
                String title = line[2];
                StatusTask statusTask = StatusTask.valueOf(line[3]);
                String description = line[4];

                if (typeTask == TypeTask.TASK) {
                    task = new Task(title, description, id, statusTask);
                    tasks.put(task.getId(), task);
                }
                else if (typeTask == TypeTask.EPIC) {
                    task = new Epic(title, description, id, statusTask);
                    epics.put(task.getId(), (Epic)task);
                }
                else {
                    int idEpic = Integer.parseInt(line[5]);
                    task = new Subtask(title, description, id, statusTask, idEpic);
                    Subtask subtask = (Subtask) task;
                    subtasks.put(task.getId(), (Subtask)task);
                    Epic epic = epics.get(subtask.getIdEpic());
                    if (epic != null)
                        epic.addSubtasks(subtask);
                }
            }

            String[] line = lines[lines.length - 1].split(","); //history

            if (line[0].equals("-1"))
                return;

            for (int i = 0; i < line.length; i++) {
                int id = Integer.parseInt(line[i]);

                if (tasks.containsKey(id))
                    super.getTask(id);
                else if (epics.containsKey(id))
                    super.getEpic(id);
                else
                    super.getSubtask(id);

            }
        }

        int maxId = 0;
        maxId = Math.max(maxId, tasks.keySet().stream().max(Integer::compare).orElse(0));
        maxId = Math.max(maxId, epics.keySet().stream().max(Integer::compare).orElse(0));
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
    public Task getSubtask(int id) {
        Task subtask = super.getSubtask(id);
        save();
        return subtask;
    }


    @Override
    public Task getEpic(int id) {
        Task epic = super.getEpic(id);
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
    public boolean update(Task inTask) {
        boolean flag = super.update(inTask);

        if (flag)
            save();
        return flag;
    }
}
