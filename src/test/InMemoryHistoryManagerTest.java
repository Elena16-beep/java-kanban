package test;

import model.Task;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void addTaskToHistory() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    void getHistory() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);

        for (int i = 1; i <= 15; i++) {
            historyManager.add(task);
        }

        System.out.println(historyManager.getHistory());
        assertEquals(10, historyManager.getHistory().size(), "Размер истории ограничен 10 задачами");
        assertEquals(task, historyManager.getHistory().get(0));
        assertEquals(task, historyManager.getHistory().get(9));
    }
}