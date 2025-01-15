import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.creatingTask("Первая задача", "з1");
        taskManager.creatingTask("Вторая задача", "з2");
        Epic epic1 = taskManager.creatingEpic("Первый эпик", "э1");
        taskManager.creatingSubtask("Первая подзадача", "п1", epic1);
        taskManager.creatingSubtask("Вторая подзадача", "п2", epic1);
        Epic epic2 = taskManager.creatingEpic("Второй эпик", "э2");
        taskManager.creatingSubtask("Первая подзадача", "п3", epic2);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.updatingTask(Status.IN_PROGRESS, taskManager.getTaskById(1));
        taskManager.updatingTask(Status.DONE, taskManager.getTaskById(2));
        taskManager.updatingSubtask(Status.DONE, taskManager.getSubtaskById(4));
        taskManager.updatingSubtask(Status.DONE, taskManager.getSubtaskById(5));
        taskManager.updatingSubtask(Status.IN_PROGRESS, taskManager.getSubtaskById(7));

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(3);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
    }
}
