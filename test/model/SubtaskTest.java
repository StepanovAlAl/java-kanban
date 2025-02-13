package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testSubtaskEqualityById() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны.");
    }

    @Test
    void testSubtaskNotEqualityById() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 1);
        subtask2.setId(2);

        assertNotEquals(subtask1, subtask2, "Подзадачи с разным id не должны быть равны.");
    }
}