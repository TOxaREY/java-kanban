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

public class HttpTaskServerEpicsTest {
    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    LocalDateTime startTime;
    Duration duration;
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerEpicsTest() throws IOException {
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
    public void shouldGeEpicFromTaskManagerBeEqualToCreatedEpic() throws IOException, InterruptedException {
        Task epic = new Epic("Test created", "epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task epicFromManager = inMemoryTaskManager.getEpicById(0);

        assertEquals(epic.getName(), epicFromManager.getName(), "Epic не создался.");
    }

    @Test
    public void shouldReturnErrorSetEpicByWrongEpicFormat() throws IOException, InterruptedException {
        Task epic = new Epic("Test created", "epic");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epic.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode(), "Epic создался.");
    }

    @Test
    public void shouldReturnErrorSetEpicByWrongRequestFormat() throws IOException, InterruptedException {
        Task epic = new Epic("Test created", "epic");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epic.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Epic создался.");
    }

    @Test
    void shouldReturnTrueGetAllEpicsIsEmptyAfterDeletingAllEpics() throws IOException, InterruptedException {
        Task epic = new Epic("Test Delete", "epic");
        Task epic1 = new Epic("Test Delete", "epic1");

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        epicJson = gson.toJson(epic1);

        HttpRequest request2 = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
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

        ArrayList<Task> getAllEpics = inMemoryTaskManager.getAllEpics();

        assertTrue(getAllEpics.isEmpty(), "Epics не удалились.");
    }

    @Test
    public void shouldGetEpicFromHTTPBeEqualToCreatedEpic() throws IOException, InterruptedException {
        Task epic = new Epic("Test get", "epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI urlGet = URI.create("http://localhost:8080/epics/0");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode());

        epic.setId(0);
        Task epic1 = gson.fromJson(responseGet.body(), Epic.class);

        assertEquals(epic, epic1, "Epic не совпадает.");
    }

    @Test
    void shouldReturnErrorGetEpicByDoesNotExistId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI urlGet = URI.create("http://localhost:8080/epics/10");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGet.statusCode());

        assertEquals("Эпика с id=10 не существует", responseGet.body(), "Epic получен.");
    }

    @Test
    public void shouldGetEpicsFromHTTPBeEqualToCreatedEpics() throws IOException, InterruptedException {
        Task epic = new Epic("Test get", "epic");
        Task epic1 = new Epic("Test get", "epic1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        epicJson = gson.toJson(epic1);
        HttpRequest request2 = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        epic.setId(0);
        epic1.setId(1);
        List<Task> epics = List.of(epic, epic1);

        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode());

        List<Task> epicsFromHTTP = gson.fromJson(responseGet.body(), TypeToken.getParameterized(List.class, Epic.class).getType());

        assertEquals(epics, epicsFromHTTP, "Epics не получены.");
    }

    @Test
    void shouldReturnErrorGetEpicAfterDeletingEpic() throws IOException, InterruptedException {
        Task epic = new Epic("Test Delete", "epic");

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI urlDelete = URI.create("http://localhost:8080/epics/0");
        HttpRequest requestDelete = HttpRequest
                .newBuilder()
                .uri(urlDelete)
                .DELETE()
                .build();

        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());

        URI urlGet = URI.create("http://localhost:8080/epics/0");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGet.statusCode());

        assertEquals("Эпика с id=0 не существует", responseGet.body(), "Epic не удалился.");
    }

    @Test
    void shouldReturnErrorDeleteEpicByDoesNotExistId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/10");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGet.statusCode());

        assertEquals("Эпика с id=10 не существует", responseGet.body(), "Epic удалился.");
    }

    @Test
    public void shouldGetAllSubtaskBeEqualToAddSubtasksInEpic() throws TasksIntersectException, IOException, InterruptedException {
        Task subtask = new Subtask("Test additions", "subtask", duration, startTime);
        Task subtask2 = new Subtask("Test additions", "subtask2", duration, startTime.plusMinutes(60));
        Task epic = new Epic("Test additions", "epic");
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask2, epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> subtasks = List.of(subtask, subtask2);

        List<Task> subtasksFromHTTP = gson.fromJson(response.body(), TypeToken.getParameterized(List.class, Subtask.class).getType());

        assertEquals(subtasks, subtasksFromHTTP, "Subtasks не получены.");
    }

    @Test
    void shouldReturnErrorAfterGetAllSubtaskInEpicByDoesNotExistId() throws IOException, InterruptedException, TasksIntersectException {
        Task subtask = new Subtask("Test additions", "subtask", duration, startTime);
        Task subtask2 = new Subtask("Test additions", "subtask2", duration, startTime.plusMinutes(60));
        Task epic = new Epic("Test additions", "epic");
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask, epic);
        inMemoryTaskManager.addSubtaskToEpic(subtask2, epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        assertEquals("Эпика с id=0 не существует", response.body(), "Subtasks получены.");
    }

    @Test
    public void shouldReturnErrorByWrongRequestFormat() throws IOException, InterruptedException {
        Task epic = new Epic("Test created", "epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/5");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Epic создался.");
    }
}