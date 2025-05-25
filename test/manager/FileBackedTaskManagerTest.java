package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @Override
    protected FileBackedTaskManager createManager() {
        file = new File("test_tasks.csv");
        return new FileBackedTaskManager(file);
    }

    @BeforeEach
    void setUp() {
        super.setUp();
        file.deleteOnExit();
    }
/*
    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }*/


    @Test
    void shouldSaveAndLoadTasksCorrectly() {
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertAll(
                () -> assertEquals(1, loaded.getAllTasks().size(), "Неверное количество задач"),
                () -> assertEquals(1, loaded.getAllEpics().size(), "Неверное количество эпиков"),
                () -> assertEquals(1, loaded.getAllSubtasks().size(), "Неверное количество подзадач")
        );
    }
}
