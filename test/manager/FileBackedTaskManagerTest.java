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
        Task task = new Task("Task", "Description");
        int taskId = manager.createTask(task);

        Epic epic = new Epic("Epic", "Epic description");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Sub desc", epicId);
        int subtaskId = manager.createSubtask(subtask);

        // Загружаем обратно
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(manager.getAllTasks(), loaded.getAllTasks(),
                "Списки задач не совпадают");
        assertEquals(manager.getAllEpics(), loaded.getAllEpics(),
                "Списки эпиков не совпадают");
        assertEquals(manager.getAllSubtasks(), loaded.getAllSubtasks(),
                "Списки подзадач не совпадают");
    }
}