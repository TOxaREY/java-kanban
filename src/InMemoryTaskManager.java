import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 0;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Task> subtasks = new HashMap<>();
    public HashMap<Integer, Task> epics = new HashMap<>();
    public HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Task epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Task subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Task epic) {
        epics.put(epic.getId(), epic);
        updateStatusEpic(epic);
    }

    @Override
    public void updateSubtask(Task subtask) {
        subtasks.put(subtask.getId(), subtask);
        if (((Subtask) subtask).getEpicId() != null) {
            updateEpic(epics.get(((Subtask) subtask).getEpicId()));
        }
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getEpicById(Integer id) {
        Task epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Task getSubtaskById(Integer id) {
        Task subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteAllEpics() {
        removeFromHistory(epics);
        removeFromHistory(subtasks);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllTasks() {
        removeFromHistory(tasks);
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        removeFromHistory(subtasks);
        subtasks.clear();
        for (Task v : epics.values()) {
            Epic value = (Epic) v;
            if (!value.getSubtasksId().isEmpty()) {
                value.clearSubtasksId();
                updateEpic(value);
            }
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (!((Epic) epics.get(id)).getSubtasksId().isEmpty()) {
            ArrayList<Integer> keys = new ArrayList<>();
            for (Integer i : ((Epic) epics.get(id)).getSubtasksId()) {
                for (Integer j : subtasks.keySet()) {
                    if (i.equals(j)) {
                        keys.add(j);
                    }
                }
            }
            for (Integer key : keys) {
                subtasks.remove(key);
                historyManager.remove(key);
            }
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        for (Task v : epics.values()) {
            Epic value = (Epic) v;
            if (value.getSubtasksId().contains(id)) {
                value.removeSubtaskId(id);
                updateEpic(value);
            }
        }
        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteTaskById(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Task> getAllSubtasksByEpic(Task epic) {
        if (((Epic) epic).getSubtasksId().isEmpty()) {
            return null;
        } else {
            ArrayList<Task> subtasksOfEpic = new ArrayList<>();
            for (Integer i : ((Epic) epic).getSubtasksId()) {
                subtasksOfEpic.add(subtasks.get(i));
            }
            return subtasksOfEpic;
        }
    }

    @Override
    public void addSubtaskToEpic(Task subtask, Task epic) {
        if (subtask.getClass().equals(Subtask.class) && epic.getClass().equals(Epic.class)) {
            ((Subtask) subtask).setEpicId(epic.getId());
            ((Epic) epic).setSubtaskId(subtask.getId());
            updateSubtask(subtask);
        }
    }

    @Override
    public void restoreTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void restoreEpic(Task epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void restoreSubtask(Task subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    private Integer generateId() {
        return id++;
    }

    private void updateStatusEpic(Task epic) {
        if (((Epic) epic).getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            ArrayList<Status> statuses = new ArrayList<>();
            Status status = Status.IN_PROGRESS;
            for (Integer i : ((Epic) epic).getSubtasksId()) {
                statuses.add(subtasks.get(i).getStatus());
            }
            if (!statuses.contains(Status.IN_PROGRESS)) {
                if (!statuses.contains(Status.NEW)) {
                    status = Status.DONE;
                } else if (!statuses.contains(Status.DONE)) {
                    status = Status.NEW;
                }
            }
            epic.setStatus(status);
        }
    }

    private void removeFromHistory(HashMap<Integer, Task> tasks) {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
    }
}
