package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws Exception {
        file = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    void testSaveAndLoadEmptyFile() throws Exception {

        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }

    @Test
    void testSaveAndLoadWithTasks() throws Exception {

        // Создаем задачи
        Task task = new TaskInstance("Task", "Description");
        int taskId = manager.createTask(task);

        Epic epic = new Epic("Epic", "Epic description");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Sub desc", epicId);
        int subtaskId = manager.createSubtask(subtask);

        // Загружаем обратно
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubtasks().size());
        Task savedTask = loaded.getTaskById(taskId);
        assertEquals(task.getName(), savedTask.getName());
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertEquals(task.getStatus(), savedTask.getStatus());

        Epic savedEpic = loaded.getEpicById(epicId);
        assertEquals(epic.getName(), savedEpic.getName());
        assertEquals(epic.getDescription(), savedEpic.getDescription());
        assertEquals(epic.getStatus(), savedEpic.getStatus());

        Subtask savedSubtask = loaded.getSubtaskById(subtaskId);
        assertEquals(subtask.getName(), savedSubtask.getName());
        assertEquals(subtask.getDescription(), savedSubtask.getDescription());
        assertEquals(subtask.getStatus(), savedSubtask.getStatus());
        assertEquals(subtask.getEpicId(), savedSubtask.getEpicId());

        assertTrue(savedEpic.getSubtaskIds().contains(subtaskId));
    }
}