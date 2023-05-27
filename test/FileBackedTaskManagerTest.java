import model.Epic;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.Managers;

public class FileBackedTaskManagerTest extends TaskManagersTest{

    static FileBackedTaskManager fileBackedTaskManager;

    @BeforeAll
    public static void createTaskManager() {
        taskManager = Managers.getFileBackedTaskManager("./resources/task.csv");
        fileBackedTaskManager = (FileBackedTaskManager) taskManager;
    }

    @Test
    public void checkSaveInFileEmptyListTasksAndEmptyListHistory() { // dima проблема в том что не пробрасываются исклдючения и тест всегда выполняется
        try {
            Assertions.assertEquals(0, fileBackedTaskManager.getListAllTask().size());
            Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.load());
        } finally {
            fileBackedTaskManager.deleteAllTasks();
        }
    }

    @Test
    public void checkRecoveryFromEmptyFileAndEmptyListHistory() { // dima проблема в том что не пробрасываются исклдючения и тест всегда выполняется
        try {
            Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.save());
        } finally {
            fileBackedTaskManager.deleteAllTasks();
        }

    }

    @Test
    public void checkSaveEpicWithoutSubTaskInFile() {
        try {
            fileBackedTaskManager.createTask(new Epic("Test-Name", "Test-Description"));
            Assertions.assertEquals(1, fileBackedTaskManager.getListAllTask().size());
            Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.load());
        } finally {
            fileBackedTaskManager.deleteAllTasks();
        }
    }

    @Test
    public void checkRecoveryEpicWithoutSubTaskFromFile() {
        try {
            fileBackedTaskManager.createTask(new Epic("Test-Name", "Test-Description"));
            fileBackedTaskManager.load();
            Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.save());
        } finally {
            fileBackedTaskManager.deleteAllTasks();
        }
    }

    @Test
    public void checkSaveListHistoryInFile() {
        try {
            int numberEpic = fileBackedTaskManager.createEpic(new Epic("Test-Name", "Test-Description")).getId();
            int numberTask = fileBackedTaskManager.createTask(new Task("TestTask-Name", "TestTask-Description")).getId();
            fileBackedTaskManager.load();
            Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.save());
            fileBackedTaskManager.getEpicById(numberEpic);
            fileBackedTaskManager.getTaskById(numberTask);
        } finally {
            fileBackedTaskManager.deleteAllTasks();
        }
    }
}
