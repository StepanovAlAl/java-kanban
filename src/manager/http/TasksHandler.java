package manager.http;

import com.sun.net.httpserver.HttpExchange;
import manager.ManagerSaveException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager) {
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
                        handleGetAllTasks(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetTaskById(exchange, pathParts[2]);
                    }
                    break;
                case "POST":
                    handlePostTask(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteTask(exchange, pathParts[2]);
                    } else if (pathParts.length == 2) {
                        handleDeleteAllTasks(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getAllTasks()));
    }

    private void handleGetTaskById(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        Task task = taskManager.getTaskById(id);  // Один вызов менеджера
        if (task != null) {
            sendText(exchange, gson.toJson(task));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        Task newTask = readRequest(exchange.getRequestBody(), Task.class);
        if (newTask.getId() == 0) {
            try {
                int taskId = taskManager.createTask(newTask);
                sendCreated(exchange, gson.toJson(taskManager.getTaskById(taskId)));
            } catch (ManagerSaveException e) {
                sendHasInteractions(exchange);
            }
        } else {
            try {
                taskManager.updateTask(newTask);
                sendCreated(exchange, gson.toJson(newTask));
            } catch (ManagerSaveException e) {
                sendHasInteractions(exchange);
            }
        }
    }

    private void handleDeleteTask(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        taskManager.deleteTask(id);
        sendText(exchange, "Task deleted");
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        sendText(exchange, "All tasks deleted");
    }
}