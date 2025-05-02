package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTasks = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return historyTasks;
    }

    @Override
    public void add(Task task) {
        if (historyTasks.size() >= 10) {
            historyTasks.remove(0);
        }
        historyTasks.add(task);
    }
}
