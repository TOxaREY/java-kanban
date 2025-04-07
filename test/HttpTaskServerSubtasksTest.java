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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerSubtasksTest {
    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    LocalDateTime startTime;
    Duration duration;
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerSubtasksTest() throws IOException {}

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
    public void shouldGeSubtaskFromTaskManagerBeEqualToCreatedSubtask() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test created", "subtask", duration, startTime);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task subtaskFromManager = inMemoryTaskManager.getSubtaskById(0);

        assertEquals(subtask.getName(), subtaskFromManager.getName(), "Subtask не создался.");
    }

    @Test
    public void shouldGetSubtaskStatusBeEqualToUpdatedSubtaskStatus() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test updated", "subtask", duration, startTime);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        subtask.setStatus(Status.DONE);
        subtask.setId(0);
        subtaskJson = gson.toJson(subtask);

        HttpRequest requestUpdated = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> responseUpdated = client.send(requestUpdated, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseUpdated.statusCode());

        Task subtaskFromManagerUpdated = inMemoryTaskManager.getSubtaskById(0);

        assertEquals(Status.DONE, subtaskFromManagerUpdated.getStatus(), "Subtask не обновился.");
    }

    @Test
    void shouldReturnErrorSetUpdatedSubtaskByDoesNotExistId() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test updated", "subtask", duration, startTime);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        subtask.setStatus(Status.DONE);
        subtask.setId(10);
        subtaskJson = gson.toJson(subtask);

        HttpRequest requestUpdated = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> responseUpdated = client.send(requestUpdated, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseUpdated.statusCode());

        assertEquals("Подзадачи с id=10 не существует", responseUpdated.body(), "Subtask обновился.");
    }

    @Test
    void shouldReturnErrorAfterSetIntersectSubtask() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test intersect", "subtask", duration, startTime);
        Task subtask2 = new Subtask("Test intersect", "subtask", duration.minusMinutes(5), startTime);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        subtaskJson = gson.toJson(subtask2);

        HttpRequest requestUpdated = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> responseUpdated = client.send(requestUpdated, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, responseUpdated.statusCode());

        assertEquals("Subtask name: Test intersect пересекается по времени выполнения с другими задачами.", responseUpdated.body(), "Subtask создался.");
    }

    @Test
    public void shouldReturnErrorSetSubtaskByWrongSubtaskFormat() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test created", "subtask", duration, startTime);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtask.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode(), "Subtask создался.");
    }

    @Test
    public void shouldReturnErrorSetSubtaskByWrongRequestFormat() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test created", "subtask", duration, startTime);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Subtask создался.");
    }

    @Test
    void shouldReturnTrueGetAllSubtasksIsEmptyAfterDeletingAllSubtasks() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);
        Task subtask2 = new Subtask("Test Delete", "subtask1", duration, startTime.plusMinutes(50));

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        subtaskJson = gson.toJson(subtask2);

        HttpRequest request2 = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
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

        ArrayList<Task> getAllSubtasks = inMemoryTaskManager.getAllSubtasks();

        assertTrue(getAllSubtasks.isEmpty(), "Subtasks не удалились.");
    }

    @Test
    public void shouldGetSubtaskFromHTTPBeEqualToCreatedSubtask() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test get", "subtask", duration, startTime);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI urlGet = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode());

        subtask.setId(0);
        Task subtask1 = gson.fromJson(responseGet.body(), Subtask.class);

        assertEquals(subtask, subtask1, "Subtask не совпадает.");
    }

    @Test
    void shouldReturnErrorGetSubtaskByDoesNotExistId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI urlGet = URI.create("http://localhost:8080/subtasks/10");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGet.statusCode());

        assertEquals("Подзадачи с id=10 не существует", responseGet.body(), "Subtask получен.");
    }

    @Test
    void shouldReturnErrorGetSubtaskAfterDeletingSubtask() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test Delete", "subtask", duration, startTime);

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI urlDelete = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest requestDelete = HttpRequest
                .newBuilder()
                .uri(urlDelete)
                .DELETE()
                .build();

        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());

        URI urlGet = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGet.statusCode());

        assertEquals("Подзадачи с id=0 не существует", responseGet.body(), "Subtask не удалился.");
    }

    @Test
    void shouldReturnErrorDeleteSubtaskByDoesNotExistId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/10");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGet.statusCode());

        assertEquals("Подзадачи с id=10 не существует", responseGet.body(), "Subtask удалился.");
    }

    @Test
    public void shouldGetSubtasksFromHTTPBeEqualToCreatedSubtasks() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test get", "subtask", duration, startTime);
        Task subtask2 = new Subtask("Test get", "subtask", duration, startTime.plusMinutes(100));
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        subtaskJson = gson.toJson(subtask2);
        HttpRequest request2 = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        subtask.setId(0);
        subtask2.setId(1);
        List<Task> subtasks = List.of(subtask, subtask2);

        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode());

        List<Task> subtasksFromHTTP = gson.fromJson(responseGet.body(), TypeToken.getParameterized(List.class, Subtask.class).getType());

        assertEquals(subtasks, subtasksFromHTTP, "Subtasks не получены.");
    }

    @Test
    public void shouldGetSubtaskBeEqualToAddSubtaskInEpic() throws TasksIntersectException, IOException, InterruptedException {
        Task subtask = new Subtask("Test additions", "subtask", duration, startTime);
        Task epic = new Epic("Test additions", "epic");
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createEpic(epic);

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/0/epic/1");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task subtaskGet = inMemoryTaskManager.getAllSubtasksByEpic(epic).getFirst();

        assertEquals(subtask, subtaskGet, "Subtasks не добавился.");
    }

    @Test
    void shouldReturnErrorAfterAddSubtaskInEpicByDoesNotExistId() throws IOException, InterruptedException, TasksIntersectException {
        Task subtask = new Subtask("Test additions", "subtask", duration, startTime);
        Task epic = new Epic("Test additions", "epic");
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createEpic(epic);

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/10/epic/1");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        assertEquals("Подзадачи с id=10 не существует", response.body(), "Subtask добавился.");
    }

    @Test
    void shouldReturnErrorAfterAddSubtaskInEpicWithoutStartTime() throws IOException, InterruptedException, TasksIntersectException {
        Task subtask = new Subtask("Test additions", "subtask", duration, null);
        Task epic = new Epic("Test additions", "epic");
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createEpic(epic);

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/0/epic/1");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

        assertEquals("У subtask нет времени старта. Добавление в epic невозможен.", response.body(), "Subtask создался.");
    }

    @Test
    public void shouldReturnErrorByWrongRequestFormat() throws IOException, InterruptedException {
        Task subtask = new Subtask("Test created", "subtask", duration, startTime);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/5");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Subtask создался.");
    }
}
