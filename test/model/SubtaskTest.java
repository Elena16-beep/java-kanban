package model;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Test
    void addNewSubtask() {
        Epic epic1 = new Epic(2, "Test1 addNewEpic", "Test1 addNewEpic description", Status.DONE);
        Subtask subtask1 = new Subtask(3, "Test2 addNewSubtask", "Test2 addNewSubtask description", Status.IN_PROGRESS, 2, Duration.ofMinutes(33), LocalDateTime.parse("01.03.2025 01:00", formatter));
        Subtask subtask2 = new Subtask(3, "Test3 addNewSubtask", "Test3 addNewSubtask description", Status.NEW, 2, Duration.ofMinutes(1), LocalDateTime.parse("11.11.2011 11:00", formatter));

        assertEquals(subtask1, subtask2, "Подзадачи не совпадают.");
    }
}