import java.util.HashMap;

public class Manager {
    public int count = 0;
    public HashMap<Integer, Task> tasks;
    public HashMap<Integer, Subtask> subtasks;
    public HashMap<Integer, Epic> epics;

    public Manager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public void addTask(Task task) {
        task.id = generateId();
        task.status = Status.NEW;
        tasks.put(task.id, task);
    }

    public void updateTask(Task task, String name, String description, Status status) {
        if (name != null && !name.isEmpty()) {
            task.name = name;
        }

        if (description != null && !description.isEmpty()) {
            task.description = description;
        }

        task.status = status;
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.id = generateId();
        epic.status = Status.NEW;
        epics.put(epic.id, epic);
    }

    public void updateEpic(Epic epic, String name, String description) {
        if (name != null && !name.isEmpty()) {
            epic.name = name;
        }

        if (description != null && !description.isEmpty()) {
            epic.description = description;
        }

        updateStatus(epic);
        epics.put(epic.getId(), epic);
    }

    public void updateStatus(Epic epic) {
        Status status = Status.NEW;
        Status oldValue = null;

        for (int subtask : epic.subtasks.keySet()) {
            if (epic.subtasks.get(subtask).status == Status.valueOf("NEW") &&
                    epic.subtasks.get(subtask).status != oldValue) {
                oldValue = Status.valueOf("NEW");
                status = epic.subtasks.get(subtask).status;

            } else if (epic.subtasks.get(subtask).status == Status.valueOf("IN_PROGRESS") &&
                    epic.subtasks.get(subtask).status != oldValue) {
                oldValue = Status.valueOf("IN_PROGRESS");
                status = epic.subtasks.get(subtask).status;

            } if (epic.subtasks.get(subtask).status == Status.valueOf("DONE") &&
                    epic.subtasks.get(subtask).status != oldValue) {
                oldValue = Status.valueOf("DONE");
                status = epic.subtasks.get(subtask).status;

            }
        }

        epic.status = status;
    }

    public void addSubtask(Subtask subtask) {
        subtask.id = generateId();
        subtask.status = Status.NEW;
        subtasks.put(subtask.id, subtask);

        if (epics.get(subtask.idEpic) != null) {
            epics.get(subtask.idEpic).subtasks.put(subtask.id, subtask);
            updateStatus(epics.get(subtask.idEpic));
        }
    }

    public void updateSubtask(Subtask subtask, String name, String description, Status status) {
        if (name != null && !name.isEmpty()) {
            subtask.name = name;
        }

        if (description != null && !description.isEmpty()) {
            subtask.description = description;
        }

        subtask.status = status;
        subtasks.put(subtask.getId(), subtask);

        if (epics.get(subtask.idEpic) != null) {
            updateStatus(epics.get(subtask.idEpic));
        }
    }

    public int generateId() {
        count++;
        return count;
    }

    public void getTasks() {
        System.out.println("Задачи:");
        for (int task : tasks.keySet()) {
            System.out.println(tasks.get(task));
        }
    }

    public void getEpics() {
        System.out.println("Эпики:");
        for (int epic : epics.keySet()) {
            System.out.println(epics.get(epic));
        }
    }

    public void getSubtasks() {
        System.out.println("Подзадачи:");
        for (int subtask : subtasks.keySet()) {
            System.out.println(subtasks.get(subtask));
        }
    }

    public void getSubtasksByEpic(int id) {
        if (epics.containsKey(id)) {
            System.out.println("Подзадачи в рамках эпика " + id + ": ");
            for (int subtask : epics.get(id).subtasks.keySet()) {
                System.out.println(subtasks.get(subtask));
            }
        } else {
            System.out.println("Эпик " + id + " не найден.");
        }
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Задача " + id + " удалена.");
        } else {
            System.out.println("Задача " + id + " не найдена.");
        }
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            epics.remove(id);
            System.out.println("Эпик " + id + " удален.");
        } else {
            System.out.println("Эпик " + id + " не найден.");
        }
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            System.out.println("Подзадача " + id + " удалена.");
        } else {
            System.out.println("Подзадача " + id + " не найдена.");
        }
    }
}
