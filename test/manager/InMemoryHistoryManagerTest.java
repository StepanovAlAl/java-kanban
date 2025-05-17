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
    private Task task4;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        task3 = new Task("Task 3", "Description 3");
        task3.setId(3);
        task4 = new Task("Task 4", "Description 4");
        task4.setId(4);
    }

    @Test
    void addShouldAddTaskToHistory() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История должна содержать 1 задачу.");
        assertEquals(task1, history.get(0), "Задачи не совпадают.");
    }

    @Test
    void addShouldRemoveDuplicateTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Дубликаты не удаляются.");
        assertEquals(task2, history.get(0), "Первая задача не совпадает. Неверный порядок задач.");
        assertEquals(task1, history.get(1), "Вторая задача не совпадает. Неверный порядок задач.");
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
    void removeMiddleWorkCorrectly() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        historyManager.remove(task2.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Неверное количество задач после удаления из середины");
        assertEquals(task1, history.get(0), "Первая задача не совпадает после удаления из середины.");
        assertEquals(task3, history.get(1), "Вторая задача не совпадает после удаления из середины.");
        assertEquals(task4, history.get(2), "Третья задача не совпадает после удаления из середины.");
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
    void removeSingleShouldWorkCorrectly() {
        historyManager.add(task1);
        historyManager.remove(task1.getId());

        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления единственной задачи");
    }

    @Test
    void removeFromEmptyHistoryShouldNotFail() {
        assertDoesNotThrow(() -> historyManager.remove(1),
                "Удаление из пустой истории не должно вызывать исключений");
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
        assertEquals(task1, history.get(0), "Неверный порядок задач. Первая задача не совпадает.");
        assertEquals(task2, history.get(1), "Неверный порядок задач. Вторая задача не совпадает.");
        assertEquals(task3, history.get(2), "Неверный порядок задач. Третья задача не совпадает.");
    }
}