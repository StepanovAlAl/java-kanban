package manager.http;

import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static manager.http.BaseHttpHandler.gson;
import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest {
    private HttpTaskServer server;
    private TaskManager manager;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();

        // Создаем задачу для истории
        Task task = new Task("Test", "Description");
        manager.createTask(task);
        manager.getTaskById(task.getId()); // Добавляем в историю
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void getHistory_shouldReturnHistory() throws IOException, InterruptedException {
        // 1. Подготовка
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        Task testTask = new Task("Test Task", "Description");
        int taskId = manager.createTask(testTask);
        manager.getTaskById(taskId);

        // 2. Выполнение запроса
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 3. Проверки
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
        // Новое - преобразуем JSON в задачи
        List<Task> history = gson.fromJson(
                response.body(),
                new TypeToken<ArrayList<Task>>() {
                }.getType()
        );
        // Сравниваем
        assertEquals(1, history.size(), "История должна содержать 1 задачу");

        Task historyTask = history.get(0);
        assertEquals(taskId, historyTask.getId(), "ID задачи не совпадает");
        assertEquals("Test Task", historyTask.getName(), "Название задачи не совпадает");
        assertEquals("Description", historyTask.getDescription(), "Описание задачи не совпадает");
    }
}