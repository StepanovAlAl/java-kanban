package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();

        task = new Task("Test Task", "Description");
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());

        epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);

        subtask = new Subtask("Test Subtask", "Description", epicId);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.now().plusHours(1));
    }

    @Test
    void createTaskShouldWork() {
        int taskId = manager.createTask(task);
        Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач");
    }

    @Test
    void createSubtaskShouldWork() {
        int subtaskId = manager.createSubtask(subtask);

        assertNotNull(manager.getSubtaskById(subtaskId), "Подзадача не найдена");
        assertTrue(manager.getEpicById(epic.getId()).getSubtaskIds().contains(subtaskId),
                "Эпик не содержит подзадачу");
    }

    @Test
    void createEpicShouldWork() {
        int epicId = manager.createEpic(epic);
        Epic savedEpic = manager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(Status.NEW, savedEpic.getStatus(), "Неверный статус эпика");
    }

    @Test
    void getAllTasksShouldWork() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Description");
        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(2, manager.getAllTasks().size(), "Неверное количество задач");
    }

    @Test
    void deleteTaskShouldWork() {
        int taskId = manager.createTask(task);
        manager.deleteTask(taskId);
        assertNull(manager.getTaskById(taskId), "Задача не удалилась");
    }

    @Test
    void deleteEpicShouldRemoveSubtasks() {
        int subtaskId = manager.createSubtask(subtask);
        manager.deleteEpic(epic.getId());
        assertNull(manager.getSubtaskById(subtaskId), "Подзадачи не удалились");
    }

    @Test
    void updateTaskShouldNotAffectHistory() {
        int taskId = manager.createTask(task);
        manager.getTaskById(taskId);

        Task updatedTask = new Task("Updated", "Updated");
        updatedTask.setId(taskId);
        manager.updateTask(updatedTask);

        Task historyTask = manager.getHistory().get(0);
        assertEquals("Test Task", historyTask.getName(), "История изменилась после обновления.");
    }

    @Test
    void shouldMaintainConsistencyWhenSubtaskDeleted() {
        int subtaskId = manager.createSubtask(subtask);
        manager.deleteSubtask(subtaskId);
        assertFalse(manager.getEpicById(epic.getId()).getSubtaskIds().contains(subtaskId));
    }

    @Test
    void historyShouldNotContainDuplicates() {
        int taskId = manager.createTask(task);
        manager.getTaskById(taskId);
        manager.getTaskById(taskId);
        assertEquals(1, manager.getHistory().size(), "История содержит дубликаты.");
    }

    @Test
    void deleteAllTasksShouldWork() {
        manager.createTask(task);
        manager.getTaskById(task.getId());
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Задачи не удалены");
        assertTrue(manager.getHistory().isEmpty(), "История не очищена");
    }

    @Test
    void shouldCheckTimeOverlaps() {
        manager.createTask(task);

        Task overlappingTask = new Task("Overlapping", "Desc");
        overlappingTask.setStartTime(task.getStartTime().plusMinutes(15));
        overlappingTask.setDuration(Duration.ofMinutes(30));

        assertThrows(ManagerSaveException.class, () -> manager.createTask(overlappingTask));
    }

    @Test
    void getPrioritizedTasksShouldReturnSorted() {
        Task earlyTask = new Task("Early", "Desc");
        earlyTask.setStartTime(LocalDateTime.now());
        earlyTask.setDuration(Duration.ofMinutes(30));

        Task lateTask = new Task("Late", "Desc");
        lateTask.setStartTime(LocalDateTime.now().plusHours(2));
        lateTask.setDuration(Duration.ofMinutes(30));

        manager.createTask(lateTask);
        manager.createTask(earlyTask);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(earlyTask, prioritized.get(0), "Неверный порядок задач");
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtasksChange() {
        int subtaskId = manager.createSubtask(subtask);
        Subtask updated = new Subtask("Updated", "Desc", epic.getId());
        updated.setId(subtaskId);
        updated.setStatus(Status.DONE);
        manager.updateSubtask(updated);

        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }
}