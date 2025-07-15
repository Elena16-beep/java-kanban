package handlers;

import com.google.gson.reflect.TypeToken;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;
import service.HttpTaskServerTest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskManagerTasksTest extends HttpTaskServerTest {

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(1), LocalDateTime.parse("2025-01-01 12:00", formatter));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = taskManager.getTasksList();

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
                .uri(URI.create("http://localhost:8081/tasks"))
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
                .uri(URI.create("http://localhost:8081/tasks/24"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
        task2.setId(22);
        String postTask = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/22"))
                .POST(HttpRequest.BodyPublishers.ofString(postTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTasksList().size());
        assertTrue(taskManager.getTaskById(22).isPresent());
        assertEquals(task1, taskManager.getTaskById(22).get());
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 14", "Testing task 14",
                Status.NEW, Duration.ofMinutes(14), LocalDateTime.parse("2025-01-14 12:14", formatter));
        taskManager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/25"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    void getTaskByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/99"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача 99 не найдена", response.body());
    }

    @Test
    void addTaskNotAcceptable() throws IOException, InterruptedException {
        Task task1 = new Task("Test 15", "Testing task 15",
                Status.NEW, Duration.ofMinutes(60), LocalDateTime.parse("2025-01-01 12:00", formatter));
        taskManager.addTask(task1);
        Task task2 = new Task("Test 16", "Testing task 16",
                Status.NEW, Duration.ofMinutes(13), LocalDateTime.parse("2025-01-01 12:30", formatter));
        String taskJson = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals("Задача пересекается с существующими", response.body());
    }

    @Test
    void addTaskInternalServerError() throws IOException, InterruptedException {
        String jsonString = "{\"name\":\"Test 15\"," +
                "\"description\":\"Testing task 17\"," +
                "\"status\":\"NEW\"," +
                "\"duration\":\"60\"," +
                "\"startTime\":\"2025-06-21 01:00\"}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
        assertEquals("Text cannot be parsed to a Duration", response.body());
    }
}
