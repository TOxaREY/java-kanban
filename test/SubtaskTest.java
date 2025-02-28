import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldInstancesOfSubtaskBeEqualToEachOther() {
        Task subtask = new Subtask("Test Equal", "subtask");
        taskManager.createSubtask(subtask);
        int id = subtask.getId();

        Task subtask1 = taskManager.getSubtaskById(id);
        Task subtask2 = taskManager.getSubtaskById(id);

        assertEquals(subtask1, subtask2, "Экземпляры класса Subtask не равны.");
    }

    @Test
    void shouldReturnNullGetEpicIdAfterAddingSubtaskAsEpic() {
        Subtask subtask = new Subtask("Test Add", "subtask");
        taskManager.createEpic(subtask);
        taskManager.addSubtaskToEpic(subtask, subtask);

        assertNull(subtask.getEpicId(), "Subtask стал своим эпиком.");
    }

    @Test
    void shouldGetEpicIdBeEqualToSetEpicId() {
        int id = 53;
        Subtask subtask = new Subtask("Test EpicId", "subtask");
        subtask.setEpicId(id);

        int getEpicId = subtask.getEpicId();

        assertEquals(id, getEpicId, "EpicId установленный и полученный не равны.");
    }

    @Test
    void shouldGetSubtaskToStringBeEqualToSubtaskToString() {
        String name = "Subtask Name";
        String description = "subtask";
        String epicId = "null";
        String subtaskToString = "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + 0 +
                ", status=" + Status.NEW +
                ", epicId=" + epicId +
                '}';
        Task subtask = new Subtask(name, description);
        taskManager.createSubtask(subtask);
        int id = subtask.getId();

        String getSubtaskToString = taskManager.getSubtaskById(id).toString();

        assertEquals(subtaskToString, getSubtaskToString, "Subtask при добавлении изменился.");
    }
}