import java.util.Objects;

public class Task {
    public String name;
    public String description;
    public int id;
    public Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Задача " + id +
                ": название: " + name +
                ", описание: " + description +
                ", статус: " + status;
    }
}
