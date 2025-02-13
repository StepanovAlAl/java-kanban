package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testEpicEqualityById() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic 2", "Description 2");
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны.");
    }

    @Test
    void testEpicNotEqualityById() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic 2", "Description 2");
        epic2.setId(2);

        assertNotEquals(epic1, epic2, "Эпики с разным id не должны быть равны.");
    }
}