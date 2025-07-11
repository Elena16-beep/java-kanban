package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendResponse(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes();
        h.getResponseHeaders().add("Content-Type", "text/plain");
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
}