import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

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
    void shouldInstancesOfSubtaskBeEqualToEachOther() throws TasksIntersectException, NotFoundException {
        Task subtask = new Subtask("Test Equal", "subtask", duration, startTime);
        taskManager.createSubtask(subtask);
        int id = subtask.getId();

        Task subtask1 = taskManager.getSubtaskById(id);
        Task subtask2 = taskManager.getSubtaskById(id);

        assertEquals(subtask1, subtask2, "Экземпляры класса Subtask не равны.");
    }

    @Test
    void shouldReturnNullGetEpicIdAfterAddingSubtaskAsEpic() throws TasksIntersectException {
        Subtask subtask = new Subtask("Test Add", "subtask", duration, startTime);
        taskManager.createEpic(subtask);
        taskManager.addSubtaskToEpic(subtask, subtask);

        assertNull(subtask.getEpicId(), "Subtask стал своим эпиком.");
    }

    @Test
    void shouldGetEpicIdBeEqualToSetEpicId() {
        int id = 53;
        Subtask subtask = new Subtask("Test EpicId", "subtask", duration, startTime);
        subtask.setEpicId(id);

        int getEpicId = subtask.getEpicId();

        assertEquals(id, getEpicId, "EpicId установленный и полученный не равны.");
    }

    @Test
    void shouldGetSubtaskToStringBeEqualToSubtaskToString() throws TasksIntersectException, NotFoundException {
        String name = "Subtask Name";
        String description = "subtask";
        String epicId = "null";
        String subtaskToString = "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + 0 +
                ", status=" + Status.NEW +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", epicId=" + epicId +
                '}';
        Task subtask = new Subtask(name, description, duration, startTime);
        taskManager.createSubtask(subtask);
        int id = subtask.getId();

        String getSubtaskToString = taskManager.getSubtaskById(id).toString();

        assertEquals(subtaskToString, getSubtaskToString, "Subtask при добавлении изменился.");
    }
}