import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class HistoryManagerTest {
    private static InMemoryHistoryManager historyManager;
    private static TaskManager taskManager;

    @BeforeAll
    public static void createHistoryManager() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
    }

    /**
     * Тесты с пустой историей задач
     */

    @Test
    public void checkRemoveTaskFromEpmtyHistory() {
        Assertions.assertDoesNotThrow(() -> historyManager.remove(1));
    }

    @Test
    public void checkGetHistoryFromEmpryHistory() {
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void checkAddHistoryInEmptyHistory() {
        int numberTask = taskManager.createTask(new Task("Новая задача1", "Описание новой задачи",
                LocalDateTime.of(2023, 1,1,12,0), 30)).getId();
        taskManager.getTaskById(numberTask);
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    /**
     * Дублирование
     */

    @Test
    public void checkDoubleAddTaskInHistory() {
        int numberTask = taskManager.createTask(new Task("Новая задача2", "Описание новой задачи",
                LocalDateTime.of(2023, 1,1,12,0), 30)).getId();
        taskManager.getTaskById(numberTask);
        taskManager.getTaskById(numberTask);
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    /**
     * Удаление из начала, серидины и конца истории
     */

    @Test
    public void checkDeleteStartElementFromHistory() {
        int taskOne = taskManager.createTask(new Task("Новая задача1", "Описание новой задачи",
                LocalDateTime.of(2023, 1,1,12,0), 30)).getId();
        int taskTwo = taskManager.createTask(new Task("Новая задача2", "Описание новой задачи",
                LocalDateTime.of(2023, 2,1,12,0), 30)).getId();
        int taskThree = taskManager.createTask(new Task("Новая задача3", "Описание новой задачи",
                LocalDateTime.of(2023, 3,1,12,0), 30)).getId();
        int taskFour = taskManager.createTask(new Task("Новая задача4", "Описание новой задачи",
                LocalDateTime.of(2023, 4,1,12,0), 30)).getId();
        ArrayList<Task> checkHistory = new ArrayList<>();
        taskManager.getTaskById(taskOne);
        checkHistory.add(taskManager.getTaskById(taskTwo));
        checkHistory.add(taskManager.getTaskById(taskThree));
        checkHistory.add(taskManager.getTaskById(taskFour));
        historyManager.remove(taskOne);
        Assertions.assertArrayEquals(checkHistory.toArray(), historyManager.getHistory().toArray());
    }

    @Test
    public void checkDeleteEndElementFromHistory() {
        int taskOne = taskManager.createTask(new Task("Новая задача1", "Описание новой задачи",
                LocalDateTime.of(2023, 1,1,12,0), 30)).getId();
        int taskTwo = taskManager.createTask(new Task("Новая задача2", "Описание новой задачи",
                LocalDateTime.of(2023, 2,1,12,0), 30)).getId();
        int taskThree = taskManager.createTask(new Task("Новая задача3", "Описание новой задачи",
                LocalDateTime.of(2023, 3,1,12,0), 30)).getId();
        int taskFour = taskManager.createTask(new Task("Новая задача4", "Описание новой задачи",
                LocalDateTime.of(2023, 4,1,12,0), 30)).getId();
        ArrayList<Task> checkHistory = new ArrayList<>();
        checkHistory.add(taskManager.getTaskById(taskOne));
        checkHistory.add(taskManager.getTaskById(taskTwo));
        checkHistory.add(taskManager.getTaskById(taskThree));
        taskManager.getTaskById(taskFour);
        historyManager.remove(taskFour);
        Assertions.assertArrayEquals(checkHistory.toArray(), historyManager.getHistory().toArray());
    }

    @Test
    public void checkDeleteMiddleElementFromHistory() {
        int taskOne = taskManager.createTask(new Task("Новая задача1", "Описание новой задачи",
                LocalDateTime.of(2023, 1,1,12,0), 30)).getId();
        int taskTwo = taskManager.createTask(new Task("Новая задача2", "Описание новой задачи",
                LocalDateTime.of(2023, 2,1,12,0), 30)).getId();
        int taskThree = taskManager.createTask(new Task("Новая задача3", "Описание новой задачи",
                LocalDateTime.of(2023, 3,1,12,0), 30)).getId();
        int taskFour = taskManager.createTask(new Task("Новая задача4", "Описание новой задачи",
                LocalDateTime.of(2023, 4,1,12,0), 30)).getId();
        ArrayList<Task> checkHistory = new ArrayList<>();
        checkHistory.add(taskManager.getTaskById(taskOne));
        checkHistory.add(taskManager.getTaskById(taskTwo));
        taskManager.getTaskById(taskThree);
        checkHistory.add(taskManager.getTaskById(taskFour));
        historyManager.remove(taskThree);
        Assertions.assertArrayEquals(checkHistory.toArray(), historyManager.getHistory().toArray());
    }

    @AfterEach
    public void deleteAllTaskAndClearHistory() {
        taskManager.deleteAllTasks();
        historyManager.clearHistory();
    }
}
