import com.google.gson.Gson;
import controller.HttpTaskServer;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVClient;
import server.KVServer;
import service.*;
import service.exception.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверка сохранения и выгрузки данных на сервер и в сервер
 */

public class HttpTaskManagerTest {

    private HttpTaskServer taskServer;
    private KVServer kvServer;
    private KVClient kvClient;
    private Gson gson = Managers.getGson();
    private TaskManager taskManager;

    private static final String JSON_TASKS = "{\"1\":{\"name\":\"Купить еды\",\"description\":\"молоко и хлеб\"," +
            "\"id\":1,\"status\":\"NEW\",\"type\":\"TASK\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1," +
            "\"day\":1},\"time\":{\"hour\":12,\"minute\":10,\"second\":0,\"nano\":0}},\"duration\":100}," +
            "\"2\":{\"name\":\"Купить одежду\",\"description\":\" майка и джинсы\",\"id\":2,\"status\":\"NEW\"," +
            "\"type\":\"TASK\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":2}," +
            "\"time\":{\"hour\":17,\"minute\":10,\"second\":0,\"nano\":0}},\"duration\":50}}";
    private static final String JSON_EPICS = "{\"3\":{\"insideSubTasks\":[{\"epicNumber\":3," +
            "\"name\":\"Новый сервис\",\"description\":\"Встреча с Алексеем\",\"id\":4,\"status\":\"NEW\"," +
            "\"type\":\"SUBTASK\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":3}," +
            "\"time\":{\"hour\":12,\"minute\":10,\"second\":0,\"nano\":0}},\"duration\":100},{\"epicNumber\":3," +
            "\"name\":\"Тестирование\",\"description\":\"Встреча с Иваном\",\"id\":5,\"status\":\"NEW\"," +
            "\"type\":\"SUBTASK\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":3}," +
            "\"time\":{\"hour\":12,\"minute\":10,\"second\":0,\"nano\":0}},\"duration\":100}],\"name\":\"Работа\"," +
            "\"description\":\"Провести встречи\",\"id\":3,\"status\":\"NEW\",\"type\":\"EPIC\"," +
            "\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":3},\"time\":{\"hour\":12,\"minute\":10," +
            "\"second\":0,\"nano\":0}},\"duration\":200}}";

    private static final String JSON_SUBTASK = "{\"4\":{\"epicNumber\":3,\"name\":\"Новый сервис\"," +
            "\"description\":\"Встреча с Алексеем\",\"id\":4,\"status\":\"NEW\",\"type\":\"SUBTASK\"," +
            "\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":3},\"time\":{\"hour\":12,\"minute\":10," +
            "\"second\":0,\"nano\":0}},\"duration\":100},\"5\":{\"epicNumber\":3,\"name\":\"Тестирование\"," +
            "\"description\":\"Встреча с Иваном\",\"id\":5,\"status\":\"NEW\",\"type\":\"SUBTASK\"," +
            "\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":3},\"time\":{\"hour\":12,\"minute\":10," +
            "\"second\":0,\"nano\":0}},\"duration\":100}}";
    private static final String JSON_HISTORY = "[1,2,3,4,5]";

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
    void shouldSetTaskListInServer() throws IOException, InterruptedException {

        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertEquals(0, taskManager.getListAllTask().size(), "Check list tasks");
        assertEquals(0, taskManager.getListAllEpic().size(), "Check list epics");
        assertEquals(0, taskManager.getListAllSubTask().size(), "Check list subtasks");
        assertEquals(0, ((HttpTaskManager) taskManager).getHistory().size() ,  "check list history");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new Task("test_Name", "Test_Descriptiom"))))  //HttpRequest.BodyPublishers.noBody()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode(), new Exception());
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/task"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new Task("test_Name2", "Test_Descriptiom2"))))  //HttpRequest.BodyPublishers.noBody()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode(), new Exception());
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/epic"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new Epic("test_Name3", "Test_Descriptiom3"))))  //HttpRequest.BodyPublishers.noBody()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode(), new Exception());
        }

        SubTask subTaskFirst = new SubTask("test_NameSub1", "test_description1");
        subTaskFirst.setEpic(3);
        SubTask subTaskSecond = new SubTask("test_NameSub2", "test_description2");
        subTaskSecond.setEpic(3);
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskFirst)))  //HttpRequest.BodyPublishers.noBody()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode(), new Exception());
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/tasks/subtask"))
                .header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskSecond)))  //HttpRequest.BodyPublishers.noBody()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode(), new Exception());
        }

        ((HttpTaskManager) taskManager).load();
        assertEquals(2, taskManager.getListAllTask().size(), "Check list tasks");
        assertEquals(1, taskManager.getListAllEpic().size(), "Check list epics");
        assertEquals(2, taskManager.getListAllSubTask().size(), "Check list subtasks");
        assertEquals(0, ((HttpTaskManager) taskManager).getHistory().size() ,  "check list history");
        taskManager.deleteAllTasks();
    }

    @Test
    void shouldReturnTaskListFromServer() throws ManagerSaveException {

        //Добавляем задачи на сервер
        kvClient.save("tasks", JSON_TASKS);
        kvClient.save("epics", JSON_EPICS);
        kvClient.save("subtasks", JSON_SUBTASK);
        kvClient.save("history", JSON_HISTORY);

        //Создаем задачи для сравнения с фактическим результатом
        List<Task> historyCheck = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        List<Epic> epics = new ArrayList<>();
        List<SubTask> subTasks = new ArrayList<>();

        tasks.add(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,1,12,10), 100));
        tasks.add(new Task("Купить одежду", " майка и джинсы",
                LocalDateTime.of(2023,1,2,17,10), 50));
        tasks.get(0).setId(1);
        tasks.get(1).setId(2);
        historyCheck.add(tasks.get(0));
        historyCheck.add(tasks.get(1));

        epics.add(new Epic("Работа", "Провести встречи"));
        epics.get(0).setId(3);
        historyCheck.add(epics.get(0));

        subTasks.add(new SubTask("Новый сервис", "Встреча с Алексеем",
                LocalDateTime.of(2023,1,3,12,10), 100));
        subTasks.add(new SubTask("Тестирование", "Встреча с Иваном",
                LocalDateTime.of(2023,1,3,12,10), 100));
        subTasks.get(0).setEpic(epics.get(0).getId());
        subTasks.get(0).setId(4);
        subTasks.get(1).setEpic(epics.get(0).getId());
        subTasks.get(1).setId(5);
        historyCheck.add(subTasks.get(0));
        historyCheck.add(subTasks.get(1));

        ((HttpTaskManager)taskManager).save(((HttpTaskManager)taskManager).load());
        assertArrayEquals(tasks.toArray(), taskManager.getListAllTask().toArray(), "Check list tasks");
        assertArrayEquals(epics.toArray(), taskManager.getListAllEpic().toArray(), "Check list epics");
        assertArrayEquals(subTasks.toArray(), taskManager.getListAllSubTask().toArray(), "Check list subtasks");
        assertArrayEquals(historyCheck.toArray(), ((HttpTaskManager) taskManager).getHistory().toArray() ,  "check list history");
        taskManager.deleteAllTasks();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        kvServer.stop();
    }
}
