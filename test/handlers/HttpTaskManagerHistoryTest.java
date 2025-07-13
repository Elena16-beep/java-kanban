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

public class HttpTaskManagerHistoryTest extends HttpTaskServerTest {

    public HttpTaskManagerHistoryTest() throws IOException {
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(1), LocalDateTime.parse("2025-01-01 12:01", formatter));
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, history.size());
    }
}
