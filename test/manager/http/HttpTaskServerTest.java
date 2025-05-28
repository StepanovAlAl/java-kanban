package manager.http;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.*;
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

class HttpTaskServerTest {
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
    void testCreateAndGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        String taskJson = BaseHttpHandler.gson.toJson(task);

        // Create task
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        // Get tasks
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().contains("Test Task"));
    }

    @Test
    void testCreateEpicWithSubtasks() throws IOException, InterruptedException {
        // Create epic
        Epic epic = new Epic("Test Epic", "Description");
        String epicJson = BaseHttpHandler.gson.toJson(epic);

        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicResponse.statusCode());

        // Get epic ID from response
        Epic createdEpic = BaseHttpHandler.gson.fromJson(epicResponse.body(), Epic.class);
        int epicId = createdEpic.getId();

        // Create subtask
        Subtask subtask = new Subtask("Test Subtask", "Description", epicId);
        subtask.setStartTime(LocalDateTime.now().plusHours(1));
        subtask.setDuration(Duration.ofMinutes(15));
        String subtaskJson = BaseHttpHandler.gson.toJson(subtask);

        HttpRequest subtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> subtaskResponse = client.send(subtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, subtaskResponse.statusCode());

        // Get epic subtasks
        HttpRequest epicSubtasksRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> epicSubtasksResponse = client.send(epicSubtasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, epicSubtasksResponse.statusCode());
        assertTrue(epicSubtasksResponse.body().contains("Test Subtask"));
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        // Create tasks with different times
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(LocalDateTime.now().plusHours(1));
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(LocalDateTime.now());
        task2.setDuration(Duration.ofMinutes(30));

        // Add tasks
        client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(BaseHttpHandler.gson.toJson(task1)))
                .build(), HttpResponse.BodyHandlers.ofString());

        client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(BaseHttpHandler.gson.toJson(task2)))
                .build(), HttpResponse.BodyHandlers.ofString());

        // Get prioritized
        HttpRequest prioritizedRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        HttpResponse<String> prioritizedResponse = client.send(prioritizedRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, prioritizedResponse.statusCode());
        assertTrue(prioritizedResponse.body().indexOf("Task 2") < prioritizedResponse.body().indexOf("Task 1"));
    }
}