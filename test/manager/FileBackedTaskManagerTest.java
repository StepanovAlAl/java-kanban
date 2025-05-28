package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

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

    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();

        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }


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

        assertAll(
                () -> assertEquals(manager.getAllTasks(), loaded.getAllTasks(), "Списки задач не совпадают"),
                () -> assertEquals(manager.getAllEpics(), loaded.getAllEpics(), "Списки эпиков не совпадают"),
                () -> assertEquals(manager.getAllSubtasks(), loaded.getAllSubtasks(), "Списки подзадач не совпадают")
        );
    }

    @Test
    void shouldSaveAndLoadTaskWithTime() {
        Task taskWithTime = new Task("Task with time", "Description");
        taskWithTime.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        taskWithTime.setDuration(Duration.ofHours(1));
        manager.createTask(taskWithTime);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loaded.getTaskById(taskWithTime.getId());

        assertAll(
                () -> assertEquals(taskWithTime.getStartTime(), loadedTask.getStartTime(),
                        "Время начала не совпадает"),
                () -> assertEquals(taskWithTime.getDuration(), loadedTask.getDuration(),
                        "Длительность не совпадает"),
                () -> assertEquals(taskWithTime.getEndTime(), loadedTask.getEndTime(),
                        "Время окончания не совпадает")
        );
    }
}
