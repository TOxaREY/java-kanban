import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }


    @Test
    void shouldSetTaskEqualToGetTaskFromHistory() {
        Task task = new Task("Test Add History", "task");
        inMemoryTaskManager.createTask(task);
        inMemoryHistoryManager.add(task);

        Task getTask = inMemoryHistoryManager.getHistory().getFirst();

        assertEquals(task, getTask, "Task не добавился в историю.");
    }

    @Test
    void shouldReturnTrueIsEmptyListAfterDeletingTaskFromHistory() {
        Task task = new Task("Test Delete History", "task");
        inMemoryTaskManager.createTask(task);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.remove(task.getId());

        assertTrue(inMemoryHistoryManager.getHistory().isEmpty() , "Task не удалился из истории.");
    }

    @Test
    void shouldSetSecondTaskEqualToGetTaskFromHistoryAfterDeleteFirstTask() {
        Task task1 = new Task("Test Delete History", "task");
        Task task2 = new Task("Test Delete History", "task2");
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.remove(task1.getId());

        Task getTask2 = inMemoryHistoryManager.getHistory().getFirst();

        assertEquals(task2, getTask2, "После добавления двух и удаления первого в истории не сохранился второй Task.");
    }

    @Test
    void shouldSetFirstTaskEqualToGetTaskFromHistoryAfterDeleteSecondTask() {
        Task task1 = new Task("Test Delete History", "task");
        Task task2 = new Task("Test Delete History", "task2");
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.remove(task2.getId());

        Task getTask1 = inMemoryHistoryManager.getHistory().getFirst();

        assertEquals(task1, getTask1, "После добавления двух и удаления второго в истории не сохранился первый Task.");
    }

    @Test
    void shouldSetFirstAndThirdTaskEqualToGetTasksFromHistoryAfterDeleteSecondTask() {
        Task task1 = new Task("Test Delete History", "task");
        Task task2 = new Task("Test Delete History", "task2");
        Task task3 = new Task("Test Delete History", "task3");
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task2.getId());

        Task getTask1 = inMemoryHistoryManager.getHistory().getFirst();
        Task getTask3 = inMemoryHistoryManager.getHistory().getLast();

        assertEquals(task1, getTask1, "После добавления трех и удаления второго в истории не сохранился первый Task.");
        assertEquals(task3, getTask3, "После добавления трех и удаления второго в истории не сохранился третий Task.");
    }
}
