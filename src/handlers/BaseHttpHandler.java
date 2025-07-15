package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);

        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, response.length);
        h.getResponseBody().write(response);
        h.close();
    }

    protected void sendResponse(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);

        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendResponse(h, "Объект не найден", 404);
    }

    protected void sendError(HttpExchange h, String errorMessage) throws IOException {
        sendResponse(h, errorMessage, 500);
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        byte[] response = "Задача пересекается с существующими".getBytes(StandardCharsets.UTF_8);

        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, response.length);
        h.getResponseBody().write(response);
        h.close();
    }
}