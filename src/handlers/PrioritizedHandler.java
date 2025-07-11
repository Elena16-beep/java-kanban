package handlers;

import com.google.gson.Gson;
import service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        if (h.getRequestMethod().equals("GET")) {
            sendText(h, gson.toJson(taskManager.getPrioritizedTasks()), 200);
        } else {
            sendNotFound(h);
        }
    }
}
