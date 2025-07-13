package handlers;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
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

public class HttpTaskManagerSubtasksTest extends HttpTaskServerTest {

    public HttpTaskManagerSubtasksTest() throws IOException {
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2",
                epic.getId(), Duration.ofMinutes(1), LocalDateTime.parse("2025-01-03 12:03", formatter));
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = taskManager.getSubtasksList();

        assertEquals(201, response.statusCode());
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", subtasks.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    void getSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 3", "Testing epic 3");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test 4", "Testing subtask 4",
                1, Duration.ofMinutes(11), LocalDateTime.parse("2025-01-14 12:11", formatter));
        taskManager.addSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 5", "Testing epic 5");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test 6", "Testing subtask 6",
                1, Duration.ofMinutes(11), LocalDateTime.parse("2025-01-16 12:11", formatter));
        taskManager.addSubtask(subtask1);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/subtasks/14"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtask2 = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());
        assertNotNull(subtask2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 7", "Testing epic 7");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test 8", "Testing subtask 8",
                1, Duration.ofMinutes(11), LocalDateTime.parse("2025-01-18 12:11", formatter));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test 9", "Testing subtask 9",
                1, Duration.ofMinutes(11), LocalDateTime.parse("2025-01-19 12:11", formatter));
        subtask2.setId(16);
        String postTask = gson.toJson(subtask2);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/subtasks/16"))
                .POST(HttpRequest.BodyPublishers.ofString(postTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getSubtasksList().size());
        assertTrue(taskManager.getSubtaskById(16).isPresent());
        assertEquals(subtask1, taskManager.getSubtaskById(16).get());
    }

    @Test
    void deleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 5", "Testing epic 5");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test 6", "Testing subtask 6",
                1, Duration.ofMinutes(11), LocalDateTime.parse("2025-01-16 12:11", formatter));
        taskManager.addSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/subtasks/18"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getSubtasksList().size());
    }

    @Test
    void getSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/subtasks/99"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Подзадача 99 не найдена", response.body());
    }

    @Test
    void addSubtaskNotAcceptable() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 7", "Testing epic 7");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test 8", "Testing subtask 8",
                1, Duration.ofMinutes(120), LocalDateTime.parse("2025-01-18 12:00", formatter));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test 9", "Testing subtask 9",
                1, Duration.ofMinutes(11), LocalDateTime.parse("2025-01-18 13:00", formatter));
        String taskJson = gson.toJson(subtask2);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals("Задача пересекается с существующими", response.body());
    }

    @Test
    void addSubtaskInternalServerError() throws IOException, InterruptedException {
        String jsonString = "{\"name\":\"Test 15\"," +
                "\"description\":\"Testing task 17\"," +
                "\"idEpic\":\"1\"," +
                "\"status\":\"NEW\"," +
                "\"duration\":\"60\"," +
                "\"startTime\":\"2025-06-21 01:00\"}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
        assertEquals("Text cannot be parsed to a Duration", response.body());
    }
}
