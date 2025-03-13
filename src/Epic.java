import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
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

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public Duration getDuration() {
        return duration;
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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
