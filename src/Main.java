public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Первая задача", "з1", taskManager.setId());//id 0
        Task task2 = new Task("Вторая задача", "з2", taskManager.setId());//id 1
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Первый эпик", "э1", taskManager.setId());//id 2
        Epic epic2 = new Epic("Второй эпик", "э2", taskManager.setId());//id 3
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("Первая подзадача", "п1", taskManager.setId());//id 4
        Subtask subtask2 = new Subtask("Вторая подзадача", "п2", taskManager.setId());//id 5
        Subtask subtask3 = new Subtask("Первая подзадача", "п3", taskManager.setId());//id 6
        taskManager.createSubtask(subtask1, epic1);
        taskManager.createSubtask(subtask2, epic1);
        taskManager.createSubtask(subtask3, epic2);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());

        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(3);
        taskManager.deleteSubtaskById(6);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllSubtasksByEpic(epic1));
        System.out.println(taskManager.getTaskById(0));

        taskManager.deleteAllSubtasks();

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
    }
}
