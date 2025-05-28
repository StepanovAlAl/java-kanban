package manager.http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);  // Явно передаем taskManager в базовый класс
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetHistory(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getHistory()));
    }
}