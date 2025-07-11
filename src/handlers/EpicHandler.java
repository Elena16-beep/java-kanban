package handlers;

import com.google.gson.Gson;
import service.TaskManager;
import model.Epic;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            switch (h.getRequestMethod()) {
                case "GET":
                    handleGet(h);
                    break;
                case "POST":
                    handlePost(h);
                    break;
                case "DELETE":
                    handleDelete(h);
                    break;
                default:
                    sendNotFound(h);
                    break;
            }
        } catch (Exception e) {
            sendError(h, e.getMessage());
        }
    }

    private void handleGet(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();

        if (path.matches("^/epics/\\d+$")) {
            String[] split = path.split("/");
            int epicId = Integer.parseInt(split[2]);
            Optional<Epic> epicOpt = taskManager.getEpicById(epicId);

            if (epicOpt.isEmpty()) {
                sendResponse(h, "Эпик " + epicId + " не найден", 404);
            } else {
                sendText(h, gson.toJson(epicOpt.get()), 200);
            }
        } else if (path.equals("/epics")) {
            sendText(h, gson.toJson(taskManager.getEpicsList()), 200);
        } else {
            sendNotFound(h);
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        InputStream body = h.getRequestBody();
        String requestBody = new String(body.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(requestBody, Epic.class);

        if (epic.getId() != -1 && epic.getId() != 0) {
            int epicId = epic.getId();

            if (taskManager.getEpicById(epicId).isEmpty()) {
                sendResponse(h, "Эпик " + epicId + " не найден", 404);
                return;
            }

            epic.setId(epicId);
            System.out.println("Эпик " + taskManager.getEpicById(epicId).get().getSubtasks().values());
            taskManager.updateEpic(epic, epic.getName(), epic.getDescription());
            sendResponse(h, gson.toJson(epic), 201);
        } else {
            taskManager.addEpic(epic);
            sendResponse(h, gson.toJson(epic), 201);
        }
    }

    private void handleDelete(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();
        if (path.matches("^/epics/\\d+$")) {
            String[] split = path.split("/");
            int epicId = Integer.parseInt(split[2]);

            if (taskManager.getEpicById(epicId).isEmpty()) {
                sendResponse(h, "Эпик " + epicId + " не найден", 404);
                return;
            }

            taskManager.deleteEpicById(epicId);
            sendResponse(h, "Эпик " + epicId + " удален", 200);
        } else {
            sendNotFound(h);
        }
    }
}