import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static Integer id = 0;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();

    public void setId() {
        TaskManager.id++;
    }

    public Integer getId() {
        return id;
    }

    public Epic creatingEpic(String name, String description) {
        setId();
        epics.put(getId(), new Epic(name, description, getId(), Status.NEW));
        return getEpicById(getId());
    }

    public void creatingSubtask(String name, String description, Epic epic) {
        setId();
        Subtask subtask = new Subtask(name, description, getId(), Status.NEW);
        epic.setSubtaskId(getId());
        subtask.setEpicId(epic.getId());
        subtasks.put(getId(), subtask);
        updatingEpic(epic);
    }

    public void creatingTask(String name, String description) {
        setId();
        tasks.put(getId(), new Task(name, description, getId(), Status.NEW));
    }

    public void updatingTask(Status newStatus, Task task) {
        if (!newStatus.equals(task.getStatus())) {
            task.setStatus(newStatus);
            tasks.put(task.getId(), task);
        }
    }

    public void updatingSubtask(Status newStatus, Subtask subtask) {
        if (!newStatus.equals(subtask.getStatus())) {
            subtask.setStatus(newStatus);
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getEpicId() != null) {
                updatingEpic(epics.get(subtask.getEpicId()));
            }
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

    public void deleteAllEpics() {
        epics.clear();
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
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

    public void deleteEpicById(Integer id) {
        epics.remove(id);
    }

    public void deleteSubtaskById(Integer id) {
        subtasks.remove(id);
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    public ArrayList<Subtask> getAllSubtasksByEpic(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            return null;
        } else {
            ArrayList<Subtask> subtasks = new ArrayList<>();
            for (Integer i : epic.getSubtasksId()) {
                subtasks.add(subtasks.get(i));
            }
            return subtasks;
        }
    }

    private void updatingEpic(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(getStatusSubtasks(epic));
        }
        epics.put(epic.getId(), epic);
    }

    private Boolean checkStatusNewSubtasks(Epic epic) {
        for (Integer i : epic.getSubtasksId()) {
            if (!subtasks.get(i).getStatus().equals(Status.NEW)) {
                return false;
            }
        }
        return true;
    }

    private Boolean checkStatusDoneSubtasks(Epic epic) {
        for (Integer i : epic.getSubtasksId()) {
            if (!subtasks.get(i).getStatus().equals(Status.DONE)) {
                return false;
            }
        }
        return true;
    }

    private Status getStatusSubtasks(Epic epic) {
        if (checkStatusNewSubtasks(epic)) {
            return Status.NEW;
        } else if (checkStatusDoneSubtasks(epic)) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }
}
