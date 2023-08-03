import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controller.HttpTaskServer;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVClient;
import server.KVServer;
import service.HttpTaskManager;
import service.Managers;
import service.TaskManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверка эндпоинтов
 */
public class HttpTaskServerTest  {
    private HttpTaskServer taskServer;
    private KVServer kvServer;
    private KVClient kvClient;
    private Gson gson = Managers.getGson();
    private TaskManager taskManager;

    @BeforeEach
    void init() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = Managers.getDefault();
        kvClient = new KVClient(8078);
        taskServer = new HttpTaskServer();
        taskServer.start();
    }

    @Test
    void shouldSetTask() throws IOException, InterruptedException {
        Task task = new Task("Поехать на дачу", "Прополоть картошку",
                LocalDateTime.of(2023,1,1,12,10), 100);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(1, taskManager.getListAllTask().size(), "Check size task list");
        assertEquals("Поехать на дачу", taskManager.getListAllTask().get(0).getName(),
                "Check name task");
        assertEquals("Прополоть картошку", taskManager.getListAllTask().get(0).getDescription(),
                "Check description task");
        assertEquals("2023-01-01T12:10", taskManager.getListAllTask().get(0).getStartTime().toString(),
                "Check data start task");
        assertEquals(100, taskManager.getListAllTask().get(0).getDuration(),
                "Check duration task");
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task taskFirst = new Task("Поехать на дачу", "Прополоть картошку",
                LocalDateTime.of(2023,1,1,12,10), 100);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskFirst)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        Task taskSecond = new Task("Работа", "Прийти на работу",
                LocalDateTime.of(2023,1,2,12,10), 50);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Task taskFirstUpdate = new Task("Поехать на дачу", "Полить помидоры",
                LocalDateTime.of(2023,1,2,12,10), 50);
        taskFirstUpdate.setId(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task/?id=1"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskFirstUpdate)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(2, taskManager.getListAllTask().size(), "Check size task list");
        assertEquals("Работа", taskManager.getListAllTask().get(1).getName(), "Check name task");
        assertEquals("Прийти на работу", taskManager.getListAllTask().get(1).getDescription(),
                "Check description task");
        assertEquals("Поехать на дачу", taskManager.getListAllTask().get(0).getName(),
                "Check name task");
        assertEquals("Полить помидоры", taskManager.getListAllTask().get(0).getDescription(),
                "Check description task");
    }

    @Test
    void shouldGetTasks() throws IOException, InterruptedException {
        Task taskFirst = new Task("Поехать на дачу", "Прополоть картошку",
                LocalDateTime.of(2023,1,1,12,10), 100);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskFirst)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Task taskSecond = new Task("Работа", "Прийти на работу",
                LocalDateTime.of(2023,1,2,12,10), 50);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        List<Task> taskList = gson.fromJson(new String(response.body().getBytes(), UTF_8),
                        new TypeToken<List<Task>>() {
                        }.getType());
        assertEquals("Поехать на дачу", taskList.get(0).getName(), "Check name task");
        assertEquals("Прополоть картошку", taskList.get(0).getDescription(),
                "Check description task");
        assertEquals("2023-01-01T12:10", taskList.get(0).getStartTime().toString(),
                "Check data start task");
        assertEquals(100, taskList.get(0).getDuration(), "Check duration task");

        assertEquals("Работа", taskList.get(1).getName(), "Check name task");
        assertEquals("Прийти на работу", taskList.get(1).getDescription(),
                "Check description task");
        assertEquals("2023-01-02T12:10", taskList.get(1).getStartTime().toString(),
                "Check data start task");
        assertEquals(50, taskList.get(1).getDuration(), "Check duration task");
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task taskFirst = new Task("Поехать на дачу", "Прополоть картошку",
                LocalDateTime.of(2023,1,1,12,10), 100);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskFirst)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Task taskSecond = new Task("Работа", "Прийти на работу",
                LocalDateTime.of(2023,1,2,12,10), 50);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task/?id=1"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Task taskFirstFromHTTPTaskServer = gson.fromJson(new String(response.body().getBytes(), UTF_8), Task.class);
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task/?id=2"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Task taskSecondFromHTTPTaskServer = gson.fromJson(new String(response.body().getBytes(), UTF_8), Task.class);
        assertEquals("Поехать на дачу", taskFirstFromHTTPTaskServer.getName(), "Check name task");
        assertEquals("Прополоть картошку", taskFirstFromHTTPTaskServer.getDescription(),
                "Check description task");
        assertEquals("2023-01-01T12:10", taskFirstFromHTTPTaskServer.getStartTime().toString(),
                "Check data start task");
        assertEquals(100, taskFirstFromHTTPTaskServer.getDuration(), "Check duration task");

        assertEquals("Работа", taskSecondFromHTTPTaskServer.getName(), "Check name task");
        assertEquals("Прийти на работу", taskSecondFromHTTPTaskServer.getDescription(),
                "Check description task");
        assertEquals("2023-01-02T12:10", taskSecondFromHTTPTaskServer.getStartTime().toString(),
                "Check data start task");
        assertEquals(50, taskSecondFromHTTPTaskServer.getDuration(), "Check duration task");
    }

    @Test
    void shouldDeleteTasks() throws IOException, InterruptedException {
        Task taskFirst = new Task("Поехать на дачу", "Прополоть картошку",
                LocalDateTime.of(2023,1,1,12,10), 100);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskFirst)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Task taskSecond = new Task("Работа", "Прийти на работу",
                LocalDateTime.of(2023,1,2,12,10), 50);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(2, taskManager.getListAllTask().size(), "Check size task list");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(0, taskManager.getListAllTask().size(), "Check size task list");
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task taskFirst = new Task("Поехать на дачу", "Прополоть картошку",
                LocalDateTime.of(2023,1,1,12,10), 100);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskFirst)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Task taskSecond = new Task("Работа", "Прийти на работу",
                LocalDateTime.of(2023,1,2,12,10), 50);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Task taskThird = new Task("Дом", "Покрасить стены",
                LocalDateTime.of(2023,1,3,12,0), 150);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskThird)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(3, taskManager.getListAllTask().size(), "Check size task list");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task/?id=2"))
                .header("Accept","application/json")
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(2, taskManager.getListAllTask().size(), "Check size task list");
        assertEquals("Поехать на дачу", taskManager.getListAllTask().get(0).getName(),
                "Check first task name");
        assertEquals("Дом", taskManager.getListAllTask().get(1).getName(), "Check third task name");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task/?id=1"))
                .header("Accept","application/json")
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(1, taskManager.getListAllTask().size(), "Check size task list");
        assertEquals("Дом", taskManager.getListAllTask().get(0).getName(), "Check third task name");
    }

    @Test
    void shouldSetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals("Отдых", taskManager.getListAllEpic().get(0).getName(), "Check name epic");
        assertEquals("Заняться своим физическим и эмоцинальным здоровьем",
                taskManager.getListAllEpic().get(0).getDescription(), "Check description task");
    }

    @Test
    void shouldSetEpicWithSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals("Отдых", taskManager.getListAllEpic().get(0).getName(), "Check name epic");
        assertEquals(2, taskManager.getListAllEpic().get(0).getSubTasks().size(),
                "Check size list subtask");
        assertEquals("Заняться своим физическим и эмоцинальным здоровьем",
                taskManager.getListAllEpic().get(0).getDescription(), "Check description task");
        assertEquals("Бег", taskManager.getListAllEpic().get(0).getSubTasks().get(0).getName(),
                "Check name subtask");
        assertEquals("Медитация", taskManager.getListAllEpic().get(0).getSubTasks().get(1).getName(),
                "Check name subtask");
    }

    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        Epic epicFirst = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicFirst)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Epic epicSecond = new Epic("Спорт", "Повысить выносливость");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Epic epicFirstUpdate = new Epic("Поехать на дачу", "Полить помидоры");
        epicFirstUpdate.setId(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicFirstUpdate)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(2, taskManager.getListAllEpic().size(), "Check size task list");
        assertEquals("Спорт", taskManager.getListAllEpic().get(1).getName(), "Check name epic");
        assertEquals("Повысить выносливость", taskManager.getListAllEpic().get(1).getDescription(),
                "Check description epic");
        assertEquals("Поехать на дачу", taskManager.getListAllEpic().get(0).getName(),
                "Check name epic");
        assertEquals("Полить помидоры", taskManager.getListAllEpic().get(0).getDescription(),
                "Check description epic");
    }

    @Test
    void shouldGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        epic = new Epic("Работа", "Необходимо закрыть задачи");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

                assertEquals(200, response.statusCode(), "status code");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        List<Epic> taskList = gson.fromJson(new String(response.body().getBytes(), UTF_8),
                new TypeToken<List<Epic>>() {
                }.getType());

        assertEquals("Отдых", taskList.get(0).getName(), "Check name epic");
        assertEquals("Заняться своим физическим и эмоцинальным здоровьем", taskList.get(0).getDescription(),
                "Check description task");
        assertEquals("Медитация", taskList.get(0).getSubTasks().get(1).getName(),
                "Check name subtask in epic");
        assertEquals("Ежедневная медитация", taskList.get(0).getSubTasks().get(1).getDescription(),
                "Check description subtask in epic");
        assertEquals("Бег", taskList.get(0).getSubTasks().get(0).getName(),
                "Check name subtask in epic");
        assertEquals("Пробежать 3 км", taskList.get(0).getSubTasks().get(0).getDescription(),
                "Check description subtask in epic");
        assertEquals("Работа", taskList.get(1).getName(), "Check name task");
        assertEquals("Необходимо закрыть задачи", taskList.get(1).getDescription(),
                "Check description task");
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        epic = new Epic("Работа", "Необходимо закрыть задачи");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(2, taskManager.getListAllEpic().size(), "Check size list epic");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic/?id=1"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        Epic epicFirstFromHTTPTaskServer = gson.fromJson(new String(response.body().getBytes(), UTF_8), Epic.class);
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic/?id=4"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        Epic epicSecondFromHTTPTaskServer = gson.fromJson(new String(response.body().getBytes(), UTF_8), Epic.class);

        assertEquals("Отдых", epicFirstFromHTTPTaskServer.getName(), "Check name epic");
        assertEquals("Заняться своим физическим и эмоцинальным здоровьем",
                epicFirstFromHTTPTaskServer.getDescription(),
                "Check description epic");
        assertEquals(2, epicFirstFromHTTPTaskServer.getSubTasks().size(), "Check size list subtask");
        assertEquals("Бег", epicFirstFromHTTPTaskServer.getSubTasks().get(0).getName(),
                "Check name first subtask");
        assertEquals("Пробежать 3 км", epicFirstFromHTTPTaskServer.getSubTasks().get(0).getDescription(),
                "Check description first subtask");
        assertEquals("Медитация", epicFirstFromHTTPTaskServer.getSubTasks().get(1).getName(),
                "Check name first subtask");
        assertEquals("Ежедневная медитация", epicFirstFromHTTPTaskServer.getSubTasks().get(1).getDescription(),
                "Check description first subtask");

        assertEquals("Работа", epicSecondFromHTTPTaskServer.getName(), "Check name epic");
        assertEquals("Необходимо закрыть задачи", epicSecondFromHTTPTaskServer.getDescription(),
                "Check description epic");
        assertEquals(0, epicSecondFromHTTPTaskServer.getSubTasks().size(),
                "Check size list subtask");
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        epic = new Epic("Работа", "Необходимо закрыть задачи");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(2, taskManager.getListAllEpic().size(), "Check size list epic");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic/?id=1"))
                .header("Accept","application/json")
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(1, taskManager.getListAllEpic().size(), "Check size list epic");
        assertEquals("Работа", taskManager.getListAllEpic().get(0).getName(), "Check second epic name");
        assertEquals("Необходимо закрыть задачи", taskManager.getListAllEpic().get(0).getDescription(),
                "Check second epic description");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic/?id=4"))
                .header("Accept","application/json")
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(0, taskManager.getListAllEpic().size(), "Check size list epic");
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirstUpdate = new SubTask("Плаванье", "Проплыть 1 км");
        subTaskFirstUpdate.setId(2);
        subTaskFirstUpdate.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirstUpdate)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(2, taskManager.getListAllEpic().get(0).getSubTasks().size(),
                "Check size list subtask");
        assertEquals("Отдых", taskManager.getListAllEpic().get(0).getName(), "Check name epic");
        assertEquals("Заняться своим физическим и эмоцинальным здоровьем",
                taskManager.getListAllEpic().get(0).getDescription(), "Check description task");
        assertEquals("Плаванье", taskManager.getListAllEpic().get(0).getSubTasks().get(0).getName(),
                "Check name subtask");
        assertEquals("Проплыть 1 км", taskManager.getListAllEpic().get(0).getSubTasks().get(0).getDescription(),
                "Check description subtask");
        assertEquals("Медитация", taskManager.getListAllEpic().get(0).getSubTasks().get(1).getName(),
                "Check name subtask");
        assertEquals("Ежедневная медитация",
                taskManager.getListAllEpic().get(0).getSubTasks().get(1).getDescription(),
                "Check description subtask");
    }

    @Test
    void shouldGetSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        assertEquals(200, response.statusCode(), "status code");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        List<SubTask> subTaskList = gson.fromJson(new String(response.body().getBytes(), UTF_8),
                new TypeToken<List<SubTask>>() {
                }.getType());
        assertEquals("Медитация", subTaskList.get(1).getName(), "Check name subtask");
        assertEquals("Ежедневная медитация", subTaskList.get(1).getDescription(),
                "Check description subtask");
        assertEquals("Бег", subTaskList.get(0).getName(), "Check name subtask");
        assertEquals("Пробежать 3 км", subTaskList.get(0).getDescription(),
                "Check description subtask");
    }

    @Test
    void shouldGetSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(2, taskManager.getListAllSubTask().size(), "Check size list subtask");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask/?id=2"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFromHTTPTaskServer = gson.fromJson(new String(response.body().getBytes(), UTF_8), SubTask.class);
        assertEquals("Бег", subTaskFromHTTPTaskServer.getName(), "Check name subtask");
        assertEquals("Пробежать 3 км", subTaskFromHTTPTaskServer.getDescription(),
                "Check description subtask");
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(1, taskManager.getListAllEpic().size(), "Check size list epic");
        assertEquals(2, taskManager.getListAllSubTask().size(), "Check size list subtask");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask/?id=2"))
                .header("Accept","application/json")
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(1, taskManager.getListAllEpic().size(), "Check size list epic");
        assertEquals(1, taskManager.getListAllSubTask().size(), "Check size list subtask");

        assertEquals("Медитация", taskManager.getListAllEpic().get(0).getSubTasks().get(0).getName(),
                "Check subtask name");
        assertEquals("Ежедневная медитация",
                taskManager.getListAllEpic().get(0).getSubTasks().get(0).getDescription(),
                "Check subtask description");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask/?id=3"))
                .header("Accept","application/json")
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(1, taskManager.getListAllEpic().size(), "Check size list epic");
        assertEquals(0, taskManager.getListAllSubTask().size(), "Check size list subtask");
        assertEquals(0, taskManager.getListAllEpic().get(0).getSubTasks().size(),
                "Check size list subtask in epic");
    }

    @Test
    void shouldGetListSubTaskByIdEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        epic = new Epic("Работа", "С утра до самого вечера");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        subTaskFirst = new SubTask("Обед", "Нужно не забыть на работе пообедать");
        subTaskFirst.setEpic(4);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask/epic/?id=1"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        List<SubTask> subTaskList = gson.fromJson(new String(response.body().getBytes(), UTF_8),
                new TypeToken<List<SubTask>>() {
                }.getType());
        assertEquals(2, subTaskList.size(), "Check size list subtask");
        assertEquals("Бег", subTaskList.get(0).getName(), "Check name subtask");
        assertEquals("Пробежать 3 км", subTaskList.get(0).getDescription(),
                "Check description subtask");
        assertEquals("Медитация", subTaskList.get(1).getName(), "Check name subtask");
        assertEquals("Ежедневная медитация", subTaskList.get(1).getDescription(),
                "Check description subtask");
    }

    @Test
    void shouldGetListTaskPriority() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        epic = new Epic("Работа", "С утра до самого вечера");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        subTaskFirst = new SubTask("Обед", "Нужно не забыть на работе пообедать");
        subTaskFirst.setEpic(4);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        Task task = new Task("Поехать на дачу", "Прополоть картошку",
                LocalDateTime.of(2023,1,1,12,10), 100);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        List<Task> tasksList = gson.fromJson(new String(response.body().getBytes(), UTF_8),
                new TypeToken<List<Task>>() {
                }.getType());
        assertEquals(4, tasksList.size(), "Check size list task");
        assertEquals("Обед", tasksList.get(0).getName(), "Check name task");
        assertEquals("Нужно не забыть на работе пообедать", tasksList.get(0).getDescription(),
                "Check description task");
        assertEquals("Медитация", tasksList.get(1).getName(), "Check name task");
        assertEquals("Ежедневная медитация", tasksList.get(1).getDescription(),
                "Check description subtask");
        assertEquals("Бег", tasksList.get(2).getName(), "Check name task");
        assertEquals("Пробежать 3 км", tasksList.get(2).getDescription(),
                "Check description task");
        assertEquals("Поехать на дачу", tasksList.get(3).getName(), "Check name task");
        assertEquals("Прополоть картошку", tasksList.get(3).getDescription(),
                "Check description task");
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдых", "Заняться своим физическим и эмоцинальным здоровьем");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskFirst = new SubTask("Бег", "Пробежать 3 км");
        subTaskFirst.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        SubTask subTaskSecond = new SubTask("Медитация", "Ежедневная медитация");
        subTaskSecond.setEpic(1);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        epic = new Epic("Работа", "С утра до самого вечера");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        subTaskFirst = new SubTask("Обед", "Нужно не забыть на работе пообедать");
        subTaskFirst.setEpic(4);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        Task task = new Task("Поехать на дачу", "Прополоть картошку",
                LocalDateTime.of(2023,1,1,12,10), 100);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task/?id=6"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic/?id=1"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask/?id=2"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask/?id=3"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic/?id=4"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask/?id=5"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask/?id=5"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/history"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        List<Task> tasksListHistory = gson.fromJson(new String(response.body().getBytes(), UTF_8),
                new TypeToken<List<Task>>() {
                }.getType());
        assertEquals(6, tasksListHistory.size(), "Check size list task");
        assertEquals("Поехать на дачу", tasksListHistory.get(0).getName(), "Check name task");
        assertEquals("Прополоть картошку", tasksListHistory.get(0).getDescription(),
                "Check description task");
        assertEquals("Отдых", tasksListHistory.get(1).getName(), "Check name task");
        assertEquals("Заняться своим физическим и эмоцинальным здоровьем",
                tasksListHistory.get(1).getDescription(), "Check description subtask");
        assertEquals("Бег", tasksListHistory.get(2).getName(), "Check name task");
        assertEquals("Пробежать 3 км", tasksListHistory.get(2).getDescription(),
                "Check description task");
        assertEquals("Медитация", tasksListHistory.get(3).getName(), "Check name task");
        assertEquals("Ежедневная медитация", tasksListHistory.get(3).getDescription(),
                "Check description task");
        assertEquals("Работа", tasksListHistory.get(4).getName(), "Check name task");
        assertEquals("С утра до самого вечера", tasksListHistory.get(4).getDescription(),
                "Check description task");
        assertEquals("Обед", tasksListHistory.get(5).getName(), "Check name task");
        assertEquals("Нужно не забыть на работе пообедать", tasksListHistory.get(5).getDescription(),
                "Check description task");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task/?id=6"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/history"))
                .header("Accept","application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "status code");
        tasksListHistory = gson.fromJson(new String(response.body().getBytes(), UTF_8),
                new TypeToken<List<Task>>() {
                }.getType());
        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(6, tasksListHistory.size(), "Check size list task");
        assertEquals("Отдых", tasksListHistory.get(0).getName(), "Check name task");
        assertEquals("Заняться своим физическим и эмоцинальным здоровьем",
                tasksListHistory.get(0).getDescription(), "Check description task");
        assertEquals("Бег", tasksListHistory.get(1).getName(), "Check name task");
        assertEquals("Пробежать 3 км", tasksListHistory.get(1).getDescription(),
                "Check description subtask");
        assertEquals("Медитация", tasksListHistory.get(2).getName(), "Check name task");
        assertEquals("Ежедневная медитация", tasksListHistory.get(2).getDescription(),
                "Check description task");
        assertEquals("Работа", tasksListHistory.get(3).getName(), "Check name task");
        assertEquals("С утра до самого вечера", tasksListHistory.get(3).getDescription(),
                "Check description task");
    }

    @AfterEach
    void tearDown() {
        taskManager.deleteAllTasks();
        taskServer.stop();
        kvServer.stop();
    }

}
