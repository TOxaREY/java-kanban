import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case CREATE_EPIC -> createEpic(exchange);
            case GET_ALL_EPICS -> getAllEpics(exchange);
            case GET_EPIC_BY_ID -> getEpicById(exchange);
            case DELETE_ALL_EPICS -> deleteAllEpics(exchange);
            case DELETE_EPIC_BY_ID -> deleteEpicsById(exchange);
            case GET_ALL_SUBTASKS_BY_EPIC -> getAllSubtasksByEpic(exchange);
            case UNKNOWN -> sendNotFound404Code(exchange, "Неизвестный метод для эпика");
        }
    }

    private void createEpic(HttpExchange exchange) throws IOException {
        List<String> contentTypeValues = exchange.getRequestHeaders().get("Content-type");
        if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task epic = gson.fromJson(body, Epic.class);
                taskManager.createEpic(epic);
                sendWithoutText201Code(exchange);
            } catch (Exception e) {
                sendInternalServerError500Code(exchange, e.getMessage());
            }
        } else {
            sendNotFound404Code(exchange, "Неверный формат передачи данных");
        }
    }

    private void getAllEpics(HttpExchange exchange) throws IOException {
        try {
            String allEpicsJson = gson.toJson(taskManager.getAllEpics());
            sendText200Code(exchange, allEpicsJson);
        } catch (IOException e) {
            sendInternalServerError500Code(exchange, e.getMessage());
        }
    }

    private void getEpicById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Integer id = Integer.parseInt(pathParts[2]);
        try {
            String epicJson = gson.toJson(taskManager.getEpicById(id));
            sendText200Code(exchange, epicJson);
        } catch (NotFoundException e) {
            sendNotFound404Code(exchange, e.getMessage());
        }
    }

    private void deleteAllEpics(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllEpics();
            sendText200Code(exchange, "Все эпики удалены");
        } catch (IOException e) {
            sendInternalServerError500Code(exchange, e.getMessage());
        }
    }

    private void deleteEpicsById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Integer id = Integer.parseInt(pathParts[2]);
        try {
            taskManager.deleteEpicById(id);
            sendText200Code(exchange, "Эпик id=" + id + " удален");
        } catch (NotFoundException e) {
            sendNotFound404Code(exchange, e.getMessage());
        }
    }

    private void getAllSubtasksByEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Integer id = Integer.parseInt(pathParts[2]);
        try {
            String allSubtasksByEpicJson = gson.toJson(taskManager.getAllSubtasksByEpic(taskManager.getEpicById(id)));
            sendText200Code(exchange, allSubtasksByEpicJson);
        } catch (NotFoundException e) {
            sendNotFound404Code(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_ALL_EPICS;
                }
                case "POST" -> {
                    return Endpoint.CREATE_EPIC;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE_ALL_EPICS;
                }
            }
        }

        if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_EPIC_BY_ID;
                }

                case "DELETE" -> {
                    return Endpoint.DELETE_EPIC_BY_ID;
                }
            }
        }

        if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ALL_SUBTASKS_BY_EPIC;
            }
        }

        return Endpoint.UNKNOWN;
    }

    enum Endpoint { CREATE_EPIC, GET_ALL_EPICS, GET_EPIC_BY_ID, DELETE_ALL_EPICS, DELETE_EPIC_BY_ID, GET_ALL_SUBTASKS_BY_EPIC, UNKNOWN }
}
