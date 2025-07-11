package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;
    Path temp;

    @BeforeEach
    void setUp() throws ManagerSaveException {
        try {
            temp = Files.createTempFile("ttt", ".csv");
            fileBackedTaskManager = new FileBackedTaskManager(new File(String.valueOf(temp)));
            fileBackedTaskManager.setCurrentCount(0);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время создания временного файла: " + e.getMessage());
        }
    }

    @Test
    void saveEmptyFile() throws ManagerSaveException {
        fileBackedTaskManager = new FileBackedTaskManager(new File("src/files/empty.csv"));
        String line;
        List<String> lines = new ArrayList<>();

        fileBackedTaskManager.save(); // проверка сохранения пустого файла

        try (BufferedReader reader = new BufferedReader(new FileReader("src/files/empty.csv", StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                line = reader.readLine();
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время загрузки из файла: " + e.getMessage());
        }

        assertEquals(1, lines.size());
        assertEquals("id,type,name,status,description,epic,duration,startTime,endTime", lines.get(0));
        assertTrue(fileBackedTaskManager.getTasks().isEmpty());
        assertTrue(fileBackedTaskManager.getEpics().isEmpty());
        assertTrue(fileBackedTaskManager.getSubtasks().isEmpty());
    }

    @Test
    void loadFromEmptyFile() {
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("src/files/empty.csv"));

        assertTrue(fileBackedTaskManager.getTasks().isEmpty()); // проверка загрузки пустого файла
        assertTrue(fileBackedTaskManager.getEpics().isEmpty());
        assertTrue(fileBackedTaskManager.getSubtasks().isEmpty());
    }

    @Test
    void saveTasks() throws ManagerSaveException {
        Task task = new Task("Test1 addNewTask", "Test1 addNewTask description");
        Epic epic = new Epic("Test2 addNewEpic", "Test2 addNewEpic description");
        Subtask subtask = new Subtask("Test3 addNewSubtask", "Test3 addNewSubtask description", 2);
        String line;
        List<String> lines = new ArrayList<>();

        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addEpic(epic);
        fileBackedTaskManager.addSubtask(subtask);

        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(temp), StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                line = reader.readLine();
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время загрузки из файла: " + e.getMessage());
        }

        assertEquals(4, lines.size());
        assertEquals("id,type,name,status,description,epic,duration,startTime,endTime", lines.get(0));
        assertEquals("1,TASK,Test1 addNewTask,NEW,Test1 addNewTask description", lines.get(1));
        assertEquals("2,EPIC,Test2 addNewEpic,NEW,Test2 addNewEpic description", lines.get(2));
        assertEquals("3,SUBTASK,Test3 addNewSubtask,NEW,Test3 addNewSubtask description,2", lines.get(3));
        assertNotNull(fileBackedTaskManager.getTasks(), "Задачи не возвращаются.");
        assertEquals(1, fileBackedTaskManager.getTasks().size(), "Неверное количество задач.");
        assertEquals(task, fileBackedTaskManager.getTasks().get(1), "Задачи не совпадают.");
        assertFalse(fileBackedTaskManager.getTasks().isEmpty());

        fileBackedTaskManager.deleteTaskById(1);

        assertTrue(fileBackedTaskManager.getTasks().isEmpty());
    }

    @Test
    void loadFromFile() {
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("src/files/test.csv"));
        Task task = new Task("Test1 addNewTask", "Test1 addNewTask description");

        fileBackedTaskManager.addTask(task);

        assertEquals(2, fileBackedTaskManager.getTasks().size());
        assertEquals(1, fileBackedTaskManager.getEpics().size());
        assertEquals(1, fileBackedTaskManager.getSubtasks().size());
        assertEquals(4, task.getId());
        assertEquals(task, fileBackedTaskManager.getTasks().get(4));
    }
}