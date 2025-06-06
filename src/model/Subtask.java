package model;

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

    @Override
    public String toString() {
        return "Подзадача " + id +
                ": название: " + name +
                ", описание: " + description +
                ", статус: " + status +
                ", эпик: " + idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public Type getType() {
        return Type.SUBTASK;
    }
}
