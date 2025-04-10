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
        Status status;
        Status newValue = null;
        Status inProgressValue = null;
        Status doneValue = null;

        for (int subtask : epic.subtasks.keySet()) {
            if (epic.subtasks.get(subtask).status == Status.valueOf("NEW")) {
                newValue = Status.valueOf("NEW");
            } else if (epic.subtasks.get(subtask).status == Status.valueOf("IN_PROGRESS")) {
                inProgressValue = Status.valueOf("IN_PROGRESS");
            } if (epic.subtasks.get(subtask).status == Status.valueOf("DONE")) {
                doneValue = Status.valueOf("DONE");
            }
        }

        if (doneValue != null && doneValue == Status.valueOf("DONE") &&
                inProgressValue == null && newValue == null) {
            status = Status.valueOf("DONE");
        } else if (newValue != null && newValue == Status.valueOf("NEW") &&
                inProgressValue == null && doneValue == null) {
            status = Status.valueOf("NEW");
        } else {
            status = Status.valueOf("IN_PROGRESS");
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

    public HashMap<Integer, Task> getTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Задачи не найдены");

            return null;
        }

        System.out.println("Задачи:");

        for (int task : tasks.keySet()) {
            System.out.println(tasks.get(task));
        }

        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        if (epics.isEmpty()) {
            System.out.println("Эпики не найдены");

            return null;
        }

        System.out.println("Эпики:");

        for (int task : epics.keySet()) {
            System.out.println(epics.get(task));
        }

        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Подзадачи не найдены");

            return null;
        }

        System.out.println("Подзадачи:");

        for (int task : subtasks.keySet()) {
            System.out.println(subtasks.get(task));
        }

        return subtasks;
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            System.out.println(tasks.get(id));

            return tasks.get(id);
        } else {
            System.out.println("Задача " + id + " не найдена.");

            return null;
        }
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            System.out.println(epics.get(id));

            return epics.get(id);
        } else {
            System.out.println("Эпик " + id + " не найден.");

            return null;
        }
    }

    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            System.out.println(subtasks.get(id));

            return subtasks.get(id);
        } else {
            System.out.println("Подзадача " + id + " не найдена.");

            return null;
        }
    }

    public HashMap<Integer, Subtask> getSubtasksByEpic(int id) {
        if (epics.containsKey(id)) {
            if (!epics.get(id).subtasks.isEmpty()) {
                System.out.println("Подзадачи в рамках эпика " + id + ": ");

                for (int subtask : epics.get(id).subtasks.keySet()) {
                    System.out.println(subtasks.get(subtask));

                    return subtasks;
                }
            } else {
                System.out.println("Подзадачи в рамках эпика " + id + " не найдены");

                return null;
            }
        } else {
            System.out.println("Эпик " + id + " не найден.");

            return null;
        }
        return null;
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
            if (!epics.get(id).subtasks.isEmpty()) {
                for (int subtask : epics.get(id).subtasks.keySet()) {
                    subtasks.remove(subtask);
                }

                System.out.println("Эпик " + id + " и его подзадачи удалены.");
            } else {
                System.out.println("Эпик " + id + " удален.");
            }

            epics.remove(id);
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
