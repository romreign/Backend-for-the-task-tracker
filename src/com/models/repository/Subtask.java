package com.models.repository;

import java.util.Objects;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String title, String description, int id, StatusTask statusTask, int idEpic) {
        super(title, description, id, statusTask);
        this.idEpic = idEpic;
    }

    public Subtask(String title, String description, StatusTask statusTask, int idEpic) {
        this(title, description, 0, statusTask, idEpic);
    }

    public Subtask(String title, String description, int idEpic) {
        this(title, description, 0, StatusTask.NEW, idEpic);
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Subtask subtask = (Subtask) o;
        return idEpic == subtask.idEpic && id == subtask.id && Objects.equals(title, subtask.title)
                && Objects.equals(description, subtask.description)
                && statusTask == subtask.statusTask;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        int prime = 31;

        hash = ((title != null ? title.hashCode() : 0 ) + hash) * prime;
        hash = ((description != null ? description.hashCode() : 0 ) + hash) * prime;
        hash = (id + hash) * prime;
        hash = ((statusTask != null ? statusTask.hashCode() : 0 ) + hash) * prime;
        hash = (idEpic + hash) * prime;
        return hash;
    }

    @Override
    public String toString() {
        return "Subtask {"
                + "title = '" + title + "'"
                + ", description.length = '" + description.length() + "'"
                + ", id = " + id
                + ", status = " + statusTask
                + ", idEpic = " + idEpic
                + "}\n";
    }

}