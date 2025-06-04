package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected static int count = 0;
    protected static HashMap<Integer, Task> tasks;
    protected static HashMap<Integer, Subtask> subtasks;
    protected static HashMap<Integer, Epic> epics;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
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
        int newCount = 0, inProgressCount = 0, doneCount = 0;

        for (Subtask subtasks : epic.getSubtasks().values()) {
            switch (subtasks.getStatus()) {
                case NEW -> newCount++;
                case IN_PROGRESS -> inProgressCount++;
                case DONE -> doneCount++;
            }
        }

        if (doneCount != 0 && inProgressCount == 0 && newCount == 0) {
            status = Status.DONE;
        } else if (newCount != 0 && inProgressCount == 0 && doneCount == 0) {
            status = Status.NEW;
        } else {
            status = Status.IN_PROGRESS;
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
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
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
        for (int taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }

        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (int epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }

        epics.clear();
        deleteSubtasks();
    }

    @Override
    public void deleteSubtasks() {
        for (int subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }

        subtasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);

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
                    historyManager.remove(subtask);
                }

                System.out.println("Эпик " + id + " и его подзадачи удалены.");
            } else {
                System.out.println("Эпик " + id + " удален.");
            }

            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Эпик " + id + " не найден.");
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            for (int epicId : epics.keySet()) {
                if (epicId == subtasks.get(id).getIdEpic()) {
                    epics.get(epicId).getSubtasks().remove(id);
                }
            }

            subtasks.remove(id);
            historyManager.remove(id);

            System.out.println("Подзадача " + id + " удалена.");
        } else {
            System.out.println("Подзадача " + id + " не найдена.");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void setCurrentCount(int num) {
        this.count = num;
    }
}
