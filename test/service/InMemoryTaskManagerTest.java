package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    void addTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final HashMap<Integer, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test2 addNewSubtask", "Test2 addNewSubtask description", 2);
        taskManager.addSubtask(subtask);

        assertNotEquals(taskManager.getEpics().get(1), taskManager.getSubtasks().get(2), "Задачи совпадают.");
        assertEquals(0, taskManager.getSubtasks().size(), "Объект Epic нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test2 addNewSubtask", "Test2 addNewSubtask description", 2);
        taskManager.addSubtask(subtask);

        assertEquals(0, taskManager.getSubtasks().size(), "Нельзя подзадачу сделать своим эпиком");
    }

    @Test
    void addTaskById() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description");
        Task task2 = new Task(1, "Test addNewTask", "Test addNewTask description", Status.DONE);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        final HashMap<Integer, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size());
        assertNotEquals(taskManager.getTaskById(task1.getId()), taskManager.getTaskById(task2.getId()), "Задачи с заданным id и сгенерированным id конфликтуют");
        assertEquals("Test addNewTask", taskManager.getTaskById(task2.getId()).getName(), "Наименование не должно измениться");
        assertEquals("Test addNewTask description", taskManager.getTaskById(task2.getId()).getDescription(), "Описание не должно измениться");
        assertEquals(Status.NEW, taskManager.getTaskById(task2.getId()).getStatus(), "Статус добавленной задачи должен быть new");
        assertEquals(2, task2.getId());
    }

    @Test
    void updateTask() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task1);
        Task task2 = new Task(task1.getId(), "Test updateTask", "Test updateTask description", Status.IN_PROGRESS);
        taskManager.updateTask(task1, "Test updateTask", "Test updateTask description", Status.IN_PROGRESS);

        assertEquals(1, taskManager.getTasks().size());
        assertEquals(task2, taskManager.getTaskById(task1.getId()));
    }

    @Test
    void getTaskById() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task1);

        assertEquals(1, taskManager.getTasks().size());
        assertEquals(task1, taskManager.getTaskById(task1.getId()));
    }

    @Test
    void getTasks() {
        Task task1 = new Task("Test1 addNewTask", "Test1 addNewTask description");
        Task task2 = new Task("Test2 addNewTask", "Test2 addNewTask description");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getTasks().size());
        assertTrue(taskManager.getTasks().containsValue(task1));
        assertTrue(taskManager.getTasks().containsValue(task2));
    }

    @Test
    void deleteTaskById() {
        Task task1 = new Task("Test1 addNewTask", "Test1 addNewTask description");
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.deleteTaskById(task1.getId());

        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, historyManager.getHistory().size());
    }


    @Test
    void deleteTasks() {
        Task task1 = new Task("Test1 addNewTask", "Test1 addNewTask description");
        Task task2 = new Task("Test2 addNewTask", "Test2 addNewTask description");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.deleteTasks();

        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void updateEpic() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        taskManager.addEpic(epic1);
        Subtask subtask = new Subtask("Test2 addNewSubtask", "Test2 addNewSubtask description", 1);
        taskManager.addSubtask(subtask);
        taskManager.updateSubtask(subtask, "Test updateSubtask", "Test updateSubtask description", Status.IN_PROGRESS);
        Epic epic2 = new Epic(epic1.getId(), "Test3 addNewEpic", "Test3 addNewEpic description", Status.NEW);
        taskManager.updateEpic(epic2, "Test updateTask", "Test updateTask description");

        assertEquals(1, taskManager.getEpics().size());
        assertEquals(epic2, taskManager.getEpicById(epic1.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    void getEpicById() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        taskManager.addEpic(epic1);

        assertEquals(1, taskManager.getEpics().size());
        assertEquals(epic1, taskManager.getEpicById(epic1.getId()));
    }

    @Test
    void getEpics() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        Epic epic2 = new Epic("Test2 addNewEpic", "Test2 addNewEpic description");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        assertEquals(2, taskManager.getEpics().size());
        assertTrue(taskManager.getEpics().containsValue(epic1));
        assertTrue(taskManager.getEpics().containsValue(epic2));
    }

    @Test
    void deleteEpicById() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        Subtask subtask1 = new Subtask("Test2 addNewSubtask", "Test2 addNewSubtask description", 1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.getEpicById(epic1.getId());
        taskManager.deleteEpicById(epic1.getId());

        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasksByEpic(epic1.getId()).size());
        assertEquals(0, historyManager.getHistory().size());
    }


    @Test
    void deleteEpics() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        Epic epic2 = new Epic("Test2 addNewEpic", "Test2 addNewEpic description");
        Subtask subtask1 = new Subtask("Test4 addNewSubtask", "Test4 addNewSubtask description", 1);
        Subtask subtask2 = new Subtask("Test5 addNewSubtask", "Test5 addNewSubtask description", 2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.deleteEpics();

        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void updateSubtask() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        Subtask subtask1 = new Subtask("Test2 addNewSubtask", "Test2 addNewSubtask description", 1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        Task subtask2 = new Subtask(subtask1.getId(), "Test updateSubtask", "Test updateSubtask description", Status.DONE, 1);
        taskManager.updateSubtask(subtask1, "Test updateSubtask", "Test updateSubtask description", Status.IN_PROGRESS);

        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubtasks().size());
        assertEquals(subtask2, taskManager.getSubtaskById(subtask1.getId()));
    }

    @Test
    void getSubtaskById() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        Subtask subtask1 = new Subtask("Test2 addNewSubtask", "Test2 addNewSubtask description", 1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);

        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubtasks().size());
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));
    }

    @Test
    void getSubtasks() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        Subtask subtask1 = new Subtask("Test2 addNewSubtask", "Test2 addNewSubtask description", 1);
        Subtask subtask2 = new Subtask("Test3 addNewSubtask", "Test3 addNewSubtask description", 1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(1, taskManager.getEpics().size());
        assertEquals(2, taskManager.getSubtasks().size());
        assertTrue(taskManager.getSubtasks().containsValue(subtask1));
        assertTrue(taskManager.getSubtasks().containsValue(subtask2));
    }

    @Test
    void deleteSubtaskById() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        Subtask subtask1 = new Subtask("Test2 addNewSubtask", "Test2 addNewSubtask description", 1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.deleteSubtaskById(subtask1.getId());

        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, historyManager.getHistory().size());
        assertFalse(epic1.getSubtasks().containsKey(subtask1.getId()));
    }


    @Test
    void deleteSubtasks() {
        Epic epic1 = new Epic("Test1 addNewEpic", "Test1 addNewEpic description");
        Subtask subtask1 = new Subtask("Test2 addNewSubtask", "Test2 addNewSubtask description", 1);
        Subtask subtask2 = new Subtask("Test3 addNewSubtask", "Test3 addNewSubtask description", 1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.deleteSubtasks();

        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, historyManager.getHistory().size());
    }
}