package main.java.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected StatusTask statusTask;
    protected LocalDateTime startTime;
    protected long duration;

    public Task(String title, String description, int id, StatusTask statusTask, LocalDateTime startTime, long duration) {
        this.description = description;
        this.title = title;
        this.statusTask = statusTask;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, StatusTask statusTask, LocalDateTime startTime, long duration) {
        this(title, description, 0, statusTask, startTime, duration);
    }

    public Task(String title, String description, int id) {
        this(title, description, id, StatusTask.NEW, null, 0L);
    }

    public Task(String title, String description) {
        this(title, description, 0, StatusTask.NEW, null, 0L);
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime)  {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    }

    public LocalDateTime getEndTime() {
        return startTime == null ? null : startTime.plusMinutes(duration);
    }

    public String getEndTimeString() {
        return  getEndTime().format(getDateTimeFormatter());
    }

    @Override
    public String toString() {
        return "Task {"
                + "title = '" + title + "'"
                + ", description.length = '" + description.length() + "'"
                + ", id = " + id
                + ", status = " + statusTask
                + ", startTime = " + startTime
                + ", duration = " + duration
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
                && statusTask == task.statusTask && startTime.equals(task.startTime) && duration == task.duration;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        int prime = 31;

        hash = ((title != null ? title.hashCode() : 0 ) + hash) * prime;
        hash = ((description != null ? description.hashCode() : 0 ) + hash) * prime;
        hash = (id + hash) * prime;
        hash = ((statusTask != null ? statusTask.hashCode() : 0 ) + hash) * prime;
        hash = ((startTime != null ? startTime.hashCode() : 0 ) + hash) * prime;
        hash = (int)((duration + hash) * prime);
        return hash;
    }
}