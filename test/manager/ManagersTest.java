package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testGetDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Менеджер задач не должен быть null.");
    }

    @Test
    void testGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер истории не должен быть null.");
    }
}