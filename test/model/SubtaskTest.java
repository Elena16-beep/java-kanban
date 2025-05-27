package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void addNewSubtask() {
        Epic epic1 = new Epic(2, "Test1 addNewEpic", "Test1 addNewEpic description", Status.DONE);
        Subtask subtask1 = new Subtask(3, "Test2 addNewSubtask", "Test2 addNewSubtask description", Status.IN_PROGRESS, 2);
        Subtask subtask2 = new Subtask(3, "Test3 addNewSubtask", "Test3 addNewSubtask description", Status.NEW, 2);

        assertEquals(subtask1, subtask2, "Подзадачи не совпадают.");
    }

}