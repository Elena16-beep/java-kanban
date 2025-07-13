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

public class HttpTaskManagerPrioritizedTest extends HttpTaskServerTest {

    public HttpTaskManagerPrioritizedTest() throws IOException {
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
                .uri(URI.create("http://localhost:8081/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertNotNull(prioritizedTasks);
        assertEquals(2, prioritizedTasks.size());
    }
}
