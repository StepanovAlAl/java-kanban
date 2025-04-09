package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void deleteEpicShouldAlsoRemoveItsSubtasksFromHistory() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        taskManager.getEpicById(epicId); // Добавляем в историю
        taskManager.getSubtaskById(subtaskId); // Добавляем в историю
        taskManager.deleteEpic(epicId);

        assertTrue(taskManager.getHistory().isEmpty(), "Эпик и подзадачи не удалены из истории.");
    }

    @Test
    void updateTaskShouldNotAffectHistory() {
        Task task = new Task("Task", "Description");
        int taskId = taskManager.createTask(task);

        taskManager.getTaskById(taskId); // Добавляем в историю

        Task updatedTask = new Task("Updated", "Updated");
        updatedTask.setId(taskId);
        taskManager.updateTask(updatedTask);

        Task historyTask = taskManager.getHistory().get(0);
        assertEquals("Task", historyTask.getName(), "История изменилась после обновления.");
        assertEquals("Description", historyTask.getDescription(), "История изменилась после обновления.");
    }


    @Test
    void shouldMaintainConsistencyWhenSubtaskDeleted() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        taskManager.deleteSubtask(subtaskId);

        Epic savedEpic = taskManager.getEpicById(epicId);
        assertFalse(savedEpic.getSubtaskIds().contains(subtaskId), "ID подзадачи остался в эпике.");
    }

    @Test
    void historyShouldNotContainDuplicates() {
        Task task = new Task("Task", "Description");
        int taskId = taskManager.createTask(task);

        taskManager.getTaskById(taskId);
        taskManager.getTaskById(taskId);
        taskManager.getTaskById(taskId);

        assertEquals(1, taskManager.getHistory().size(), "История содержит дубликаты.");
    }
}