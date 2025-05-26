package models;

import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks;

    public Epic(String title, String description, int id, Status status) {
        super(description, title, id, status);
        subtasks = new HashMap<Integer, Subtask>();
    }

    public Epic(String title, String description, Status status) {
        this(title, description, 0, status);
    }

    public HashMap<Integer, Subtask> getSubstasks() {
        return subtasks;
    }

    public void setSubstasks(HashMap<Integer, Subtask> substasks) {
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
            status = Status.NEW;
            return;
        }

        int countDone = 0;

        for (Subtask sub : subtasks.values()) {
            if (sub.getStatus() == Status.DONE)
                countDone++;
        }

        if (countDone == subtasks.size())
            status = Status.DONE;
        else if (countDone < subtasks.size() && countDone > 0)
            status = Status.IN_PROGRESS;
    }

    @Override
    public String toString() {
        String str = "Epic {"
                + "title = '" + title + "'"
                + ", description.length = '" + description.length() + "'"
                + ", id = " + id
                + ", status = " + status
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
                && Objects.equals(description, epic.description) && status == epic.status;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        int prime = 31;

        hash = ((title != null ? title.hashCode() : 0 ) + hash) * prime;
        hash = ((description != null ? description.hashCode() : 0 ) + hash) * prime;
        hash = (id + hash) * prime;
        hash = ((status != null ? status.hashCode() : 0 ) + hash) * prime;
        hash = ((subtasks != null ? subtasks.hashCode() : 0 ) + hash) * prime;
        return hash;
    }
}