import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) throws IOException {
        File file = File.createTempFile("list_tasks", ".csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("task1", "task");//id=0
        Epic epic1 = new Epic("epic1", "epic");//id=1
        Subtask subtask1 = new Subtask("subtask1", "subtask");//id=2
        Subtask subtask2 = new Subtask("subtask2", "subtask");//id=3

        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createSubtask(subtask1);
        fileBackedTaskManager.createSubtask(subtask2);
        fileBackedTaskManager.addSubtaskToEpic(subtask1, epic1);
        fileBackedTaskManager.addSubtaskToEpic(subtask2, epic1);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);

        System.out.println(fileBackedTaskManager.getAllTasks().equals(fileBackedTaskManager1.getAllTasks()));
        System.out.println(fileBackedTaskManager.getAllEpics().equals(fileBackedTaskManager1.getAllEpics()));
        System.out.println(fileBackedTaskManager.getAllSubtasks().equals(fileBackedTaskManager1.getAllSubtasks()));
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(reader)) {
            br.readLine();
            while (br.ready()) {
                Task task = fileBackedTaskManager.fromString(br.readLine());
                if (task.getClass().equals(Task.class)) {
                    fileBackedTaskManager.restoreTask(task);
                } else if (task.getClass().equals(Epic.class)) {
                    fileBackedTaskManager.restoreEpic(task);
                } else if (task.getClass().equals(Subtask.class)) {
                    fileBackedTaskManager.restoreSubtask(task);
                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Task epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Task subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Task epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Task subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void addSubtaskToEpic(Task subtask, Task epic) {
        super.addSubtaskToEpic(subtask, epic);
        save();
    }

    private String toString(Task task) {
        ArrayList<String> strArrList = new ArrayList<>();
        strArrList.add(String.valueOf(task.getId()));
        strArrList.add(String.valueOf(task.getName()));
        strArrList.add(String.valueOf(task.getStatus()));
        strArrList.add(String.valueOf(task.getDescription()));

        if (task.getClass().equals(Task.class)) {
            strArrList.add(1, String.valueOf(Types.TASK));
        } else if (task.getClass().equals(Epic.class)) {
            strArrList.add(1, String.valueOf(Types.EPIC));
            if (((Epic) task).getSubtasksId() != null) {
                StringBuilder joinId = new StringBuilder();
                for (Integer i : ((Epic) task).getSubtasksId()) {
                    joinId.append(i).append("r");
                }
                strArrList.add(5, joinId.toString());
            }
        } else if (task.getClass().equals(Subtask.class)) {
            strArrList.add(1, String.valueOf(Types.SUBTASK));
            if (((Subtask) task).getEpicId() != null) {
                strArrList.add(5, String.valueOf(((Subtask) task).getEpicId()));
            }
        }

        return String.join(",", strArrList);
    }

    private Task fromString(String value) {
        String[] split = value.split(",");
        Task task = null;
        if (split[1].equals(Types.TASK.toString())) {
            task = new Task(split[2], split[4]);
        } else if (split[1].equals(Types.EPIC.toString())) {
            task = new Epic(split[2], split[4]);
            if (split.length == 6) {
                String[] splitId = split[5].split("r");
                for (String s : splitId) {
                    ((Epic) task).setSubtaskId(Integer.parseInt(s));
                }
            }
        } else if (split[1].equals(Types.SUBTASK.toString())) {
            task = new Subtask(split[2], split[4]);
            if (split.length == 6) {
                ((Subtask) task).setEpicId(Integer.parseInt(split[5]));
            }
        }
        if (task != null) {
            task.setId(Integer.parseInt(split[0]));
            if (split[3].equals(Status.NEW.toString())) {
                task.setStatus(Status.NEW);
            } else if (split[3].equals(Status.IN_PROGRESS.toString())) {
                task.setStatus(Status.IN_PROGRESS);
            } else if (split[3].equals(Status.DONE.toString())) {
                task.setStatus(Status.DONE);
            }
        }

        return task;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8); BufferedWriter br = new BufferedWriter(writer)) {
            br.write("id,type,name,status,description,epic\n");
            for (Task task : super.getAllTasks()) {
                br.write(toString(task) + "\n");
            }
            for (Task task : super.getAllEpics()) {
                br.write(toString(task) + "\n");
            }
            for (Task task : super.getAllSubtasks()) {
                br.write(toString(task) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }
}