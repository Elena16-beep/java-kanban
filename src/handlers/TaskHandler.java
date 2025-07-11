package handlers;

import com.google.gson.Gson;
import service.TaskManager;
import model.Task;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
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

        if (path.matches("^/tasks/\\d+$")) {
            String[] split = path.split("/");
            int taskId = Integer.parseInt(split[2]);
            Optional<Task> taskOpt = taskManager.getTaskById(taskId);

            if (taskOpt.isEmpty()) {
                sendResponse(h, "Задача " + taskId + " не найдена", 404);
            } else {
                sendText(h, gson.toJson(taskOpt.get()), 200);
            }
        } else if (path.equals("/tasks")) {
            sendText(h, gson.toJson(taskManager.getTasksList()), 200);
        } else {
            sendNotFound(h);
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        InputStream body = h.getRequestBody();
        String requestBody = new String(body.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(requestBody, Task.class);

        if (task.getId() != -1 && task.getId() != 0) {
            int taskId = task.getId();

            System.out.println("taskId " + taskId);

            if (taskManager.getTaskById(taskId).isEmpty()) {
                sendResponse(h, "Задача " + taskId + " не найдена", 404);
                return;
            }

            task.setId(taskId);
            taskManager.updateTask(task, task.getName(), task.getDescription(), task.getStatus(), task.getDuration(), task.getStartTime());
            sendText(h, gson.toJson(task), 201);
        } else {
            taskManager.addTask(task);
            sendText(h, gson.toJson(task), 201);
        }
    }

    private void handleDelete(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();

        if (path.matches("^/tasks/\\d+$")) {
            String[] split = path.split("/");
            int taskId = Integer.parseInt(split[2]);

            if (taskManager.getTaskById(taskId).isEmpty()) {
                sendResponse(h, "Задача " + taskId + " не найдена", 404);
                return;
            }

            taskManager.deleteTaskById(taskId);
            sendResponse(h, "Задача " + taskId + " удалена", 200);
        } else {
            sendNotFound(h);
        }
    }
}