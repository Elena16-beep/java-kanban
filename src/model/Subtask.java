package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String name, String description, int idEpic) {
        super(name, description);
        this.idEpic = idEpic;
    }

    public Subtask(Integer id, String name, String description, Status status, int idEpic) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.idEpic = idEpic;
    }

    // Sprint8
    public Subtask(String name, String description, int idEpic, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.idEpic = idEpic;
    }

    public Subtask(Integer id, String name, String description, Status status, int idEpic, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.idEpic = idEpic;
        this.duration = duration;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        String str = "Подзадача " + id +
                ": название: " + name +
                ", описание: " + description +
                ", статус: " + status +
                ", эпик: " + idEpic;

        if (duration != null) {
            str = str +
                    "," + '\n' + "        " + "продолжительность: " +
                    duration.toMinutes() +
                    " минут";
        }

        if (startTime != null) {
            str = str +
                    "," + '\n' + "        " + "дата и время, когда предполагается приступить к выполнению: " +
                    startTime.format(formatter);
        }

        if (getEndTime() != null) {
            str = str +
                    "," + '\n' + "        " + "дата и время завершения: " +
                    getEndTime().format(formatter);
        }

        str = str + '\n';

        return str;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public Type getType() {
        return Type.SUBTASK;
    }
}
