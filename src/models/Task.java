package models;

import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;

    public Task(String title, String description, int id, Status status) {
        this.description = description;
        this.title = title;
        this.status = status;
        this.id = id;
    }

    public Task(String title, String description, Status status) {
        this(title, description, 0, status);
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task {"
                + "title = '" + title + "'"
                + ", description.length = '" + description.length() + "'"
                + ", id = " + id
                + ", status = " + status
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
                && status == task.status;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        int prime = 31;

        hash = ((title != null ? title.hashCode() : 0 ) + hash) * prime;
        hash = ((description != null ? description.hashCode() : 0 ) + hash) * prime;
        hash = (id + hash) * prime;
        hash = ((status != null ? status.hashCode() : 0 ) + hash) * prime;
        return hash;
    }
}