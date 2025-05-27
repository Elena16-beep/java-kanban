package tests.model;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void addNewTask() {
        Task task2 = new Task(5, "Test2 addNewTask", "Test2 addNewTask description", Status.NEW);
        Task task3 = new Task(5, "Test3 addNewTask", "Test3 addNewTask description", Status.IN_PROGRESS);

        assertEquals(task2, task3, "Задачи не совпадают.");
    }
}