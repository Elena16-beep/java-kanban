package handlers;

import com.google.gson.Gson;
import model.Subtask;
import service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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

        if (path.matches("^/subtasks/\\d+$")) {
            String[] split = path.split("/");
            int subtaskId = Integer.parseInt(split[2]);
            Optional<Subtask> subtaskOpt = taskManager.getSubtaskById(subtaskId);

            if (subtaskOpt.isEmpty()) {
                sendResponse(h, "Подзадача " + subtaskId + " не найдена", 404);
            } else {
                sendText(h, gson.toJson(subtaskOpt.get()), 200);
            }
        } else if (path.equals("/subtasks")) {
            sendText(h, gson.toJson(taskManager.getSubtasksList()), 200);
        } else {
            sendNotFound(h);
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        InputStream body = h.getRequestBody();
        String requestBody = new String(body.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);

        if (subtask.getId() != -1 && subtask.getId() != 0) {
            int subtaskId = subtask.getId();

            if (taskManager.getSubtaskById(subtaskId).isEmpty()) {
                sendResponse(h, "Подзадача " + subtaskId + " не найдена", 404);
                return;
            }

            subtask.setId(subtaskId);

            try {
                taskManager.updateSubtask(subtask, subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getDuration(), subtask.getStartTime());
                sendText(h, gson.toJson(subtask), 201);
            } catch (IllegalArgumentException e) {
                sendHasInteractions(h);
            }
        } else {
            try {
                taskManager.addSubtask(subtask);
                sendText(h, gson.toJson(subtask), 201);
            } catch (IllegalArgumentException e) {
                sendHasInteractions(h);
            }
        }
    }

    private void handleDelete(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();
        if (path.matches("^/subtasks/\\d+$")) {
            String[] parts = path.split("/");
            int subtaskId = Integer.parseInt(parts[2]);

            if (taskManager.getSubtaskById(subtaskId).isEmpty()) {
                sendResponse(h, "Подзадача " + subtaskId + " не найдена", 404);
                return;
            }

            taskManager.deleteSubtaskById(subtaskId);
            sendResponse(h, "Подзадача " + subtaskId + " удалена", 200);
        } else {
            sendNotFound(h);
        }
    }
}