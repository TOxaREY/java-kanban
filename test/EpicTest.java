import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;
    Duration duration;
    LocalDateTime startTime;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        duration = Duration.ofMinutes(1);
        startTime = LocalDateTime.of(2025, 3, 11, 0, 0);
    }

    @Test
    void shouldInstancesOfEpicBeEqualToEachOther() throws NotFoundException {
        Task epic = new Epic("Test Equal", "epic");
        taskManager.createEpic(epic);

        Task epic1 = taskManager.getEpicById(epic.getId());
        Task epic2 = taskManager.getEpicById(epic.getId());

        assertEquals(epic1, epic2, "Экземпляры класса Epic не равны.");
    }

    @Test
    void shouldReturnTrueGetSubtasksIdIsEmptyAfterAddingEpicAsSubtask() throws TasksIntersectException {
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
    void shouldGetEpicToStringBeEqualToSetEpicToString() throws NotFoundException {
        String name = "Epic Name";
        String description = "Epic Name";
        String subtasksId = "[]";
        String epicToString = "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + 0 +
                ", status=" + Status.NEW +
                ", duration=" + Duration.ZERO +
                ", startTime=" + null +
                ", endTime=" + null +
                ", subtasksId=" + subtasksId +
                '}';
        Task epic = new Epic(name, description);
        taskManager.createEpic(epic);

        String getEpicToString = taskManager.getEpicById(epic.getId()).toString();

        assertEquals(epicToString, getEpicToString, "Epic при добавлении изменился.");
    }

    @Test
    void shouldGetEndTimeEpicBeEqualToSetSubtaskEndTime() throws TasksIntersectException {
        Task epic1 = new Epic("Epic Name", "epic");
        Task subtask1 = new Subtask("Subtask Name", "subtask", duration, startTime);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.addSubtaskToEpic(subtask1, epic1);

        assertEquals(subtask1.getEndTime(), epic1.getEndTime(), "Epic не сохранил время окончания subtask.");
    }
}