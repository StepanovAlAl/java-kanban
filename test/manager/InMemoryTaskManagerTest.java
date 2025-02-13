package manager;

import model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager = Managers.getDefault();

    @Test
    void testAddAndFindDifferentTaskTypes() {
        // Добавляем обычную задачу
        Task task = new Task("Task 1", "Description 1");
        int taskId = taskManager.createTask(task);
        Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Обычная задача не найдена.");
        assertEquals(task, savedTask, "Обычная задача не совпадает.");

        // Добавляем эпик
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.createEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпик не совпадает.");

        // Добавляем подзадачу
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадача не совпадает.");
    }

    @Test
    void testTaskIdConflict() {
        // Создаем задачу с заданным id
        Task taskWithCustomId = new Task("Task 1", "Description 1");
        taskWithCustomId.setId(100); // Задаем id вручную
        taskManager.createTask(taskWithCustomId);

        // Создаем задачу с автоматически сгенерированным id
        Task taskWithGeneratedId = new Task("Task 2", "Description 2");
        int generatedId = taskManager.createTask(taskWithGeneratedId);

        // Проверяем, что id не конфликтуют
        assertNotEquals(taskWithCustomId.getId(), generatedId, "ID задач не должны конфликтовать.");

        // Проверяем, что обе задачи доступны по своим id
        Task savedCustomTask = taskManager.getTaskById(taskWithCustomId.getId());
        Task savedGeneratedTask = taskManager.getTaskById(generatedId);

        assertNotNull(savedCustomTask, "Задача с заданным id не найдена.");
        assertNotNull(savedGeneratedTask, "Задача со сгенерированным id не найдена.");
    }


    @Test
    void testTaskImmutabilityWhenAddedToManager() {
        // Создаем задачу
        Task task = new Task("Task 1", "Description 1");
        int taskId = taskManager.createTask(task);

        // Получаем задачу из менеджера
        Task savedTask = taskManager.getTaskById(taskId);

        // Изменяем задачу
        savedTask.setName("New Name");

        // Получаем задачу из менеджера снова
        Task originalTask = taskManager.getTaskById(taskId);

        // Проверяем, что задача внутри менеджера осталась неизменной
        assertEquals("Task 1", originalTask.getName(), "Задача внутри менеджера должна остаться неизменной.");
    }
}