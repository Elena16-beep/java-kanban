package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int count = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task, String name, String description, Status status) {
        if (name != null && !name.isEmpty()) {
            task.setName(name);
        }

        if (description != null && !description.isEmpty()) {
            task.setDescription(description);
        }

        task.setStatus(status);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic, String name, String description) {
        if (name != null && !name.isEmpty()) {
            epic.setName(name);
        }

        if (description != null && !description.isEmpty()) {
            epic.setDescription(description);
        }

        updateStatus(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateStatus(Epic epic) {
        Status status;
        Status newValue = null;
        Status inProgressValue = null;
        Status doneValue = null;

        for (int subtask : epic.getSubtasks().keySet()) {
            if (epic.getSubtasks().get(subtask).getStatus() == Status.valueOf("NEW")) {
                newValue = Status.valueOf("NEW");
            } else if (epic.getSubtasks().get(subtask).getStatus() == Status.valueOf("IN_PROGRESS")) {
                inProgressValue = Status.valueOf("IN_PROGRESS");
            } if (epic.getSubtasks().get(subtask).getStatus() == Status.valueOf("DONE")) {
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

        epic.setStatus(status);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtask.setStatus(Status.NEW);

        if (subtask.getIdEpic() != subtask.getId()) {
            subtasks.put(subtask.getId(), subtask);

            if (epics.get(subtask.getIdEpic()) != null) {
                epics.get(subtask.getIdEpic()).getSubtasks().put(subtask.getId(), subtask);
                updateStatus(epics.get(subtask.getIdEpic()));
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, String name, String description, Status status) {
        if (name != null && !name.isEmpty()) {
            subtask.setName(name);
        }

        if (description != null && !description.isEmpty()) {
            subtask.setDescription(description);
        }

        subtask.setStatus(status);
        subtasks.put(subtask.getId(), subtask);

        if (epics.get(subtask.getIdEpic()) != null) {
            updateStatus(epics.get(subtask.getIdEpic()));
        }
    }

    @Override
    public int generateId() {
        count++;

        return count;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Задачи не найдены");
        } else {

            System.out.println("Задачи:");

            for (int task : tasks.keySet()) {
                System.out.println(tasks.get(task));
            }
        }

        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        if (epics.isEmpty()) {
            System.out.println("Эпики не найдены");
        } else {

            System.out.println("Эпики:");

            for (int task : epics.keySet()) {
                System.out.println(epics.get(task));
            }
        }

        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Подзадачи не найдены");
        } else {

            System.out.println("Подзадачи:");

            for (int task : subtasks.keySet()) {
                System.out.println(subtasks.get(task));
            }
        }

        return subtasks;
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            System.out.println(tasks.get(id));
            historyManager.add(tasks.get(id));

            return tasks.get(id);
        } else {
            System.out.println("Задача " + id + " не найдена.");

            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            System.out.println(epics.get(id));
            historyManager.add(epics.get(id));

            return epics.get(id);
        } else {
            System.out.println("Эпик " + id + " не найден.");

            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            System.out.println(subtasks.get(id));
            historyManager.add(subtasks.get(id));

            return subtasks.get(id);
        } else {
            System.out.println("Подзадача " + id + " не найдена.");

            return null;
        }
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasksByEpic(int id) {
        if (epics.containsKey(id)) {
            if (!epics.get(id).getSubtasks().isEmpty()) {
                System.out.println("Подзадачи в рамках эпика " + id + ": ");

                for (int subtask : epics.get(id).getSubtasks().keySet()) {
                    System.out.println(subtasks.get(subtask));
                }
            } else {
                System.out.println("Подзадачи в рамках эпика " + id + " не найдены");
            }
        } else {
            System.out.println("Эпик " + id + " не найден.");
        }

        return subtasks;
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        deleteSubtasks();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Задача " + id + " удалена.");
        } else {
            System.out.println("Задача " + id + " не найдена.");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            if (!epics.get(id).getSubtasks().isEmpty()) {
                for (int subtask : epics.get(id).getSubtasks().keySet()) {
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

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            System.out.println("Подзадача " + id + " удалена.");
        } else {
            System.out.println("Подзадача " + id + " не найдена.");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
