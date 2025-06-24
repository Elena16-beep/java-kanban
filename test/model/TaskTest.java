package model;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Test
    void addNewTask() {
        Task task2 = new Task(5, "Test2 addNewTask", "Test2 addNewTask description", Status.NEW, Duration.ofMinutes(13), LocalDateTime.parse("03.03.2025 03:00", formatter));
        Task task3 = new Task(5, "Test3 addNewTask", "Test3 addNewTask description", Status.IN_PROGRESS, Duration.ofMinutes(33), LocalDateTime.parse("01.03.2025 01:00", formatter));

        assertEquals(task2, task3, "Задачи не совпадают.");
    }
}