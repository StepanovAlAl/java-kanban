package manager.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
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
                        // GET /tasks
                        String response = gson.toJson(taskManager.getAllTasks());
                        sendText(exchange, response);
                    } else if (pathParts.length == 3) {
                        // GET /tasks/{id}
                        int id = Integer.parseInt(pathParts[2]);
                        Optional<Task> task = Optional.ofNullable(taskManager.getTaskById(id));
                        if (task.isPresent()) {
                            sendText(exchange, gson.toJson(task.get()));
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    // POST /tasks
                    Task newTask = readRequest(exchange.getRequestBody(), Task.class);
                    if (newTask.getId() == 0) {
                        // Create new task
                        try {
                            int taskId = taskManager.createTask(newTask);
                            sendCreated(exchange, gson.toJson(taskManager.getTaskById(taskId)));
                        } catch (ManagerSaveException e) {
                            sendHasInteractions(exchange);
                        }
                    } else {
                        // Update existing task
                        try {
                            taskManager.updateTask(newTask);
                            sendCreated(exchange, gson.toJson(newTask));
                        } catch (ManagerSaveException e) {
                            sendHasInteractions(exchange);
                        }
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        // DELETE /tasks/{id}
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteTask(id);
                        sendText(exchange, "Task deleted");
                    } else if (pathParts.length == 2) {
                        // DELETE /tasks
                        taskManager.deleteAllTasks();
                        sendText(exchange, "All tasks deleted");
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