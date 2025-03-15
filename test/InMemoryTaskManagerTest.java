import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager inMemoryTaskManager;
    LocalDateTime startTime;
    Duration duration;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
        startTime = LocalDateTime.of(2025, 3, 11, 0, 0);
        duration = Duration.ofMinutes(1);
    }

    @Test
    void shouldInstancesOfTaskBeEqualAfterSearchingById() throws TasksIntersectException {
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
    void shouldInstancesOfSubtaskBeEqualAfterSearchingById() throws TasksIntersectException {
        Task subtask = new Subtask("Test Id", "subtask", duration, startTime);
        inMemoryTaskManager.createSubtask(subtask);

        Task subtask1 = inMemoryTaskManager.getSubtaskById(subtask.getId());

        assertEquals(subtask, subtask1, "InMemoryTaskManager не может найти Subtask по id.");
    }

    @Test
    void shouldReturnNullGetTaskByIdAfterDeletingTask() throws TasksIntersectException {
        Task task = new Task("Test Delete", "task");
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.deleteTaskById(task.getId());

        Task getTask = inMemoryTaskManager.getTaskById(task.getId());

        assertNull(getTask, "Task не удалился.");
    }

    @Test
    void shouldReturnNullGetEpicByIdAfterDeletingEpic() throws TasksIntersectException {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
        Task subtask1 = new Subtask("Test Delete", "subtask1", duration, startTime.plusMinutes(2));
        Task subtask2 = new Subtask("Test Delete", "subtask2", duration, startTime.plusMinutes(4));
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
    void shouldReturnNullGetSubtaskByIdAfterDeletingSubtask() throws TasksIntersectException {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.addSubtaskToEpic(subtask, epic);
        inMemoryTaskManager.deleteSubtaskById(subtask.getId());

        Task getSubtask = inMemoryTaskManager.getSubtaskById(subtask.getId());

        assertNull(getSubtask, "Subtask не удалился.");
    }

    @Test
    void shouldGetUpdatedSubtaskBeNotEqualSetSubtask() throws TasksIntersectException {
        Task epic = new Epic("Test Update Subtask", "epic");
        Task subtask = new Subtask("Test Update", "subtask", duration, startTime);
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
    void shouldReturnTrueGetAllTasksIsEmptyAfterDeletingAllTasks() throws TasksIntersectException {
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
    void shouldReturnTrueGetAllSubtasksIsEmptyAfterDeletingAllSubtasks() throws TasksIntersectException {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
        Task subtask1 = new Subtask("Test Delete", "subtask1", duration, startTime.plusMinutes(2));
        Task subtask2 = new Subtask("Test Delete", "subtask2", duration, startTime.plusMinutes(4));
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
    void shouldGetAllSubtasksBeEqualSetAllSubtasks() throws TasksIntersectException {
        Task epic = new Epic("Test Get", "epic");
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
        Task subtask1 = new Subtask("Test Delete", "subtask1", duration, startTime.plusMinutes(2));
        Task subtask2 = new Subtask("Test Delete", "subtask2", duration, startTime.plusMinutes(4));
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
    void shouldReturnTrueGetAllEpicsIsEmptyAfterDeletingAllEpics() throws TasksIntersectException {
        Task epic = new Epic("Test Delete", "epic");
        Task epic1 = new Epic("Test Delete", "epic1");
        Task epic2 = new Epic("Test Delete", "epic2");
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
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
    void shouldGetUpdatedTaskBeNotEqualSetTask() throws TasksIntersectException {
        Task task = new Task("Test Update", "task");
        inMemoryTaskManager.createTask(task);
        String taskToString = inMemoryTaskManager.getTaskById(task.getId()).toString();
        task.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(task);

        String getUpdatedTaskToString = inMemoryTaskManager.getTaskById(task.getId()).toString();

        assertNotEquals(taskToString, getUpdatedTaskToString, "Task не обновился.");
    }

    @Test
    void shouldGetHistoryTasksBeEqualSetTasks() throws TasksIntersectException {
        Task task = new Task("Test History", "task");
        Task epic = new Epic("Test History", "epic");
        Task subtask = new Subtask("Test History", "subtask", duration, startTime);
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

    @Test
    void shouldSetTaskBeEqualRestoreTask() throws TasksIntersectException {
        Task task = new Task("Test Restore", "task");
        inMemoryTaskManager.createTask(task);
        Task setTask = inMemoryTaskManager.getAllTasks().getFirst();
        inMemoryTaskManager.deleteAllTasks();

        inMemoryTaskManager.restoreTask(setTask);
        Task getTask = inMemoryTaskManager.getAllTasks().getFirst();

        assertEquals(setTask, getTask, "Task не восстановился.");
    }

    @Test
    void shouldSetSubtaskBeEqualRestoreSubtask() throws TasksIntersectException {
        Task subtask = new Subtask("Test Restore", "subtask", duration, startTime);
        inMemoryTaskManager.createSubtask(subtask);
        Task setSubtask = inMemoryTaskManager.getAllSubtasks().getFirst();
        inMemoryTaskManager.deleteAllSubtasks();

        inMemoryTaskManager.restoreSubtask(setSubtask);
        Task getSubtask = inMemoryTaskManager.getAllSubtasks().getFirst();

        assertEquals(setSubtask, getSubtask, "Subtask не восстановился.");
    }

    @Test
    void shouldGetEpicBeEqualRestoreEpic() {
        Task epic = new Epic("Test Restore", "epic");
        inMemoryTaskManager.createEpic(epic);
        Task setEpic = inMemoryTaskManager.getAllEpics().getFirst();
        inMemoryTaskManager.deleteAllEpics();

        inMemoryTaskManager.restoreEpic(setEpic);
        Task getEpic = inMemoryTaskManager.getAllEpics().getFirst();

        assertEquals(setEpic, getEpic, "Epic не восстановился.");
    }

    @Test
    void shouldGetPrioritizedTasksBeEqualSetTasksByTime() throws TasksIntersectException {
        Task task1 = new Task("Test History", "task1", duration, startTime);
        Task task2 = new Task("Test History", "task2", duration, startTime.minusMinutes(50));
        Task subtask1 = new Subtask("Test History", "subtask1", duration, startTime.plusMinutes(50));
        Task subtask2 = new Subtask("Test History", "subtask2", duration, startTime.plusMinutes(25));
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        List<Task> setTasks = new ArrayList<>();
        setTasks.add(task2);
        setTasks.add(task1);
        setTasks.add(subtask2);
        setTasks.add(subtask1);

        List<Task> getTasks = inMemoryTaskManager.getPrioritizedTasks();

        System.out.println(getTasks);
        System.out.println(setTasks);

        assertEquals(setTasks, getTasks, "Tasks не отсортировались по времени старта.");
    }

    @Test
    void shouldReturnTrueGetStartTimeTaskIsNullAfterNotSetStartTimeTask() throws TasksIntersectException {
        Task task = new Task("Test StartTime", "task");
        inMemoryTaskManager.createTask(task);

        LocalDateTime getStartTime = task.getStartTime();

        assertNull(getStartTime, "Task сохранил время начала.");
    }

    @Test
    void shouldReturnTrueGetStartTimeSubtaskIsNullAfterNotSetStartTimeSubtask() throws TasksIntersectException {
        Task subtask = new Subtask("Test StartTime", "subtask", duration, null);
        inMemoryTaskManager.createSubtask(subtask);

        LocalDateTime getStartTime = subtask.getStartTime();

        assertNull(getStartTime, "Subtask сохранил время начала.");
    }

    @Test
    void shouldReturnExceptionAfterSetIntersectTask() throws TasksIntersectException {
        Task task = new Task("Test Intersect Task", "task", duration.plusMinutes(5), startTime);
        inMemoryTaskManager.createTask(task);
        Task task1 = new Task("Test Intersect Task1", "task1", duration, startTime.plusMinutes(3));
        TasksIntersectException exception = assertThrows(
                TasksIntersectException.class,
                () -> inMemoryTaskManager.createTask(task1)
        );

        assertEquals("Task name: " + task1.getName() + " пересекается по времени выполнения с другими задачами.", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionAfterSetIntersectSubtask() throws TasksIntersectException {
        Task subtask = new Subtask("Test Intersect Subtask", "subtask", duration.plusMinutes(5), startTime);
        inMemoryTaskManager.createSubtask(subtask);
        Task subtask1 = new Task("Test Intersect Subtask1", "subtask1", duration, startTime.plusMinutes(3));
        TasksIntersectException exception = assertThrows(
                TasksIntersectException.class,
                () -> inMemoryTaskManager.createSubtask(subtask1)
        );

        assertEquals("Subtask name: " + subtask1.getName() + " пересекается по времени выполнения с другими задачами.", exception.getMessage());
    }

    @Test
    void shouldGetStatusNewEpicAfterSetStatusNewAllSubtasks() throws TasksIntersectException {
        Task epic = new Epic("Test Status Epic", "epic");
        Task subtask1 = new Subtask("Test Status Epic", "subtask1", duration, startTime);
        Task subtask2 = new Subtask("Test Status Epic", "subtask2", duration, startTime.plusMinutes(5));
        Task subtask3 = new Subtask("Test Status Epic", "subtask3", duration, startTime.plusMinutes(10));
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.createSubtask(subtask3);
        inMemoryTaskManager.addSubtaskToEpic(subtask1, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask2, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask3, epic);
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        subtask3.setStatus(Status.NEW);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);
        inMemoryTaskManager.updateSubtask(subtask3);

        assertEquals(Status.NEW, epic.getStatus(), "Epic рассчитал неверный статус.");
    }

    @Test
    void shouldGetStatusDoneEpicAfterSetStatusDoneAllSubtasks() throws TasksIntersectException {
        Task epic = new Epic("Test Status Epic", "epic");
        Task subtask1 = new Subtask("Test Status Epic", "subtask1", duration, startTime);
        Task subtask2 = new Subtask("Test Status Epic", "subtask2", duration, startTime.plusMinutes(5));
        Task subtask3 = new Subtask("Test Status Epic", "subtask3", duration, startTime.plusMinutes(10));
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.createSubtask(subtask3);
        inMemoryTaskManager.addSubtaskToEpic(subtask1, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask2, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask3, epic);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);
        inMemoryTaskManager.updateSubtask(subtask3);

        assertEquals(Status.DONE, epic.getStatus(), "Epic рассчитал неверный статус.");
    }

    @Test
    void shouldGetStatusInProgressEpicAfterSetStatusNewAndDoneSubtasks() throws TasksIntersectException {
        Task epic = new Epic("Test Status Epic", "epic");
        Task subtask1 = new Subtask("Test Status Epic", "subtask1", duration, startTime);
        Task subtask2 = new Subtask("Test Status Epic", "subtask2", duration, startTime.plusMinutes(5));
        Task subtask3 = new Subtask("Test Status Epic", "subtask3", duration, startTime.plusMinutes(10));
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.createSubtask(subtask3);
        inMemoryTaskManager.addSubtaskToEpic(subtask1, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask2, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask3, epic);
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.NEW);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);
        inMemoryTaskManager.updateSubtask(subtask3);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic рассчитал неверный статус.");
    }

    @Test
    void shouldGetStatusInProgressEpicAfterSetStatusInProgressAllSubtasks() throws TasksIntersectException {
        Task epic = new Epic("Test Status Epic", "epic");
        Task subtask1 = new Subtask("Test Status Epic", "subtask1", duration, startTime);
        Task subtask2 = new Subtask("Test Status Epic", "subtask2", duration, startTime.plusMinutes(5));
        Task subtask3 = new Subtask("Test Status Epic", "subtask3", duration, startTime.plusMinutes(10));
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.createSubtask(subtask3);
        inMemoryTaskManager.addSubtaskToEpic(subtask1, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask2, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask3, epic);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);
        inMemoryTaskManager.updateSubtask(subtask3);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic рассчитал неверный статус.");
    }
}