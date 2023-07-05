package entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    protected static Epic epic;

    @BeforeAll
    static void CreateEpicTest() {
        Epic epic1 = new Epic("epic1", "epic1");
        epic = new Epic(1234, "name1234", "description 1234", Status.DONE, 0, null);

    }

    @Test
    void getTypeTest() {
        final TaskType type = epic.getType();
        assertEquals(type, TaskType.EPIC);
    }

    @Test
    void setGetSubtaskIds() {
        final List<Integer> subtaskIds = List.of(new Integer[]{1, 2, 3, 99, 34});
        epic.setSubtaskIds(subtaskIds);
        final List<Integer> subtaskIds1 = epic.getSubtaskIds();
        assertArrayEquals(subtaskIds.toArray(new Integer[0]), subtaskIds1.toArray(new Integer[0]));
    }


    @Test
    void setGetEndTimeTest() {
        final LocalDateTime endTime = LocalDateTime.of(9005, 3, 8, 11, 23);
        epic.setEndTime(endTime);
        assertEquals(epic.getEndTime(), endTime);
    }

    @Test
    void testEquals() {
        Epic epic1 = new Epic(1234, "name1234",
                "description 1234", Status.DONE, 0, null);
        Epic epic2 = new Epic(1234, "name1234",
                "description 1234", Status.DONE, 0, null);
        assertEquals(epic1, epic2);
        epic2.addSubtaskId(1);
        assertNotEquals(epic1, epic2);
    }

    @Test
    void testHashCode() {
        Epic epic1 = new Epic(1234, "name1234",
                "description 1234", Status.DONE, 0, null);
        Epic epic2 = new Epic(1234, "name1234",
                "description 1234", Status.DONE, 0, null);
        assertEquals(epic1.hashCode(), epic2.hashCode());
        epic2.addSubtaskId(1);
        assertNotEquals(epic1.hashCode(), epic2.hashCode());
    }

    @Test
    void testToString() {
        final String string = epic.toString();
        assertEquals(string, epic.toString());
    }

    @Test
    void cleanSubtaskIdsTest() {
        List<Integer> subtaskIds = Arrays.stream(new Integer[]{1, 2, 3, 99, 34}).collect(Collectors.toList());
        epic.setSubtaskIds(subtaskIds);
        assertFalse(epic.getSubtaskIds().isEmpty());
        epic.cleanSubtaskIds();
        assertTrue(epic.getSubtaskIds().isEmpty());

    }

    @Test
    void addSubtaskIdTest() {
        epic.cleanSubtaskIds();
        assertTrue(epic.getSubtaskIds().isEmpty());
        epic.addSubtaskId(277);
        assertTrue(epic.getSubtaskIds().contains(277));
    }

    @Test
    void removeSubtask() {
        epic.cleanSubtaskIds();
        assertTrue(epic.getSubtaskIds().isEmpty());
        epic.addSubtaskId(277);
        epic.addSubtaskId(23);
        epic.addSubtaskId(7);
        epic.addSubtaskId(277222);
        assertTrue(epic.getSubtaskIds().contains(277222));
        epic.removeSubtask(7);
        assertFalse(epic.getSubtaskIds().contains(7));
    }

}