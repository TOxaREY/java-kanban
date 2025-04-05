import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_PRIORITIZED -> getPrioritized(exchange);
            case UNKNOWN -> sendNotFound404Code(exchange, "Неизвестный метод для приоритета задач");
        }
    }

    private void getPrioritized(HttpExchange exchange) throws IOException {
        try {
            String prioritizedJson = gson.toJson(taskManager.getPrioritizedTasks());
            sendText200Code(exchange, prioritizedJson);
        } catch (IOException e) {
            sendInternalServerError500Code(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("prioritized")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint { GET_PRIORITIZED, UNKNOWN }
}
