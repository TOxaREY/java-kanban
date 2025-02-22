import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void shouldTaskInstancesEqualToGetFromInMemoryTaskManager() {
        Task task = new Task("Test Init", "task");
        taskManager.createTask(task);
        int id = task.getId();

        Task task1 = taskManager.getTaskById(id);

        assertEquals(task, task1, "InMemoryTaskManager не проинициализирован и не готов к работе.");
    }

    @Test
    void shouldTaskInstancesEqualToGetFromInMemoryHistoryManager() {
        Task task = new Task("Test Init", "task");
        taskManager.createTask(task);
        historyManager.add(task);

        Task task1 = historyManager.getHistory().getFirst();

        assertEquals(task, task1, "InMemoryHistoryManager не проинициализирован и не готов к работе.");
    }
}