package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks;
    // Sprint8
    protected LocalDateTime endTime;

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

    // Sprint8
    public Epic(Integer id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        subtasks = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Эпик " + id +
                ": название: " + name +
                ", описание: " + description +
                ", статус: " + status);

        if (duration != null) {
            result.append(",").append('\n').append("     ").append("продолжительность: ").append(duration.toMinutes()).append(" минут");
        }

        if (startTime != null) {
            result.append(",").append('\n').append("     ").append("дата и время, когда предполагается приступить к выполнению: ").append(startTime.format(formatter));
        }

        if (getEndTime() != null) {
            result.append(",").append('\n').append("     ").append("дата и время завершения: ").append(getEndTime().format(formatter));
        }

        result.append('\n');

        if (!subtasks.isEmpty()) {
            for (int subtask : subtasks.keySet()) {
                result.append("   ").append(subtasks.get(subtask));
            }
        }

        return result.toString();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
