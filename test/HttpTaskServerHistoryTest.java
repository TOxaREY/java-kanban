import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerHistoryTest {
    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    LocalDateTime startTime;
    Duration duration;
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerHistoryTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        startTime = LocalDateTime.of(2025, 3, 11, 0, 0);
        duration = Duration.ofMinutes(10);
        inMemoryTaskManager.deleteAllTasks();
        inMemoryTaskManager.deleteAllEpics();
        inMemoryTaskManager.deleteAllSubtasks();
        httpTaskServer.start();
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
    }

    @Test
    void shouldGetHistoryTasksBeEqualSetTasks() throws TasksIntersectException, NotFoundException, IOException, InterruptedException {
        Task task = new Task("Test History", "task");
        Task task2 = new Task("Test History", "task2");
        Task task3 = new Task("Test History", "task3", duration, startTime);
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);
        List<Task> setHistory = new ArrayList<>();
        setHistory.add(task);
        setHistory.add(task2);
        setHistory.add(task3);
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task3.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> getHistoryFromHTTP = gson.fromJson(response.body(), TypeToken.getParameterized(List.class, Task.class).getType());

        assertEquals(setHistory, getHistoryFromHTTP, "Tasks не сохранились в истории.");
    }

    @Test
    public void shouldReturnErrorByWrongRequestFormat() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history/");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "История получена.");
    }
}
