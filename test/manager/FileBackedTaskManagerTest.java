package manager;

import model.*;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void testSaveAndLoadEmptyFile() throws Exception {
        File file = File.createTempFile("test", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }

    @Test
    void testSaveAndLoadWithTasks() throws Exception {
        File file = File.createTempFile("test", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Создаем задачи
        Task task = new Task("Task", "Description");
        manager.createTask(task);

        Epic epic = new Epic("Epic", "Epic description");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Sub desc", epicId);
        manager.createSubtask(subtask);

        // Загружаем обратно
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubtasks().size());
    }
}