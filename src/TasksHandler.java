import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case CREATE_UPDATE_TASK -> createUpdateTask(exchange);
            case GET_ALL_TASKS -> getAllTasks(exchange);
            case GET_TASK_BY_ID -> getTaskById(exchange);
            case DELETE_ALL_TASKS -> deleteAllTasks(exchange);
            case DELETE_TASK_BY_ID -> deleteTaskById(exchange);
            case UNKNOWN -> sendNotFound404Code(exchange, "Неизвестный метод для задачи");
        }
    }

    private void createUpdateTask(HttpExchange exchange) throws IOException {
        List<String> contentTypeValues = exchange.getRequestHeaders().get("Content-type");
        if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getId() == null) {
                        taskManager.createTask(new Task(task.getName(), task.getDescription(), task.getDuration(), task.getStartTime()));
                        sendWithoutText201Code(exchange);
                    } else {
                        try {
                            taskManager.getTaskById(task.getId());
                            taskManager.updateTask(task);
                            sendWithoutText201Code(exchange);
                        } catch (NotFoundException e) {
                            sendNotFound404Code(exchange, e.getMessage());
                        }
                    }
                } catch (TasksIntersectException e) {
                    sendHasInteractions406Code(exchange, e.getMessage());
                }
            } catch (Exception e) {
                sendInternalServerError500Code(exchange, e.getMessage());
            }
        } else {
            sendNotFound404Code(exchange, "Неверный формат передачи данных");
        }
    }

    private void deleteAllTasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllTasks();
            sendText200Code(exchange, "Все задачи удалены");
        } catch (IOException e) {
            sendInternalServerError500Code(exchange, e.getMessage());
        }
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Integer id = Integer.parseInt(pathParts[2]);
        try {
            String taskJson = gson.toJson(taskManager.getTaskById(id));
            sendText200Code(exchange, taskJson);
        } catch (NotFoundException e) {
            sendNotFound404Code(exchange, e.getMessage());
        }
    }

    private void deleteTaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Integer id = Integer.parseInt(pathParts[2]);
        try {
            taskManager.deleteTaskById(id);
            sendText200Code(exchange, "Задача id=" + id + " удалена");
        } catch (NotFoundException e) {
            sendNotFound404Code(exchange, e.getMessage());
        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        try {
            String allTasksJson = gson.toJson(taskManager.getAllTasks());
            sendText200Code(exchange, allTasksJson);
        } catch (IOException e) {
            sendInternalServerError500Code(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_ALL_TASKS;
                }
                case "POST" -> {
                    return Endpoint.CREATE_UPDATE_TASK;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE_ALL_TASKS;
                }
            }
        }

        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_TASK_BY_ID;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE_TASK_BY_ID;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }
}
