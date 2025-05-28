package manager.http;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class EpicsHandlerTest {
    private HttpTaskServer server;
    private TaskManager manager;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void createEpic_shouldReturn201() throws IOException, InterruptedException {
        String epicJson = "{\"name\":\"Test Epic\",\"description\":\"Test\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void getEpicSubtasks_shouldReturnEmptyList() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Description");
        int epicId = manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}