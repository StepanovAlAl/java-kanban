package manager;

import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldDirectlyCheckTimeOverlaps() {
        InMemoryTaskManager inMemoryManager = (InMemoryTaskManager) manager;
        Task task1 = new Task("Task 1", "Desc");
        task1.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        task1.setDuration(Duration.ofHours(1));

        Task task2 = new Task("Task 2", "Desc");
        task2.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 30));
        task2.setDuration(Duration.ofHours(1));

        assertTrue(inMemoryManager.hasTimeOverlap(task1, task2));
    }
}