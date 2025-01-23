import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldInstancesOfTaskBeEqualAfterSearchingById() {
        Task task = new Task("Test Id", "task");
        inMemoryTaskManager.createTask(task);

        Task task1 = inMemoryTaskManager.getTaskById(task.getId());

        assertEquals(task, task1, "InMemoryTaskManager не может найти Task по id.");
    }

    @Test
    void shouldInstancesOfEpicBeEqualAfterSearchingById() {
        Task epic = new Epic("Test Id", "epic");
        inMemoryTaskManager.createEpic(epic);

        Task epic1 = inMemoryTaskManager.getEpicById(epic.getId());

        assertEquals(epic, epic1, "InMemoryTaskManager не может найти Epic по id.");
    }

    @Test
    void shouldInstancesOfSubtaskBeEqualAfterSearchingById() {
        Task subtask = new Subtask("Test Id", "subtask");
        inMemoryTaskManager.createSubtask(subtask);

        Task subtask1 = inMemoryTaskManager.getSubtaskById(subtask.getId());

        assertEquals(subtask, subtask1, "InMemoryTaskManager не может найти Subtask по id.");
    }

    @Test
    void shouldGetTaskBeEqualSetTaskAfterAddUpdatingTaskToHistory() {
        Task task = new Task("Test History", "task");
        Task task1 = new Task("Test History", "task1");
        Task task2 = new Task("Test History", "task2");
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        Task updatedTask = inMemoryTaskManager.getTaskById(task.getId());
        String taskToString = updatedTask.toString();
        updatedTask.setStatus(Status.DONE);
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.updateTask(updatedTask);
        inMemoryTaskManager.getTaskById(task.getId());

        String getTaskFromHistoryToString = inMemoryTaskManager.getHistory().getFirst().toString();

        assertEquals(taskToString, getTaskFromHistoryToString, "Task не сохранил предыдущую версию себя после добавления в историю обновленной версии.");
    }

    @Test
    void shouldReturnNullGetTaskByIdAfterDeletingTask() {
        Task task = new Task("Test Delete", "task");
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.deleteTaskById(task.getId());

        Task getTask = inMemoryTaskManager.getTaskById(task.getId());

        assertNull(getTask, "Task не удалился.");
    }

    @Test
    void shouldReturnNullGetEpicByIdAfterDeletingEpic() {
        Task epic = new Epic("Test Delete", "epic");
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.deleteEpicById(epic.getId());

        Task getEpic = inMemoryTaskManager.getEpicById(epic.getId());

        assertNull(getEpic, "Epic не удалился.");
    }

    @Test
    void shouldReturnNullGetSubtaskByIdAfterDeletingSubtask() {
        Task subtask = new Subtask("Test Delete", "subtask");
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.deleteSubtaskById(subtask.getId());

        Task getSubtask = inMemoryTaskManager.getSubtaskById(subtask.getId());

        assertNull(getSubtask, "Subtask не удалился.");
    }

    @Test
    void shouldGetUpdatedSubtaskBeNotEqualSetSubtask() {
        Task epic = new Epic("Test Update Subtask", "epic");
        Task subtask = new Subtask("Test Update", "subtask");
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.addSubtaskToEpic(subtask, epic);
        String subtaskToString = inMemoryTaskManager.getSubtaskById(subtask.getId()).toString();
        subtask.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask);

        String getUpdatedSubtaskToString = inMemoryTaskManager.getSubtaskById(subtask.getId()).toString();

        assertNotEquals(subtaskToString, getUpdatedSubtaskToString, "Subtask не обновился.");
    }

    @Test
    void shouldReturnTrueGetAllTasksIsEmptyAfterDeletingAllTasks() {
        Task task = new Task("Test Delete", "task");
        Task task1 = new Task("Test Delete", "task1");
        Task task2 = new Task("Test Delete", "task2");
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.deleteAllTasks();

        ArrayList<Task> getAllTasks = inMemoryTaskManager.getAllTasks();

        assertTrue(getAllTasks.isEmpty(), "Tasks не удалились.");
    }

    @Test
    void shouldReturnTrueGetAllSubtasksIsEmptyAfterDeletingAllSubtasks() {
        Task subtask = new Subtask("Test Delete", "subtask");
        Task subtask1 = new Subtask("Test Delete", "subtask1");
        Task subtask2 = new Subtask("Test Delete", "subtask2");
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.deleteAllSubtasks();

        ArrayList<Task> getAllSubtasks = inMemoryTaskManager.getAllSubtasks();

        assertTrue(getAllSubtasks.isEmpty(), "Subtask не удалились.");
    }
}