package test;

import model.Epic;
import model.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void addNewEpic() {
        Epic epic1 = new Epic(2, "Test1 addNewEpic", "Test1 addNewEpic description", Status.DONE);
        Epic epic2 = new Epic(2, "Test2 addNewEpic", "Test2 addNewEpic description", Status.IN_PROGRESS);

        assertEquals(epic1, epic2, "Эпики не совпадают.");
    }
}