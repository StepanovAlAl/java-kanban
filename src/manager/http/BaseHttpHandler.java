package manager.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 200);
    }

    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 201);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Not Found";
        exchange.sendResponseHeaders(404, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "Task time overlaps with existing tasks";
        exchange.sendResponseHeaders(406, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String response = "Internal Server Error";
        exchange.sendResponseHeaders(500, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected <T> T readRequest(InputStream is, Class<T> clazz) throws IOException {
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, clazz);
    }
}