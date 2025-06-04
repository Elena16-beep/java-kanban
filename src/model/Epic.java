package model;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        subtasks = new HashMap<>();
    }

    public Epic(Integer id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        subtasks = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Эпик " + id +
                ": название: " + name +
                ", описание: " + description +
                ", статус: " + status + '\n');

        if (!subtasks.isEmpty()) {
            for (int subtask : subtasks.keySet()) {
                result.append("     ").append(subtasks.get(subtask)).append('\n');
            }
        }

        return result.toString();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Type getType() {
        return Type.EPIC;
    }
}
