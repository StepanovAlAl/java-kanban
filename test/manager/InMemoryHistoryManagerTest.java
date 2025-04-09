package manager;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        task3 = new Task("Task 3", "Description 3");
        task3.setId(3);
    }

    @Test
    void addShouldAddTaskToHistory() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        assertEquals(task1, history.get(0), "Задачи не совпадают.");
    }

    @Test
    void addShouldRemoveDuplicateTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач в истории.");
        assertEquals(task2, history.get(0), "Первая задача не совпадает.");
        assertEquals(task1, history.get(1), "Вторая задача не совпадает.");
    }

    @Test
    void removeShouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач в истории.");
        assertFalse(history.contains(task2), "Задача не удалена из истории.");
        assertTrue(history.contains(task1), "Первая задача должна остаться.");
        assertTrue(history.contains(task3), "Третья задача должна остаться.");
    }

    @Test
    void removeFirstShouldWorkCorrectly() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Неверное количество задач в истории.");
        assertEquals(task2, history.get(0), "Оставшаяся задача не совпадает.");
    }

    @Test
    void removeLastShouldWorkCorrectly() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Неверное количество задач в истории.");
        assertEquals(task1, history.get(0), "Оставшаяся задача не совпадает.");
    }

    @Test
    void getHistoryShouldReturnEmptyListWhenNoTasks() {
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertTrue(history.isEmpty(), "История не пустая.");
    }

    @Test
    void getHistoryShouldReturnTasksInOrderOfAddition() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Неверное количество задач в истории.");
        assertEquals(task1, history.get(0), "Первая задача не совпадает.");
        assertEquals(task2, history.get(1), "Вторая задача не совпадает.");
        assertEquals(task3, history.get(2), "Третья задача не совпадает.");
    }
}