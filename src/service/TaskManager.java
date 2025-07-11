package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {
    void addTask(Task task);

    void updateTask(Task task, String name, String description, Status status, Duration duration, LocalDateTime startTime);

    void addEpic(Epic epic);

    void updateEpic(Epic epic, String name, String description);

    void updateStatus(Epic epic);

    void addSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask, String name, String description, Status status, Duration duration, LocalDateTime startTime);

    int generateId();

    HashMap<Integer, Task> getTasks();

    List<Task> getTasksList();

    HashMap<Integer, Epic> getEpics();

    List<Epic> getEpicsList();

    HashMap<Integer, Subtask> getSubtasks();

    List<Subtask> getSubtasksList();

    Optional<Task> getTaskById(int id);

    Optional<Epic> getEpicById(int id);

    Optional<Subtask> getSubtaskById(int id);

    HashMap<Integer, Subtask> getSubtasksByEpic(int id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    void setCurrentCount(int num);
}
