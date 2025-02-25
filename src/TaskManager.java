import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Task epic);

    void createSubtask(Task subtask);

    void updateTask(Task task);

    void updateEpic(Task epic);

    void updateSubtask(Task subtask);

    ArrayList<Task> getAllEpics();

    ArrayList<Task> getAllSubtasks();

    ArrayList<Task> getAllTasks();

    Task getEpicById(Integer id);

    Task getSubtaskById(Integer id);

    Task getTaskById(Integer id);

    void deleteAllEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteEpicById(Integer id);

    void deleteSubtaskById(Integer id);

    void deleteTaskById(Integer id);

    ArrayList<Task> getAllSubtasksByEpic(Task epic);

    void addSubtaskToEpic(Task subtask, Task epic);

    List<Task> getHistory();

    void restoreTask(Task task);

    void restoreEpic(Task epic);

    void restoreSubtask(Task subtask);
}
