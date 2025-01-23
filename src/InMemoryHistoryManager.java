import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == 10) {
                history.removeFirst();
            }
            history.add(task.clone());
        }
    }

    @Override
    public List<Task> getHistory() {
       return history;
    }
}
