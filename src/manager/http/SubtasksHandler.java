package manager.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
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
                        // GET /subtasks
                        String response = gson.toJson(taskManager.getAllSubtasks());
                        sendText(exchange, response);
                    } else if (pathParts.length == 3) {
                        // GET /subtasks/{id}
                        int id = Integer.parseInt(pathParts[2]);
                        Optional<Subtask> subtask = Optional.ofNullable(taskManager.getSubtaskById(id));
                        if (subtask.isPresent()) {
                            sendText(exchange, gson.toJson(subtask.get()));
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    // POST /subtasks
                    Subtask newSubtask = readRequest(exchange.getRequestBody(), Subtask.class);
                    if (newSubtask.getId() == 0) {
                        // Create new subtask
                        try {
                            int subtaskId = taskManager.createSubtask(newSubtask);
                            sendCreated(exchange, gson.toJson(taskManager.getSubtaskById(subtaskId)));
                        } catch (ManagerSaveException e) {
                            sendHasInteractions(exchange);
                        }
                    } else {
                        // Update existing subtask
                        try {
                            taskManager.updateSubtask(newSubtask);
                            sendCreated(exchange, gson.toJson(newSubtask));
                        } catch (ManagerSaveException e) {
                            sendHasInteractions(exchange);
                        }
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        // DELETE /subtasks/{id}
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteSubtask(id);
                        sendText(exchange, "Subtask deleted");
                    } else if (pathParts.length == 2) {
                        // DELETE /subtasks
                        taskManager.deleteAllSubtasks();
                        sendText(exchange, "All subtasks deleted");
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