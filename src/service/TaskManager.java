package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void updateTask(Task task, String name, String description, Status status);

    void addEpic(Epic epic);

    void updateEpic(Epic epic, String name, String description);

    void updateStatus(Epic epic);

    void addSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask, String name, String description, Status status);

    int generateId();

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    HashMap<Integer, Subtask> getSubtasksByEpic(int id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    List<Task> getHistory();
}
