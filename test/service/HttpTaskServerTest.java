package service;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class HttpTaskServerTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Gson gson = Managers.getGson();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(1), LocalDateTime.parse("2025-01-01 12:00", formatter));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasks = taskManager.getTasksList();

        // проверяем код ответа
        assertEquals(201, response.statusCode());
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 7", "Testing task 7",
                Status.NEW, Duration.ofMinutes(7), LocalDateTime.parse("2025-01-07 12:07", formatter));
        taskManager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        Task task3 = new Task("Test 8", "Testing task 8",
                Status.NEW, Duration.ofMinutes(7), LocalDateTime.parse("2025-01-08 12:08", formatter));
        taskManager.addTask(task3);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/10"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());
        System.out.println(response);
        System.out.println(response.body());
        Task task4 = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertNotNull(task4);
        assertEquals(task3, task4);
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 12", "Testing task 12",
                Status.NEW, Duration.ofMinutes(12), LocalDateTime.parse("2025-01-12 12:12", formatter));
        taskManager.addTask(task1);
        Task task2 = new Task("Test 13", "Testing task 13",
                Status.NEW, Duration.ofMinutes(13), LocalDateTime.parse("2025-01-13 12:13", formatter));
        task2.setId(7);
        String postTask = gson.toJson(task2);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/7"))
                .POST(HttpRequest.BodyPublishers.ofString(postTask))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());
        System.out.println(response);
        System.out.println(response.body());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTasksList().size());
        assertEquals(task1, taskManager.getTaskById(7).get());
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 14", "Testing task 14",
                Status.NEW, Duration.ofMinutes(14), LocalDateTime.parse("2025-01-14 12:14", formatter));
        taskManager.addTask(task);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/11"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());
        System.out.println(response);
        System.out.println(response.body());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasksList().size());

    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = taskManager.getEpicsList();

        assertEquals(201, response.statusCode());
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertEquals("Test 2", epics.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 9", "Testing task 9");
        taskManager.addEpic(epic);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test 3", "Testing subtask 3",
                epic.getId(), Duration.ofMinutes(1), LocalDateTime.parse("2025-01-03 12:03", formatter));
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = taskManager.getSubtasksList();

        assertEquals(201, response.statusCode());
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        assertEquals("Test 3", subtasks.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    void getSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 10", "Testing epic 10");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test 11", "Testing subtask 11",
                1, Duration.ofMinutes(11), LocalDateTime.parse("2025-01-11 12:11", formatter));
        taskManager.addSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        Task task = new Task("Test 4", "Testing task 4",
                Status.NEW, Duration.ofMinutes(1), LocalDateTime.parse("2025-01-04 12:04", formatter));
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, history.size());
    }

    @Test
    void getPrioritizedTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Test 5", "Testing task 5",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.parse("2025-01-05 12:05", formatter));
        taskManager.addTask(task1);
        Task task2 = new Task("Test 6", "Testing task 6",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.parse("2025-01-06 12:06", formatter));
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertNotNull(prioritizedTasks);
        assertEquals(2, prioritizedTasks.size());
    }
}