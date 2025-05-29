package manager.http;

import com.sun.net.httpserver.HttpExchange;
import manager.ManagerSaveException; // Добавлен импорт
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);  // Явно передаем taskManager в базовый класс
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetAllSubtasks(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetSubtaskById(exchange, pathParts[2]);
                    }
                    break;
                case "POST":
                    handlePostSubtask(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteSubtask(exchange, pathParts[2]);
                    } else if (pathParts.length == 2) {
                        handleDeleteAllSubtasks(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getAllSubtasks()));
    }

    private void handleGetSubtaskById(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask != null) {
            sendText(exchange, gson.toJson(subtask));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        Subtask newSubtask = readRequest(exchange.getRequestBody(), Subtask.class);
        if (newSubtask.getId() == 0) {
            try {
                int subtaskId = taskManager.createSubtask(newSubtask);
                sendCreated(exchange, gson.toJson(taskManager.getSubtaskById(subtaskId)));
            } catch (ManagerSaveException e) {
                sendHasInteractions(exchange);
            }
        } else {
            try {
                taskManager.updateSubtask(newSubtask);
                sendCreated(exchange, gson.toJson(newSubtask));
            } catch (ManagerSaveException e) {
                sendHasInteractions(exchange);
            }
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        taskManager.deleteSubtask(id);
        sendText(exchange, "Subtask deleted");
    }

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubtasks();
        sendText(exchange, "All subtasks deleted");
    }
}