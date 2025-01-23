import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldInstancesOfTaskBeEqualToEachOther() {
        Task task = new Task("Test Equal", "task");
        taskManager.createTask(task);
        int id = task.getId();

        Task task1 = taskManager.getTaskById(id);
        Task task2 = taskManager.getTaskById(id);

        assertEquals(task1, task2, "Экземпляры класса Task не равны.");
    }

    @Test
    void shouldTaskSetIdNotEqualGenerateId() {
        int id = 53;
        Task task = new Task("Test Id", "task");
        task.setId(id);
        taskManager.createTask(task);

        int getGenerateId = task.getId();

        assertNotEquals(id, getGenerateId, "Сгенерированный id не заменил заданный при создании Task.");
    }

    @Test
    void shouldGetStatusBeEqualToSetStatus() {
        Status status = Status.IN_PROGRESS;
        Task task = new Task("Test Status", "task");
        task.setStatus(status);

        Status getStatus = task.getStatus();

        assertEquals(status, getStatus, "Status установленный и полученный не равны.");
    }

    @Test
    void shouldGetNameBeEqualToSetName() {
        String name = "Task Name";
        Task task = new Task(name, "task");

        String getName = task.getName();

        assertEquals(name, getName, "Name установленный и полученный не равны.");
    }

    @Test
    void shouldGetDescriptionBeEqualToSetDescription() {
        String description = "task";
        Task task = new Task("Task Name", description);

        String getDescription = task.getDescription();

        assertEquals(description, getDescription, "Description установленный и полученный не равны.");
    }

    @Test
    void shouldGetTaskToStringBeEqualToTaskToString() {
        String name = "Task Name";
        String description = "task";
        String taskToString = "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + 0 +
                ", status=" + Status.NEW +
                '}';
        Task task = new Task(name, description);
        taskManager.createTask(task);
        int id = task.getId();

        String getTaskToString = taskManager.getTaskById(id).toString();

        assertEquals(taskToString, getTaskToString, "Task при добавлении изменился.");
    }
}