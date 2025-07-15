package handlers;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import org.junit.jupiter.api.Test;
import service.HttpTaskServerTest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskManagerEpicsTest extends HttpTaskServerTest {

    public HttpTaskManagerEpicsTest() throws IOException {
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = taskManager.getEpicsList();

        assertEquals(201, response.statusCode());
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertEquals("Test 1", epics.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 3", "Testing task 3");
        taskManager.addEpic(epic1);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/epics/4"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epic2 = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertNotNull(epic2);
        assertEquals(epic1, epic2);
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 4", "Testing task 4");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Test 5", "Testing task 5");
        epic2.setId(3);
        String postTask = gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/epics/3"))
                .POST(HttpRequest.BodyPublishers.ofString(postTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getEpicsList().size());
        assertTrue(taskManager.getEpicById(3).isPresent());
        assertEquals(epic1, taskManager.getEpicById(3).get());
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/epics/5"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    void getEpicByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/epics/99"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Эпик 99 не найден", response.body());
    }

    @Test
    void addEpicInternalServerError() throws IOException, InterruptedException {
        String jsonString = "{\"name\":\"Test 15\"," +
                "\"description\":\"Testing task 17\"," +
                "\"status\":\"NEW\"," +
                "\"duration\":\"60\"," +
                "\"startTime\":\"2025-06-21 01:00\"}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
        assertEquals("Text cannot be parsed to a Duration", response.body());
    }
}
