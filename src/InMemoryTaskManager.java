import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 0;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Task> subtasks = new HashMap<>();
    public HashMap<Integer, Task> epics = new HashMap<>();
    public HistoryManager historyManager = Managers.getDefaultHistory();
    private final TaskComparator taskComparator = new TaskComparator();
    private final Set<Task> sortTasks = new TreeSet<>(taskComparator);

    @Override
    public void createTask(Task task) throws TasksIntersectException {
        if (task.getStartTime() == null) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
        } else {
            if (getPrioritizedTasks().stream()
                    .noneMatch(task1 -> isDoTwoTasksIntersect(task1, task))) {
                task.setId(generateId());
                tasks.put(task.getId(), task);
                sortTasks.add(task);
            } else {
                throw new TasksIntersectException("Task name: " + task.getName() + " пересекается по времени выполнения с другими задачами.");
            }
        }
    }

    @Override
    public void createEpic(Task epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Task subtask) throws TasksIntersectException {
        if (subtask.getStartTime() == null) {
            subtask.setId(generateId());
            tasks.put(subtask.getId(), subtask);
        } else {
            if (getPrioritizedTasks().stream()
                    .noneMatch(task1 -> isDoTwoTasksIntersect(task1, subtask))) {
                subtask.setId(generateId());
                subtasks.put(subtask.getId(), subtask);
                sortTasks.add(subtask);
            } else {
                throw new TasksIntersectException("Subtask name: " + subtask.getName() + " пересекается по времени выполнения с другими задачами.");
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Task epic) {
        epics.put(epic.getId(), epic);
        updateStatusEpic(epic);
        ((Epic) epic).updateEpicDateTimeFields(subtasks);
    }

    @Override
    public void updateSubtask(Task subtask) {
        subtasks.put(subtask.getId(), subtask);
        if (((Subtask) subtask).getEpicId() != null) {
            Epic epic = (Epic) epics.get(((Subtask) subtask).getEpicId());
            updateEpic(epic);
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
        epics.values().stream()
                .filter(task -> !((Epic) task).getSubtasksId().isEmpty())
                .forEach(task -> {
                    ((Epic) task).clearSubtasksId();
                    updateEpic(task);
                });
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (!((Epic) epics.get(id)).getSubtasksId().isEmpty()) {
            ((Epic) epics.get(id)).getSubtasksId().stream()
                    .flatMap(integer ->
                            subtasks.keySet().stream()
                                    .filter(integer::equals)
                    ).toList()
                    .forEach(integer -> {
                                subtasks.remove(integer);
                                historyManager.remove(integer);
                            }
                    );
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        epics.values().stream()
                .filter(task -> ((Epic) task).getSubtasksId().contains(id))
                .forEach(task -> {
                    ((Epic) task).removeSubtaskId(id);
                    updateEpic(task);
                });

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
            return (ArrayList<Task>) ((Epic) epic).getSubtasksId().stream()
                    .map(integer -> subtasks.get(integer))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void addSubtaskToEpic(Task subtask, Task epic) throws TasksIntersectException {
        if (subtask.getClass().equals(Subtask.class) && epic.getClass().equals(Epic.class)) {
            if (subtask.getStartTime() != null) {
                ((Subtask) subtask).setEpicId(epic.getId());
                ((Epic) epic).setSubtaskId(subtask.getId());
                updateSubtask(subtask);
            } else {
                throw new TasksIntersectException("У subtask нет времени старта. Добавление в epic невозможен.");
            }
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortTasks);
    }

    private Integer generateId() {
        return id++;
    }

    private void updateStatusEpic(Task epic) {
        if (((Epic) epic).getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            List<Status> statuses = ((Epic) epic).getSubtasksId().stream()
                    .map(integer -> subtasks.get(integer).getStatus())
                    .toList();
            Status status = Status.IN_PROGRESS;

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
        tasks.keySet()
                .forEach(integer ->
                        historyManager.remove(id)
                );
    }

    private Boolean isDoTwoTasksIntersect(Task taskInSet, Task task) {
        if (task.getStartTime().isAfter(taskInSet.getEndTime())) {
            return false;
        } else {
            return task.getEndTime().isAfter(taskInSet.getStartTime());
        }
    }
}
