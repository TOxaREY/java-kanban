import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;
    File file;
    LocalDateTime startTime;
    Duration duration;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("list_tasks_test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
        startTime = LocalDateTime.of(2025, 3, 11, 0, 0);
        duration = Duration.ofMinutes(1);
    }

    @Test
    void shouldReturnTrueIsEmptyLoadFileAfterSaveEmptyFile() throws TasksIntersectException {
        Task task = new Task("Test Load", "task", duration, startTime);
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.deleteAllTasks();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        assertTrue(fileBackedTaskManager1.getAllTasks().isEmpty() , "Загруженный файл не пустой.");
    }

    @Test
    void shouldSetTaskBeEqualLoadTask() throws TasksIntersectException {
        Task task = new Task("Test Load", "task", duration, startTime);
        fileBackedTaskManager.createTask(task);
        Task setTask = fileBackedTaskManager.getAllTasks().getFirst();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task getTask = fileBackedTaskManager1.getAllTasks().getFirst();

        assertEquals(setTask, getTask, "Task не сохранился.");
    }

    @Test
    void shouldSetSubtaskBeEqualLoadSubtask() throws TasksIntersectException {
        Task subtask = new Subtask("Test Load", "subtask", duration, startTime);
        fileBackedTaskManager.createSubtask(subtask);
        Task setSubtask = fileBackedTaskManager.getAllSubtasks().getFirst();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task getSubtask = fileBackedTaskManager1.getAllSubtasks().getFirst();

        assertEquals(setSubtask, getSubtask, "Subtask не сохранился.");
    }

    @Test
    void shouldSetEpicBeEqualLoadEpic() {
        Task epic = new Epic("Test Load", "epic");
        fileBackedTaskManager.createEpic(epic);
        Task setEpic = fileBackedTaskManager.getAllEpics().getFirst();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task getEpic = fileBackedTaskManager1.getAllEpics().getFirst();

        assertEquals(setEpic, getEpic, "Epic не сохранился.");
    }

    @Test
    void shouldSetUpdatedSaveTaskBeEqualGetUpdatedLoadTask() throws TasksIntersectException {
        Task task = new Task("Test Update", "task", duration, startTime);
        fileBackedTaskManager.createTask(task);
        task.setStatus(Status.DONE);
        String taskToString = fileBackedTaskManager.getTaskById(task.getId()).toString();
        fileBackedTaskManager.updateTask(task);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        String getUpdatedLoadTaskToString = fileBackedTaskManager1.getTaskById(task.getId()).toString();

        assertEquals(taskToString, getUpdatedLoadTaskToString, "Task не обновился.");
    }

    @Test
    void shouldSetUpdatedSaveSubtaskBeEqualGetUpdatedLoadSubtask() throws TasksIntersectException {
        Task epic = new Epic("Test Update Subtask", "epic");
        Task subtask = new Subtask("Test Update", "subtask", duration, startTime);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);
        fileBackedTaskManager.addSubtaskToEpic(subtask, epic);
        subtask.setStatus(Status.IN_PROGRESS);
        String subtaskToString = fileBackedTaskManager.getSubtaskById(subtask.getId()).toString();
        fileBackedTaskManager.updateSubtask(subtask);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        String getUpdatedLoadSubtaskToString = fileBackedTaskManager1.getSubtaskById(subtask.getId()).toString();

        assertEquals(subtaskToString, getUpdatedLoadSubtaskToString, "Subtask не обновился.");
    }

    @Test
    void shouldReturnTrueGetLoadAllEpicsIsEmptyAfterDeletingAllEpicsAndSave() throws TasksIntersectException {
        Task epic = new Epic("Test Delete", "epic");
        Task epic1 = new Epic("Test Delete", "epic1");
        Task epic2 = new Epic("Test Delete", "epic2");
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createEpic(epic2);
        fileBackedTaskManager.createSubtask(subtask);
        fileBackedTaskManager.addSubtaskToEpic(subtask, epic2);
        fileBackedTaskManager.deleteAllEpics();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        ArrayList<Task> getAllEpics = fileBackedTaskManager1.getAllEpics();

        assertTrue(getAllEpics.isEmpty(), "Epics не удалились.");
    }

    @Test
    void shouldReturnTrueGetLoadAllSubtasksIsEmptyAfterDeletingAllSubtasksAndSave() throws TasksIntersectException {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
        Task subtask1 = new Subtask("Test Delete", "subtask1", duration, startTime.plusMinutes(2));
        Task subtask2 = new Subtask("Test Delete", "subtask2", duration, startTime.plusMinutes(4));
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);
        fileBackedTaskManager.createSubtask(subtask1);
        fileBackedTaskManager.createSubtask(subtask2);
        fileBackedTaskManager.addSubtaskToEpic(subtask, epic);
        fileBackedTaskManager.deleteAllSubtasks();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        ArrayList<Task> getAllSubtasks = fileBackedTaskManager1.getAllSubtasks();

        assertTrue(getAllSubtasks.isEmpty(), "Subtask не удалились.");
    }

    @Test
    void shouldReturnTrueGetLoadAllTasksIsEmptyAfterDeletingAllTasksAndSave() throws TasksIntersectException {
        Task task = new Task("Test Delete", "task", duration, startTime);
        Task task1 = new Task("Test Delete", "task1", duration, startTime.plusMinutes(2));
        Task task2 = new Task("Test Delete", "task2", duration, startTime.plusMinutes(4));
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(task2);
        fileBackedTaskManager.deleteAllTasks();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        ArrayList<Task> getAllTasks = fileBackedTaskManager1.getAllTasks();

        assertTrue(getAllTasks.isEmpty(), "Tasks не удалились.");
    }

    @Test
    void shouldReturnNullGetLoadSubtaskByIdAfterDeletingSubtaskAndSave() throws TasksIntersectException {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);
        fileBackedTaskManager.addSubtaskToEpic(subtask, epic);
        fileBackedTaskManager.deleteSubtaskById(subtask.getId());

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task getSubtask = fileBackedTaskManager1.getSubtaskById(subtask.getId());

        assertNull(getSubtask, "Subtask не удалился.");
    }

    @Test
    void shouldReturnNullGetLoadEpicByIdAfterDeletingEpicAndSave() throws TasksIntersectException {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
        Task subtask1 = new Subtask("Test Delete", "subtask1", duration, startTime.plusMinutes(2));
        Task subtask2 = new Subtask("Test Delete", "subtask2", duration, startTime.plusMinutes(4));
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);
        fileBackedTaskManager.createSubtask(subtask1);
        fileBackedTaskManager.createSubtask(subtask2);
        fileBackedTaskManager.addSubtaskToEpic(subtask, epic);
        fileBackedTaskManager.addSubtaskToEpic(subtask1, epic);
        fileBackedTaskManager.addSubtaskToEpic(subtask2, epic);
        fileBackedTaskManager.deleteEpicById(epic.getId());

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task getEpic = fileBackedTaskManager1.getEpicById(epic.getId());

        assertNull(getEpic, "Epic не удалился.");
    }

    @Test
    void shouldReturnNullGetLoadTaskByIdAfterDeletingTaskAndSave() throws TasksIntersectException {
        Task task = new Task("Test Delete", "task", duration, startTime);
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.deleteTaskById(task.getId());

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task getTask = fileBackedTaskManager1.getTaskById(task.getId());

        assertNull(getTask, "Task не удалился.");
    }
}
