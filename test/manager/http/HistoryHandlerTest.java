package manager.http;

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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
    }
}