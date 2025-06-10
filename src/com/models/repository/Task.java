package com.models.repository;

import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected StatusTask statusTask;

    public Task(String title, String description, int id, StatusTask statusTask) {
        this.description = description;
        this.title = title;
        this.statusTask = statusTask;
        this.id = id;
    }

    public Task(String title, String description, StatusTask statusTask) {
        this(title, description, 0, statusTask);
    }

    public Task(String title, String description) {
        this(title, description, 0, StatusTask.NEW);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusTask getStatus() {
        return statusTask;
    }

    public void setStatus(StatusTask statusTask) {
        this.statusTask = statusTask;
    }

    @Override
    public String toString() {
        return "Task {"
                + "title = '" + title + "'"
                + ", description.length = '" + description.length() + "'"
                + ", id = " + id
                + ", status = " + statusTask
                + "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description)
                && statusTask == task.statusTask;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        int prime = 31;

        hash = ((title != null ? title.hashCode() : 0 ) + hash) * prime;
        hash = ((description != null ? description.hashCode() : 0 ) + hash) * prime;
        hash = (id + hash) * prime;
        hash = ((statusTask != null ? statusTask.hashCode() : 0 ) + hash) * prime;
        return hash;
    }
}