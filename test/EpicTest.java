import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldInstancesOfEpicBeEqualToEachOther() {
        Task epic = new Epic("Test Equal", "epic");
        taskManager.createEpic(epic);

        Task epic1 = taskManager.getEpicById(epic.getId());
        Task epic2 = taskManager.getEpicById(epic.getId());

        assertEquals(epic1, epic2, "Экземпляры класса Epic не равны.");
    }

    @Test
    void shouldReturnTrueGetSubtasksIdIsEmptyAfterAddingEpicAsSubtask() {
        Epic epic = new Epic("Test Add", "epic");
        taskManager.createEpic(epic);
        taskManager.addSubtaskToEpic(epic, epic);

        assertTrue(epic.getSubtasksId().isEmpty(), "Эпик добавился в самого себя в виде задачи.");
    }

    @Test
    void shouldGetSubtasksIdBeEqualToSetSubtasks() {
        ArrayList<Integer> subtasksIdTest = new ArrayList<>();
        subtasksIdTest.add(53);
        subtasksIdTest.add(77);
        Epic epic = new Epic("Test SubtasksId", "epic");
        epic.setSubtaskId(53);
        epic.setSubtaskId(77);

        ArrayList<Integer> getSubtasksID = epic.getSubtasksId();

        assertEquals(subtasksIdTest, getSubtasksID, "SubtasksId установленный и полученный не равны.");
    }

    @Test
    void shouldReturnTrueIsEmptyGetSubtasksIdAfterClearingSubtasksId() {
        Epic epic = new Epic("Test SubtasksId", "epic");
        epic.setSubtaskId(53);
        epic.setSubtaskId(77);
        epic.clearSubtasksId();

        ArrayList<Integer> getSubtasksId = epic.getSubtasksId();

        assertTrue(getSubtasksId.isEmpty(), "SubtasksId не очистился.");
    }

    @Test
    void shouldReturnFalseIsContainsGetSubtaskIdAfterRemovingSubtaskId() {
        Epic epic = new Epic("Test SubtasksId", "epic");
        epic.setSubtaskId(53);
        epic.setSubtaskId(77);
        epic.removeSubtaskId(53);

        ArrayList<Integer> getSubtasksId = epic.getSubtasksId();

        assertFalse(getSubtasksId.contains(53), "SubtaskId не удалился.");
    }

    @Test
    void shouldGetEpicToStringBeEqualToEpicToString() {
        String name = "Epic Name";
        String description = "epic";
        String subtasksId = "[]";
        String epicToString = "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + 0 +
                ", status=" + Status.NEW +
                ", subtasksId=" + subtasksId +
                '}';
        Task epic = new Epic(name, description);
        taskManager.createEpic(epic);

        String getEpicToString = taskManager.getEpicById(epic.getId()).toString();

        assertEquals(epicToString, getEpicToString, "Epic при добавлении изменился.");
    }
}