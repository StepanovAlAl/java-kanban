package manager;

import model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void testAddToHistory() {
        Task task = new Task("Task 1", "Description 1");
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "История должна содержать одну задачу.");
    }

    @Test
    void testHistoryPreservesTaskData() {
        Task task = new Task("Task 1", "Description 1");
        historyManager.add(task);

        Task savedTask = historyManager.getHistory().get(0);

        assertEquals(task, savedTask, "Задача в истории должна сохранять свои данные.");
    }
}