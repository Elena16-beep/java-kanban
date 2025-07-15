package service;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class HttpTaskServerTest {
    // создаём экземпляр InMemoryTaskManager
    public TaskManager taskManager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    public HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public final Gson gson = Managers.getGson();

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
}