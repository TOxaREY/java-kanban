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

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTasksTest {
    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    LocalDateTime startTime;
    Duration duration;
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerTasksTest() throws IOException {}

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
    public void shouldGetTaskFromTaskManagerBeEqualToCreatedTask() throws IOException, InterruptedException {
        Task task = new Task("Test created", "task", duration, startTime);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task taskFromManager = inMemoryTaskManager.getTaskById(0);

        assertEquals(task.getName(), taskFromManager.getName(), "Task не создался.");
    }

    @Test
    public void shouldGetTaskStatusBeEqualToUpdatedTaskStatus() throws IOException, InterruptedException {
        Task task = new Task("Test updated", "task", duration, startTime);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        task.setStatus(Status.DONE);
        task.setId(0);
        taskJson = gson.toJson(task);

        HttpRequest requestUpdated = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> responseUpdated = client.send(requestUpdated, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseUpdated.statusCode());

        Task taskFromManagerUpdated = inMemoryTaskManager.getTaskById(0);

        assertEquals(Status.DONE, taskFromManagerUpdated.getStatus(), "Task не обновился.");
    }

    @Test
    void shouldReturnErrorSetUpdatedTaskByDoesNotExistId() throws IOException, InterruptedException {
        Task task = new Task("Test updated", "task", duration, startTime);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        task.setStatus(Status.DONE);
        task.setId(10);
        taskJson = gson.toJson(task);

        HttpRequest requestUpdated = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> responseUpdated = client.send(requestUpdated, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseUpdated.statusCode());

        assertEquals("Задачи с id=10 не существует", responseUpdated.body(), "Task обновился.");
    }

    @Test
    void shouldReturnErrorAfterSetIntersectTask() throws IOException, InterruptedException {
        Task task = new Task("Test intersect", "task", duration, startTime);
        Task task2 = new Task("Test intersect", "task", duration.minusMinutes(5), startTime);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        taskJson = gson.toJson(task2);

        HttpRequest requestUpdated = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> responseUpdated = client.send(requestUpdated, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, responseUpdated.statusCode());

        assertEquals("Task name: Test intersect пересекается по времени выполнения с другими задачами.", responseUpdated.body(), "Task создался.");
    }

    @Test
    public void shouldReturnErrorSetTaskByWrongTaskFormat() throws IOException, InterruptedException {
        Task task = new Task("Test created", "task", duration, startTime);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(task.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode(), "Task создался.");
    }

    @Test
    public void shouldReturnErrorSetTaskByWrongRequestFormat() throws IOException, InterruptedException {
        Task task = new Task("Test created", "task", duration, startTime);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Task создался.");
    }

    @Test
    void shouldReturnTrueGetAllTasksIsEmptyAfterDeletingAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Test Delete", "task", duration, startTime);
        Task task2 = new Task("Test Delete", "task1", duration, startTime.plusMinutes(50));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        taskJson = gson.toJson(task2);

        HttpRequest request2 = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();


        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        HttpRequest requestDelete = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());

        ArrayList<Task> getAllTasks = inMemoryTaskManager.getAllTasks();

        assertTrue(getAllTasks.isEmpty(), "Tasks не удалились.");
    }

    @Test
    public void shouldGetTaskFromHTTPBeEqualToCreatedTask() throws IOException, InterruptedException {
        Task task = new Task("Test created", "task", duration, startTime);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI urlGet = URI.create("http://localhost:8080/tasks/0");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode());

        task.setId(0);
        Task task1 = gson.fromJson(responseGet.body(), Task.class);

        assertEquals(task, task1, "Task не совпадает.");
    }

    @Test
    void shouldReturnErrorGetTaskByDoesNotExistId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI urlGet = URI.create("http://localhost:8080/tasks/10");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGet.statusCode());

        assertEquals("Задачи с id=10 не существует", responseGet.body(), "Task получен.");
    }

    @Test
    void shouldReturnErrorGetTaskAfterDeletingTask() throws IOException, InterruptedException {
        Task task = new Task("Test Delete", "task", duration, startTime);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI urlDelete = URI.create("http://localhost:8080/tasks/0");
        HttpRequest requestDelete = HttpRequest
                .newBuilder()
                .uri(urlDelete)
                .DELETE()
                .build();

        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());

        URI urlGet = URI.create("http://localhost:8080/tasks/0");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGet.statusCode());

        assertEquals("Задачи с id=0 не существует", responseGet.body(), "Task не удалился.");
    }

    @Test
    void shouldReturnErrorDeleteTaskByDoesNotExistId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/10");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGet.statusCode());

        assertEquals("Задачи с id=10 не существует", responseGet.body(), "Task удалился.");
    }

    @Test
    public void shouldGetTasksFromHTTPBeEqualToCreatedTasks() throws IOException, InterruptedException {
        Task task = new Task("Test created", "task", duration, startTime);
        Task task2 = new Task("Test created", "task", duration, startTime.plusMinutes(100));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        taskJson = gson.toJson(task2);
        HttpRequest request2 = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        task.setId(0);
        task2.setId(1);
        List<Task> tasks = List.of(task, task2);

        URI urlGet = URI.create("http://localhost:8080/tasks");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode());

        List<Task> tasksFromHTTP = gson.fromJson(responseGet.body(), TypeToken.getParameterized(List.class, Task.class).getType());

        assertEquals(tasks, tasksFromHTTP, "Tasks не получены.");
    }

    @Test
    public void shouldReturnErrorByWrongRequestFormat() throws IOException, InterruptedException {
        Task task = new Task("Test created", "task", duration, startTime);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/5");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Task создался.");
    }
}
