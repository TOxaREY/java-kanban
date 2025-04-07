import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    static HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        setHttpServer(Managers.getDefault(), httpServer);
        httpServer.start();
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        setHttpServer(taskManager, httpServer);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.serializeNulls();
        return gsonBuilder.create();
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    private static void setHttpServer(TaskManager taskManager, HttpServer httpServer) {
        httpServer.createContext("/tasks", new TasksHandler(taskManager, getGson()));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, getGson()));
        httpServer.createContext("/epics", new EpicsHandler(taskManager, getGson()));
        httpServer.createContext("/history", new HistoryHandler(taskManager, getGson()));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, getGson()));
    }
}
