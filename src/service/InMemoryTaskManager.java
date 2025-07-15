package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected static int count = 0;
    protected static HashMap<Integer, Task> tasks;
    protected static HashMap<Integer, Subtask> subtasks;
    protected static HashMap<Integer, Epic> epics;
    protected final HistoryManager historyManager;
    protected Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        if (hasTaskIntersection(task)) {
            throw new IllegalArgumentException("Задачи пересекаются");
        }

        task.setId(generateId());
        task.setStatus(Status.NEW);

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        if (name != null && !name.isEmpty()) {
            task.setName(name);
        }

        if (description != null && !description.isEmpty()) {
            task.setDescription(description);
        }

        if (duration != null) {
            task.setDuration(duration);
        }

        if (startTime != null) {
            task.setStartTime(startTime);
        }

        task.setStatus(status);

        if (hasTaskIntersection(task)) {
            throw new IllegalArgumentException("Задачи пересекаются");
        }

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

        for (Subtask subtask : epics.get(epic.getId()).getSubtasks().values()) {
            epic.getSubtasks().put(subtask.getId(), subtask);
            updateStatus(epic);
            updateEpicDateTime(epic);
        }

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
        if (hasTaskIntersection(subtask)) {
            throw new IllegalArgumentException("Подзадачи пересекаются");
        }

        subtask.setId(generateId());
        subtask.setStatus(Status.NEW);

        if (subtask.getIdEpic() != subtask.getId()) {
            subtasks.put(subtask.getId(), subtask);

            if (epics.get(subtask.getIdEpic()) != null && epics.get(subtask.getIdEpic()).getSubtasks() != null) {
                epics.get(subtask.getIdEpic()).getSubtasks().put(subtask.getId(), subtask);
                updateStatus(epics.get(subtask.getIdEpic()));
                updateEpicDateTime(epics.get(subtask.getIdEpic()));
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        if (name != null && !name.isEmpty()) {
            subtask.setName(name);
        }

        if (description != null && !description.isEmpty()) {
            subtask.setDescription(description);
        }

        if (duration != null) {
            subtask.setDuration(duration);
        }

        if (startTime != null) {
            subtask.setStartTime(startTime);
        }

        subtask.setStatus(status);

        if (hasTaskIntersection(subtask)) {
            throw new IllegalArgumentException("Подзадачи пересекаются");
        }

        subtasks.put(subtask.getId(), subtask);

        if (epics.get(subtask.getIdEpic()) != null && epics.get(subtask.getIdEpic()).getSubtasks() != null) {
            updateStatus(epics.get(subtask.getIdEpic()));
            updateEpicDateTime(epics.get(subtask.getIdEpic()));
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
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        } else {
            System.out.println("Задача " + id + " не найдена.");
        }

        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        } else {
            System.out.println("Эпик " + id + " не найден.");
        }

        return Optional.ofNullable(epics.get(id));
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        } else {
            System.out.println("Подзадача " + id + " не найдена.");
        }

        return Optional.ofNullable(subtasks.get(id));
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
                    updateStatus(epics.get(epicId));
                    updateEpicDateTime(epics.get(epicId));
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
        count = num;
    }

    public void updateEpicDateTime(Epic epic) {
        Duration duration = null;;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        List<Subtask> subtasksSorted = epic.getSubtasks().values().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .sorted(Comparator.comparing(Subtask::getStartTime))
                .collect(Collectors.toList());

        for (Subtask subtaskSorted : subtasksSorted) {
            if (startTime == null) {
                startTime = subtaskSorted.getStartTime();
            }

            if (endTime == null) {
                endTime = subtaskSorted.getEndTime();
            }

            if (subtaskSorted.getStartTime() != null && startTime != null && subtaskSorted.getStartTime().isBefore(startTime)) {
                startTime = subtaskSorted.getStartTime();
            }

            if (subtaskSorted.getEndTime() != null && endTime != null && subtaskSorted.getEndTime().isAfter(endTime)) {
                endTime = subtaskSorted.getEndTime();
            }
        }

        if (startTime != null && endTime != null) {
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            duration = Duration.between(startTime, endTime);

            if (duration != null) {
                epic.setDuration(duration);
            }
        }
    }

    public Set<Task> getPrioritizedTasks() {
        List<Task> tasksList = new ArrayList<Task>();

        tasksList.addAll(getTasks().values());
        tasksList.addAll(getSubtasks().values());

        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

        for (Task t : tasksList) {
            if (t.getStartTime() != null) {
                prioritizedTasks.add(t);
            }
        }

        return prioritizedTasks;
    }

    public boolean isTaskOverlap(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null ||
                t1.getEndTime() == null || t2.getEndTime() == null) {
            return false;
        }

        return !(t1.getEndTime().isBefore(t2.getStartTime()) || t2.getEndTime().isBefore(t1.getStartTime()));
    }

    public boolean hasTaskIntersection(Task taskToCheck) {
        for (Task task : getPrioritizedTasks()) {
            if (taskToCheck.getId() != task.getId() && isTaskOverlap(task, taskToCheck)) {
                return true;
            }
        }

        return false;
    }
}
