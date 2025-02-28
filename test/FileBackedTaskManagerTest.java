import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;
    File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("list_tasks_test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    @Test
    void shouldReturnTrueIsEmptyLoadFileAfterSaveEmptyFile() {
        Task task = new Task("Test Load", "task");
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.deleteAllTasks();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        assertTrue(fileBackedTaskManager1.getAllTasks().isEmpty() , "Загруженный файл не пустой.");
    }

    @Test
    void shouldSetTaskBeEqualLoadTask() {
        Task task = new Task("Test Load", "task");
        fileBackedTaskManager.createTask(task);
        Task setTask = fileBackedTaskManager.getAllTasks().getFirst();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task getTask = fileBackedTaskManager1.getAllTasks().getFirst();

        assertEquals(setTask, getTask, "Task не сохранился.");
    }

    @Test
    void shouldSetSubtaskBeEqualLoadSubtask() {
        Task subtask = new Subtask("Test Load", "subtask");
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
    void shouldSetUpdatedSaveTaskBeEqualGetUpdatedLoadTask() {
        Task task = new Task("Test Update", "task");
        fileBackedTaskManager.createTask(task);
        task.setStatus(Status.DONE);
        String taskToString = fileBackedTaskManager.getTaskById(task.getId()).toString();
        fileBackedTaskManager.updateTask(task);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        String getUpdatedLoadTaskToString = fileBackedTaskManager1.getTaskById(task.getId()).toString();

        assertEquals(taskToString, getUpdatedLoadTaskToString, "Task не обновился.");
    }

    @Test
    void shouldSetUpdatedSaveSubtaskBeEqualGetUpdatedLoadSubtask() {
        Task epic = new Epic("Test Update Subtask", "epic");
        Task subtask = new Subtask("Test Update", "subtask");
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
    void shouldReturnTrueGetLoadAllEpicsIsEmptyAfterDeletingAllEpicsAndSave() {
        Task epic = new Epic("Test Delete", "epic");
        Task epic1 = new Epic("Test Delete", "epic1");
        Task epic2 = new Epic("Test Delete", "epic2");
        Task subtask = new Subtask("Test Delete", "subtask");
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
    void shouldReturnTrueGetLoadAllSubtasksIsEmptyAfterDeletingAllSubtasksAndSave() {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask");
        Task subtask1 = new Subtask("Test Delete", "subtask1");
        Task subtask2 = new Subtask("Test Delete", "subtask2");
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
    void shouldReturnTrueGetLoadAllTasksIsEmptyAfterDeletingAllTasksAndSave() {
        Task task = new Task("Test Delete", "task");
        Task task1 = new Task("Test Delete", "task1");
        Task task2 = new Task("Test Delete", "task2");
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(task2);
        fileBackedTaskManager.deleteAllTasks();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        ArrayList<Task> getAllTasks = fileBackedTaskManager1.getAllTasks();

        assertTrue(getAllTasks.isEmpty(), "Tasks не удалились.");
    }

    @Test
    void shouldReturnNullGetLoadSubtaskByIdAfterDeletingSubtaskAndSave() {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask");
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);
        fileBackedTaskManager.addSubtaskToEpic(subtask, epic);
        fileBackedTaskManager.deleteSubtaskById(subtask.getId());

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task getSubtask = fileBackedTaskManager1.getSubtaskById(subtask.getId());

        assertNull(getSubtask, "Subtask не удалился.");
    }

    @Test
    void shouldReturnNullGetLoadEpicByIdAfterDeletingEpicAndSave() {
        Task epic = new Epic("Test Delete", "epic");
        Task subtask = new Subtask("Test Delete", "subtask");
        Task subtask1 = new Subtask("Test Delete", "subtask1");
        Task subtask2 = new Subtask("Test Delete", "subtask2");
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
    void shouldReturnNullGetLoadTaskByIdAfterDeletingTaskAndSave() {
        Task task = new Task("Test Delete", "task");
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.deleteTaskById(task.getId());

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task getTask = fileBackedTaskManager1.getTaskById(task.getId());

        assertNull(getTask, "Task не удалился.");
    }
}
