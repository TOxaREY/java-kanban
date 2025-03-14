import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();
    private Duration duration = Duration.ZERO;
    private LocalDateTime startTime = null;
    private LocalDateTime endTime= null;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description);
        this.duration = duration;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", subtasksId=" + getSubtasksId() +
                '}';
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtaskId(Integer subtaskId) {
        subtasksId.add(subtaskId);
    }

    public void clearSubtasksId() {
        subtasksId.clear();
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtasksId.remove(subtaskId);
    }

    public void updateEpicDateTimeFields(HashMap<Integer, Task> subtasks) {
        if (getSubtasksId().isEmpty()) {
            duration = Duration.ZERO;
            startTime = null;
            endTime = null;
        } else {
            startTime = subtasksId.stream()
                    .map(subtasks::get)
                    .min(Comparator.comparing(Task::getStartTime))
                    .orElseThrow()
                    .getStartTime();

            endTime = subtasksId.stream()
                    .map(subtasks::get)
                    .max(Comparator.comparing(Task::getEndTime))
                    .orElseThrow()
                    .getEndTime();
            duration = Duration.between(startTime, endTime);
        }
    }
}
