import java.util.HashMap;

public class Epic extends Task {
    public HashMap <Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super();
        this.name = name;
        this.description = description;
        subtasks = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Эпик " + id +
                ": название: " + name +
                ", описание: " + description +
                ", статус: " + status + '\n' +
                "Подзадачи эпика " + id + ":" + '\n');

        for (int subtask : subtasks.keySet()) {
            result.append("     ").append(subtasks.get(subtask)).append('\n');
        }

        return result.toString();
    }
}
