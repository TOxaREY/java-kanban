import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime = null;

    public Epic(String name, String description) {
        super(name, description);
        super.setDuration(Duration.ZERO);
    }

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", duration=" + super.getDuration() +
                ", startTime=" + super.getStartTime() +
                ", endTime=" + endTime +
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
            super.setDuration(Duration.ZERO);
            super.setStartTime(null);
            endTime = null;
        } else {
            super.setStartTime(
                    subtasksId.stream()
                            .map(subtasks::get)
                            .min(Comparator.comparing(Task::getStartTime))
                            .orElseThrow()
                            .getStartTime()
            );

            endTime = subtasksId.stream()
                    .map(subtasks::get)
                    .max(Comparator.comparing(Task::getEndTime))
                    .orElseThrow()
                    .getEndTime();
            super.setDuration(Duration.between(super.getStartTime(), endTime));
        }
    }
}
