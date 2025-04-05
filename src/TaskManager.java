import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void createTask(Task task) throws TasksIntersectException;

    void createEpic(Task epic);

    void createSubtask(Task subtask) throws TasksIntersectException;

    void updateTask(Task task);

    void updateEpic(Task epic);

    void updateSubtask(Task subtask);

    ArrayList<Task> getAllEpics();

    ArrayList<Task> getAllSubtasks();

    ArrayList<Task> getAllTasks();

    Task getEpicById(Integer id) throws NotFoundException;

    Task getSubtaskById(Integer id) throws NotFoundException;

    Task getTaskById(Integer id) throws NotFoundException;

    void deleteAllEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteEpicById(Integer id) throws NotFoundException;

    void deleteSubtaskById(Integer id) throws NotFoundException;

    void deleteTaskById(Integer id) throws NotFoundException;

    ArrayList<Task> getAllSubtasksByEpic(Task epic) throws NotFoundException;

    void addSubtaskToEpic(Task subtask, Task epic) throws TasksIntersectException;

    List<Task> getHistory();

    void restoreTask(Task task);

    void restoreEpic(Task epic);

    void restoreSubtask(Task subtask);

    List<Task> getPrioritizedTasks();
}
