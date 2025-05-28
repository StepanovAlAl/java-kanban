package manager.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
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
                        // GET /epics
                        String response = gson.toJson(taskManager.getAllEpics());
                        sendText(exchange, response);
                    } else if (pathParts.length == 3) {
                        // GET /epics/{id}
                        int id = Integer.parseInt(pathParts[2]);
                        Optional<Epic> epic = Optional.ofNullable(taskManager.getEpicById(id));
                        if (epic.isPresent()) {
                            sendText(exchange, gson.toJson(epic.get()));
                        } else {
                            sendNotFound(exchange);
                        }
                    } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                        // GET /epics/{id}/subtasks
                        int id = Integer.parseInt(pathParts[2]);
                        Optional<Epic> epic = Optional.ofNullable(taskManager.getEpicById(id));
                        if (epic.isPresent()) {
                            String response = gson.toJson(taskManager.getSubtasksByEpicId(id));
                            sendText(exchange, response);
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    // POST /epics
                    Epic newEpic = readRequest(exchange.getRequestBody(), Epic.class);
                    if (newEpic.getId() == 0) {
                        // Create new epic
                        int epicId = taskManager.createEpic(newEpic);
                        sendCreated(exchange, gson.toJson(taskManager.getEpicById(epicId)));
                    } else {
                        // Update existing epic
                        taskManager.updateEpic(newEpic);
                        sendCreated(exchange, gson.toJson(newEpic));
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        // DELETE /epics/{id}
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteEpic(id);
                        sendText(exchange, "Epic deleted");
                    } else if (pathParts.length == 2) {
                        // DELETE /epics
                        taskManager.deleteAllEpics();
                        sendText(exchange, "All epics deleted");
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
            e.printStackTrace();
        }
    }
}