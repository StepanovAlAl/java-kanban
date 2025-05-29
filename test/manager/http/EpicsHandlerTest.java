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

import static manager.http.BaseHttpHandler.gson;
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
        Epic epic = new Epic("Test Epic", "Description");
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        Epic createdEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(createdEpic.getId());
        assertEquals("Test Epic", createdEpic.getName());
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

    @Test
    void deleteEpic_shouldRemoveEpicAndSubtasks() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test", "Description");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", epicId);
        int subtaskId = manager.createSubtask(subtask);

        // Действие - удаление эпика
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(200, deleteResponse.statusCode());
        assertNull(manager.getEpicById(epicId));
        assertNull(manager.getSubtaskById(subtaskId));
    }

    @Test
    void getEpic_shouldReturnCorrectEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epicId, responseEpic.getId());
        assertEquals("Test Epic", responseEpic.getName());
    }
}