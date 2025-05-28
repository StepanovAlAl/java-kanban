package manager.http;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest {
    private HttpTaskServer server;
    private TaskManager manager;
    private HttpClient client;
    private Epic epic;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
        epic = new Epic("Test Epic", "Description");
        manager.createEpic(epic);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void createSubtask_shouldReturn201() throws IOException, InterruptedException {
        String subtaskJson = String.format(
                "{\"name\":\"Test\",\"description\":\"Test\",\"epicId\":%d}",
                epic.getId()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    void getSubtask_shouldReturnSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test", "Description", epic.getId());
        int subtaskId = manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
    }
}