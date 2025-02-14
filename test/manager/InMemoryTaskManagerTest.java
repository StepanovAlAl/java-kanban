package manager;

import model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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

    @Test
    void testUpdateTask() {
        Task task = new Task("Task 1", "Description 1");
        int taskId = taskManager.createTask(task);

        Task updatedTask = new Task("Updated Task 1", "Updated Description 1");
        updatedTask.setId(taskId);
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTaskById(taskId);
        assertEquals(updatedTask.getName(), savedTask.getName(), "Название задачи не обновлено.");
        assertEquals(updatedTask.getDescription(), savedTask.getDescription(), "Описание задачи не обновлено.");
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Task 1", "Description 1");
        int taskId = taskManager.createTask(task);

        taskManager.deleteTask(taskId);
        assertNull(taskManager.getTaskById(taskId), "Задача не удалена.");
    }

    @Test
    void testDeleteAllTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.deleteTask(task1.getId());
        taskManager.deleteTask(task2.getId());

        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены.");
    }

    @Test
    void testUpdateEpicStatus() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epicId);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        Epic savedEpic = taskManager.getEpicById(epicId);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        savedEpic = taskManager.getEpicById(epicId);
        assertEquals(Status.DONE, savedEpic.getStatus(), "Статус эпика должен быть DONE.");
    }

    @Test
    void testDeleteEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.createEpic(epic);

        taskManager.deleteEpic(epicId);
        assertNull(taskManager.getEpicById(epicId), "Эпик не удален.");
    }

    @Test
    void testDeleteSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        taskManager.deleteSubtask(subtaskId);
        assertNull(taskManager.getSubtaskById(subtaskId), "Подзадача не удалена.");
    }

    @Test
    void testGetHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        int taskId1 = taskManager.createTask(task1);
        int taskId2 = taskManager.createTask(task2);

        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId2);

        ArrayList<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи.");
        assertEquals(task1, history.get(0), "Первая задача в истории не совпадает.");
        assertEquals(task2, history.get(1), "Вторая задача в истории не совпадает.");
    }
}