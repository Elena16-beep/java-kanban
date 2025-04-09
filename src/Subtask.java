public class Subtask extends Task {
    int idEpic;

    public Subtask(String name, String description, int idEpic) {
        super(name, description);
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
}
