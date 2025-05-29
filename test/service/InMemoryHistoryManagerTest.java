package service;

import model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void addTaskToHistory() {
        Task task1 = new Task("Test1 addNewTask", "Test1 addNewTask description");
        Task task2 = new Task("Test2 addNewTask", "Test2 addNewTask description");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // Проверяем, что при добавлении задачи второй раз, в истории не будет её дубликата

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task2, historyManager.getHistory().get(0)); // Проверяем порядок просмотра задач
        assertEquals(task1, historyManager.getHistory().get(1)); // Проверяем обновление позиции задачи при повторном просмотре
    }

    @Test
    void deleteTaskFromHistory() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.deleteTasks(); // Проверяем, что при удалении задачи, она удалиться из истории просмотров

        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void getHistory() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);

        for (int i = 1; i <= 2; i++) {
            historyManager.add(task);
        }

        assertEquals(task, historyManager.getHistory().get(0));
        assertEquals(1, historyManager.getHistory().size());
    }
}