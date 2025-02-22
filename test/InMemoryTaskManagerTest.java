import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        Task subtask = new Subtask("Test Delete", "subtask");
        Task subtask1 = new Subtask("Test Delete", "subtask1");
        Task subtask2 = new Subtask("Test Delete", "subtask2");
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.addSubtaskToEpic(subtask, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask1, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask2, epic);
        inMemoryTaskManager.deleteEpicById(epic.getId());

        Task getEpic = inMemoryTaskManager.getEpicById(epic.getId());

        assertNull(getEpic, "Epic не удалился.");
    }

    @Test
    void shouldReturnNullGetSubtaskByIdAfterDeletingSubtask() {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask");
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.addSubtaskToEpic(subtask, epic);
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
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask");
        Task subtask1 = new Subtask("Test Delete", "subtask1");
        Task subtask2 = new Subtask("Test Delete", "subtask2");
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.addSubtaskToEpic(subtask, epic);
        inMemoryTaskManager.deleteAllSubtasks();

        ArrayList<Task> getAllSubtasks = inMemoryTaskManager.getAllSubtasks();

        assertTrue(getAllSubtasks.isEmpty(), "Subtask не удалились.");
    }

    @Test
    void shouldGetAllSubtasksBeEqualSetAllSubtasks() {
        Task epic = new Epic("Test Get", "epic");
        Task subtask = new Subtask("Test Delete", "subtask");
        Task subtask1 = new Subtask("Test Delete", "subtask1");
        Task subtask2 = new Subtask("Test Delete", "subtask2");
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.addSubtaskToEpic(subtask, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask1, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask2, epic);

        ArrayList<Task> allSubtasks = new ArrayList<>();
        allSubtasks.add(subtask);
        allSubtasks.add(subtask1);
        allSubtasks.add(subtask2);

        ArrayList<Task> getAllSubtasks = inMemoryTaskManager.getAllSubtasksByEpic(epic);

        assertEquals(allSubtasks, getAllSubtasks, "Не удалось получить все Subtask из Epic.");
    }

    @Test
    void shouldReturnTrueGetAllEpicsIsEmptyAfterDeletingAllEpics() {
        Task epic = new Epic("Test Delete", "epic");
        Task epic1 = new Epic("Test Delete", "epic1");
        Task epic2 = new Epic("Test Delete", "epic2");
        Task subtask = new Subtask("Test Delete", "subtask");
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createEpic(epic2);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.addSubtaskToEpic(subtask, epic2);
        inMemoryTaskManager.deleteAllEpics();

        ArrayList<Task> getAllEpics = inMemoryTaskManager.getAllEpics();

        assertTrue(getAllEpics.isEmpty(), "Epics не удалились.");
    }

    @Test
    void shouldGetUpdatedTaskBeNotEqualSetTask() {
        Task task = new Task("Test Update", "task");
        inMemoryTaskManager.createTask(task);
        String taskToString = inMemoryTaskManager.getTaskById(task.getId()).toString();
        task.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(task);

        String getUpdatedTaskToString = inMemoryTaskManager.getTaskById(task.getId()).toString();

        assertNotEquals(taskToString, getUpdatedTaskToString, "Task не обновился.");
    }

    @Test
    void shouldGetHistoryTasksBeEqualSetTasks() {
        Task task = new Task("Test History", "task");
        Task epic = new Epic("Test History", "epic");
        Task subtask = new Subtask("Test History", "subtask");
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        List<Task> setHistory = new ArrayList<>();
        setHistory.add(task);
        setHistory.add(epic);
        setHistory.add(subtask);
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getEpicById(epic.getId());
        inMemoryTaskManager.getSubtaskById(subtask.getId());

        List<Task> getHistory = inMemoryTaskManager.getHistory();

        assertEquals(setHistory, getHistory, "Tasks не сохранились в истории.");
    }
}