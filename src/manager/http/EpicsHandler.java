package manager.http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
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
                        handleGetAllEpics(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetEpicById(exchange, pathParts[2]);
                    } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
                        handleGetEpicSubtasks(exchange, pathParts[2]);
                    }
                    break;
                case "POST":
                    handlePostEpic(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteEpic(exchange, pathParts[2]);
                    } else if (pathParts.length == 2) {
                        handleDeleteAllEpics(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getAllEpics()));
    }

    private void handleGetEpicById(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            sendText(exchange, gson.toJson(epic));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        sendText(exchange, gson.toJson(taskManager.getSubtasksByEpicId(id)));
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        Epic newEpic = readRequest(exchange.getRequestBody(), Epic.class);
        if (newEpic.getId() == 0) {
            int epicId = taskManager.createEpic(newEpic);
            sendCreated(exchange, gson.toJson(taskManager.getEpicById(epicId)));
        } else {
            taskManager.updateEpic(newEpic);
            sendCreated(exchange, gson.toJson(newEpic));
        }
    }

    private void handleDeleteEpic(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        taskManager.deleteEpic(id);
        sendText(exchange, "Epic deleted");
    }

    private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        sendText(exchange, "All epics deleted");
    }
}