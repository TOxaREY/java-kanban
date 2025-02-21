import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("task1", "task");//id=0
        Task task2 = new Task("task2", "task");//id=1
        Epic epic1 = new Epic("epic1", "epic");//id=2
        Subtask subtask1 = new Subtask("subtask1", "subtask");//id=4
        Subtask subtask2 = new Subtask("subtask2", "subtask");//id=5
        Subtask subtask3 = new Subtask("subtask3", "subtask");//id=6
        Epic epic2 = new Epic("epic2", "epic");//id=3

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.addSubtaskToEpic(subtask1, epic1);
        taskManager.addSubtaskToEpic(subtask2, epic1);
        taskManager.addSubtaskToEpic(subtask3, epic1);

        taskManager.getTaskById(task1.getId());//id=0
        System.out.println("id=0 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.getSubtaskById(subtask2.getId());//id=5
        System.out.println("id=0,5 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.getSubtaskById(subtask1.getId());//id=4
        System.out.println("id=0,5,4 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.getEpicById(epic2.getId());//id=3
        System.out.println("id=0,5,4,3 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.getTaskById(task2.getId());//id=1
        System.out.println("id=0,5,4,3,1 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.getEpicById(epic1.getId());//id=2
        System.out.println("id=0,5,4,3,1,2 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.getSubtaskById(subtask3.getId());//id=6
        System.out.println("id=0,5,4,3,1,2,6 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.getEpicById(epic2.getId());//id=3
        System.out.println("id=0,5,4,1,2,6,3 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.getTaskById(task1.getId());//id=0
        System.out.println("id=5,4,1,2,6,3,0 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.deleteTaskById(task2.getId());//id=1
        System.out.println("id=5,4,2,6,3,0 --> " + getStringFromHistory(taskManager.getHistory()));
        taskManager.deleteEpicById(epic1.getId());//id=2
        System.out.println("id=3,0 --> " + getStringFromHistory(taskManager.getHistory()));
    }

    static String getStringFromHistory(List<Task> tasks) {
        StringBuilder stringTasksId = new StringBuilder("id=");
        for (Task task : tasks) {
            stringTasksId.append(task.getId());
            stringTasksId.append(",");
        }
        stringTasksId.setLength(stringTasksId.length() - 1);
        return stringTasksId.toString();
    }
}
