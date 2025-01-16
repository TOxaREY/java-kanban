import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private Integer id = 0;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();

    public Integer setId() {
        return id++;
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask, Epic epic) {
        epic.setSubtaskId(subtask.getId());
        subtask.setEpicId(epic.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpic(epic);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateStatusEpic(epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getEpicId() != null) {
            updateEpic(epics.get(subtask.getEpicId()));
        }
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public ArrayList<Subtask> getAllSubtasksByEpic(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            return null;
        } else {
            ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
            for (Integer i : epic.getSubtasksId()) {
                subtasksOfEpic.add(subtasks.get(i));
            }
            return subtasksOfEpic;
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic value : epics.values()) {
            if (!value.getSubtasksId().isEmpty()) {
                value.clearSubtasksId();
                updateEpic(value);
            }
        }
    }

    public void deleteEpicById(Integer id) {
        if (!epics.get(id).getSubtasksId().isEmpty()) {
            ArrayList<Integer> keys = new ArrayList<>();
            for (Integer i : epics.get(id).getSubtasksId()) {
                for (Integer j : subtasks.keySet()) {
                    if (i.equals(j)) {
                        keys.add(j);
                    }
                }
            }
            for (Integer key : keys) {
                subtasks.remove(key);
            }
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(Integer id) {
        for (Epic value : epics.values()) {
            if (value.getSubtasksId().contains(id)) {
                value.removeSubtaskId(id);
                updateEpic(value);
            }
        }
        subtasks.remove(id);
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    private void updateStatusEpic(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            ArrayList<Status> statuses = new ArrayList<>();
            Status status = Status.IN_PROGRESS;
            for (Integer i : epic.getSubtasksId()) {
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
}
