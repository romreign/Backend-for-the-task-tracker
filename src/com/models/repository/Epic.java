package com.models.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {
    private Map<Integer, Subtask> subtasks;

    public Epic(String title, String description, int id, StatusTask statusTask) {
        super(title, description, id, statusTask);
        subtasks = new HashMap<Integer, Subtask>();
    }

    public Epic(String title, String description, StatusTask statusTask) {
        this(title, description, 0, statusTask);
    }

    public Epic(String title, String description) {
        this(title, description, 0, StatusTask.NEW);
    }

    public Map<Integer, Subtask> getSubstasks() {
        return subtasks;
    }

    public void setSubstasks(Map<Integer, Subtask> substasks) {
        this.subtasks = substasks;
    }

    public void addSubtasks(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
        updateStatus();
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            statusTask = StatusTask.NEW;
            return;
        }

        int countDone = 0;
        int countInProgress = 0;

        for (Subtask sub : subtasks.values()) {
            if (sub.getStatus() == StatusTask.DONE)
                countDone++;
            else if (sub.getStatus() == StatusTask.IN_PROGRESS)
                countInProgress++;
        }

        if (countDone == subtasks.size())
            statusTask = StatusTask.DONE;
        else if (countInProgress > 0 || countDone < subtasks.size() && countDone > 0)
            statusTask = StatusTask.IN_PROGRESS;
    }

    @Override
    public String toString() {
        String str = "Epic {"
                + "title = '" + title + "'"
                + ", description.length = '" + description.length() + "'"
                + ", id = " + id
                + ", status = " + statusTask
                + "\n\tsubtasks{\n";
        for (Subtask sub: subtasks.values())
            str += '\t' + sub.toString();
        str += "\t} \n }\n";
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks) && id == epic.id && Objects.equals(title, epic.title)
                && Objects.equals(description, epic.description) && statusTask == epic.statusTask;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        int prime = 31;

        hash = ((title != null ? title.hashCode() : 0 ) + hash) * prime;
        hash = ((description != null ? description.hashCode() : 0 ) + hash) * prime;
        hash = (id + hash) * prime;
        hash = ((statusTask != null ? statusTask.hashCode() : 0 ) + hash) * prime;
        hash = ((subtasks != null ? subtasks.hashCode() : 0 ) + hash) * prime;
        return hash;
    }
}