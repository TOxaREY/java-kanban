import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case CREATE_UPDATE_TASK -> createUpdateSubtask(exchange);
            case GET_ALL_TASKS -> getAllSubtasks(exchange);
            case GET_TASK_BY_ID -> getSubtaskById(exchange);
            case DELETE_ALL_TASKS -> deleteAllSubtasks(exchange);
            case DELETE_TASK_BY_ID -> deleteSubtaskById(exchange);
            case ADD_SUBTASK_TO_EPIC -> addSubtaskToEpic(exchange);
            case UNKNOWN -> sendNotFound404Code(exchange, "Неизвестный метод для подзадачи");
        }
    }

    private void createUpdateSubtask(HttpExchange exchange) throws IOException {
        List<String> contentTypeValues = exchange.getRequestHeaders().get("Content-type");
        if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Task subtask = gson.fromJson(body, Subtask.class);
                    if (subtask.getId() == null) {
                        taskManager.createSubtask(new Subtask(subtask.getName(), subtask.getDescription(), subtask.getDuration(), subtask.getStartTime()));
                        sendWithoutText201Code(exchange);
                    } else {
                        try {
                            taskManager.getSubtaskById(subtask.getId());
                            taskManager.updateSubtask(subtask);
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

    private void deleteAllSubtasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllSubtasks();
            sendText200Code(exchange, "Все подзадачи удалены");
        } catch (IOException e) {
            sendInternalServerError500Code(exchange, e.getMessage());
        }
    }

    private void getSubtaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Integer id = Integer.parseInt(pathParts[2]);
        try {
            String subtaskJson = gson.toJson(taskManager.getSubtaskById(id));
            sendText200Code(exchange, subtaskJson);
        } catch (NotFoundException e) {
            sendNotFound404Code(exchange, e.getMessage());
        }
    }

    private void deleteSubtaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Integer id = Integer.parseInt(pathParts[2]);
        try {
            taskManager.deleteSubtaskById(id);
            sendText200Code(exchange, "Подзадача id=" + id + " удалена");
        } catch (NotFoundException e) {
            sendNotFound404Code(exchange, e.getMessage());
        }
    }

    private void getAllSubtasks(HttpExchange exchange) throws IOException {
        try {
            String allSubtasksJson = gson.toJson(taskManager.getAllSubtasks());
            sendText200Code(exchange, allSubtasksJson);
        } catch (IOException e) {
            sendInternalServerError500Code(exchange, e.getMessage());
        }
    }

    private void addSubtaskToEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Integer idSubtask = Integer.parseInt(pathParts[2]);
        Integer idEpic = Integer.parseInt(pathParts[4]);
        try {
            Task subtask = taskManager.getSubtaskById(idSubtask);
            Task epic = taskManager.getEpicById(idEpic);
            taskManager.addSubtaskToEpic(subtask, epic);
            sendWithoutText201Code(exchange);
        } catch (NotFoundException e) {
            sendNotFound404Code(exchange, e.getMessage());
        } catch (TasksIntersectException e) {
            sendHasInteractions406Code(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
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

        if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_TASK_BY_ID;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE_TASK_BY_ID;
                }
            }
        }

        if (pathParts.length == 5 && pathParts[1].equals("subtasks") && pathParts[3].equals("epic")) {
            if (requestMethod.equals("POST")) {
                return Endpoint.ADD_SUBTASK_TO_EPIC;
            }
        }

        return Endpoint.UNKNOWN;
    }
}
