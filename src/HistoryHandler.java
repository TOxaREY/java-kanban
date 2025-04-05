import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY -> getHistory(exchange);
            case UNKNOWN -> sendNotFound404Code(exchange, "Неизвестный метод для истории");
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        try {
            String historyJson = gson.toJson(taskManager.getHistory());
            sendText200Code(exchange, historyJson);
        } catch (IOException e) {
            sendInternalServerError500Code(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("history")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_HISTORY;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint { GET_HISTORY, UNKNOWN }
}
