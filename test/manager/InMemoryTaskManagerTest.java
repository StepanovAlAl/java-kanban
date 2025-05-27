package manager;

import model.*;
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
        Task task1 = new Task("Task 1", "Desc");
        task1.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        task1.setDuration(Duration.ofHours(1));
        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Desc");
        task2.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 30)); // Пересекается с task1
        task2.setDuration(Duration.ofHours(1));

        assertThrows(ManagerSaveException.class, () -> manager.createTask(task2));
    }

    @Test
    void shouldUpdateEpicStatusCorrectly() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        subtask1.setStatus(Status.NEW);
        manager.createSubtask(subtask1);

        assertEquals(Status.NEW, manager.getEpicById(epicId).getStatus());

        // первая DONE
        Subtask updatedSubtask = new Subtask("Updated", "Desc", epicId);
        updatedSubtask.setId(subtask1.getId());
        updatedSubtask.setStatus(Status.DONE);
        manager.updateSubtask(updatedSubtask);

        // первая DONE - проверка
        assertEquals(Status.DONE, manager.getEpicById(epicId).getStatus());

        // вторая NEW
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);
        subtask2.setStatus(Status.NEW);
        manager.createSubtask(subtask2);

        // DONE и NEW
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epicId).getStatus());

        // вторую -> на DONE
        Subtask updatedSubtask2 = new Subtask("Updated 2", "Desc", epicId);
        updatedSubtask2.setId(subtask2.getId());
        updatedSubtask2.setStatus(Status.DONE);
        manager.updateSubtask(updatedSubtask2);

        // Теперь все DONE
        assertEquals(Status.DONE, manager.getEpicById(epicId).getStatus());
    }
}