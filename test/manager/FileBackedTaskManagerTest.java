package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
        // Создаем тестовые данные
        Task task = new Task("Test Task", "Description");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        int taskId = manager.createTask(task);

        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", epicId);
        subtask.setStartTime(LocalDateTime.now().plusHours(1));
        subtask.setDuration(Duration.ofMinutes(15));
        int subtaskId = manager.createSubtask(subtask);

        // Получаем списки ДО сохранения
        List<Task> originalTasks = manager.getAllTasks();
        List<Epic> originalEpics = manager.getAllEpics();
        List<Subtask> originalSubtasks = manager.getAllSubtasks();

        // Сохраняем и загружаем
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        // Получаем списки ПОСЛЕ загрузки
        List<Task> loadedTasks = loaded.getAllTasks();
        List<Epic> loadedEpics = loaded.getAllEpics();
        List<Subtask> loadedSubtasks = loaded.getAllSubtasks();

        // Проверяем
        assertAll(
                () -> assertEquals(originalTasks, loadedTasks, "Списки задач не совпадают"),
                () -> assertEquals(originalEpics, loadedEpics, "Списки эпиков не совпадают"),
                () -> assertEquals(originalSubtasks, loadedSubtasks, "Списки подзадач не совпадают")
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
