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

public class HttpTaskServerPrioritizedTest {
    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    LocalDateTime startTime;
    Duration duration;
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerPrioritizedTest() throws IOException {
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
    void shouldGetPrioritizedTasksBeEqualSetTasksByTime() throws TasksIntersectException, IOException, InterruptedException {
        Task task1 = new Task("Test History", "task1", duration, startTime);
        Task task2 = new Task("Test History", "task2", duration, startTime.minusMinutes(50));
        Task task3 = new Task("Test History", "task3", duration, startTime.plusMinutes(50));
        Task task4 = new Task("Test History", "task4", duration, startTime.plusMinutes(25));
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);
        inMemoryTaskManager.createTask(task4);
        List<Task> setTasks = new ArrayList<>();
        setTasks.add(task2);
        setTasks.add(task1);
        setTasks.add(task4);
        setTasks.add(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> getTasksFromHTTP = gson.fromJson(response.body(), TypeToken.getParameterized(List.class, Task.class).getType());

        assertEquals(setTasks, getTasksFromHTTP, "Tasks не от сортировались по времени старта.");
    }

    @Test
    public void shouldReturnErrorByWrongRequestFormat() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized/");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Tasks получены.");
    }
}
