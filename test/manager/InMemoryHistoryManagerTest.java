package manager;

import model.Task;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager = Managers.getDefaultHistory();


    @Test
    void testHistorySizeLimit() {
        HistoryManager historyManager = new InMemoryHistoryManager();

        // Добавляем 11 задач
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }

        // Проверяем, что в истории только 10 задач
        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать не более 10 задач.");

        // Проверяем, что первая задача (Task 1) была удалена
        assertNotEquals(1, history.get(0).getId(), "Первая задача должна быть удалена из истории.");
    }
}