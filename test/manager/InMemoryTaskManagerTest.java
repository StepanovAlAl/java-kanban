package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    void createTaskShouldWork() {
        Task task = new Task("Task", "Description");
        int taskId = taskManager.createTask(task);

        assertNotNull(taskManager.getTaskById(taskId), "Задача не найдена");
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач");
    }

    @Test
    void createSubtaskShouldWork() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        assertNotNull(taskManager.getSubtaskById(subtaskId), "Подзадача не найдена");
        assertTrue(taskManager.getEpicById(epicId).getSubtaskIds().contains(subtaskId),
                "Эпик не содержит подзадачу");
    }

    @Test
    void createEpicShouldWork() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);

        assertNotNull(taskManager.getEpicById(epicId), "Эпик не найден");
        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус эпика");
    }

    @Test
    void getAllTasksShouldWork() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Description");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertEquals(2, taskManager.getAllTasks().size(), "Неверное количество задач");
    }

    @Test
    void deleteTaskShouldWork() {
        Task task = new Task("Task", "Description");
        int taskId = taskManager.createTask(task);

        taskManager.deleteTask(taskId);

        assertNull(taskManager.getTaskById(taskId), "Задача не удалилась");
    }

    @Test
    void deleteEpicShouldWork() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        taskManager.deleteEpic(epicId);

        assertNull(taskManager.getSubtaskById(subtaskId), "Подзадачи не удалились");
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

    @Test
    void deleteAllTasksShouldWork() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Description");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId()); // Добавляем в историю
        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалены");
        assertTrue(taskManager.getHistory().isEmpty(), "История не очищена");
    }

    @Test
    void deleteAllSubtasksShouldWork() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.getSubtaskById(subtask1.getId()); // Добавляем в историю
        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалены");
        assertTrue(taskManager.getEpicById(epicId).getSubtaskIds().isEmpty(), "Подзадачи не удалены из эпика");
        assertEquals(Status.NEW, taskManager.getEpicById(epicId).getStatus(), "Статус эпика не обновлен");

        // Проверяем что подзадач нет в истории
        for (Task task : taskManager.getHistory()) {
            assertNotEquals(Subtask.class, task.getClass(), "В истории осталась подзадача");
        }
    }

    @Test
    void deleteAllEpicsShouldWork() {
        Epic epic1 = new Epic("Epic 1", "Description");
        Epic epic2 = new Epic("Epic 2", "Description");
        int epicId1 = taskManager.createEpic(epic1);
        int epicId2 = taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.getEpicById(epicId1); // Добавляем в историю
        taskManager.getSubtaskById(subtask1.getId()); // Добавляем в историю
        taskManager.deleteAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не удалены");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалены");
        assertTrue(taskManager.getHistory().isEmpty(), "История не очищена");
    }
}