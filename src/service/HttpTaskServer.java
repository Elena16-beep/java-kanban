package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import handlers.TaskHandler;
import handlers.SubtaskHandler;
import handlers.EpicHandler;
import handlers.HistoryHandler;
import handlers.PrioritizedHandler;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final TaskManager taskManager;
    private final Gson gson;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = Managers.getGson();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8081), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        httpServer.start();
        System.out.println("Сервер запущен на 8081 порту");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();

//        Task task = new Task("Тест", "Тест");
//        LocalDateTime time = LocalDateTime.of(2025, 5, 18, 9, 30);
//        Duration duration = Duration.ofMinutes(12);
//        task.setStartTime(time);
//        task.setDuration(duration);
//        taskManager.addTask(task);
//        taskManager.getTaskById(1);
    }
}