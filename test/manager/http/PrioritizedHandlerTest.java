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
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest {
    private HttpTaskServer server;
    private TaskManager manager;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();

        // Создаем задачу с временем
        Task task = new Task("Test", "Description");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        manager.createTask(task);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void getPrioritized_shouldReturnTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
    }
}