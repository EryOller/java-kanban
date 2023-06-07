import model.Epic;
import model.Task;
import org.junit.jupiter.api.*;
import service.FileBackedTaskManager;
import service.Managers;

import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagersTest{

    static FileBackedTaskManager fileBackedTaskManager;
    static int count = 1;

    @BeforeEach
    public void createTaskManagerWithFile() {
        taskManager = Managers.getFileBackedTaskManager("./test/resources/" + count + "task.csv");
        fileBackedTaskManager = (FileBackedTaskManager) taskManager;
    }

    @Test
    public void checkSaveInFileEmptyListTasksAndEmptyListHistory() {
        taskManager = Managers.getFileBackedTaskManager("./test/resources/" + count + "task.csv");
        fileBackedTaskManager = (FileBackedTaskManager) taskManager;
        Assertions.assertTrue(fileBackedTaskManager.getListAllTask().isEmpty());
        Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.load());
    }

    @Test
    public void checkRecoveryFromEmptyFileAndEmptyListHistory() {
        taskManager = Managers.getFileBackedTaskManager("./test/resources/" + count + "task.csv");
        fileBackedTaskManager = (FileBackedTaskManager) taskManager;
        Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.save());
    }

    @Test
    public void checkSaveEpicWithoutSubTaskInFile() {
        taskManager = Managers.getFileBackedTaskManager("./test/resources/" + count + "task.csv");
        fileBackedTaskManager = (FileBackedTaskManager) taskManager;
        fileBackedTaskManager.createTask(new Epic("Test-Name", "Test-Description"));
        Assertions.assertFalse(fileBackedTaskManager.getListAllTask().isEmpty());
        Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.load());
    }

    @Test
    public void checkRecoveryEpicWithoutSubTaskFromFile() {
        fileBackedTaskManager.createTask(new Epic("Test-Name", "Test-Description"));
        fileBackedTaskManager.load();
        Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.save());
    }

    @Test
    public void checkSaveListHistoryInFile() {
        int numberEpic = fileBackedTaskManager.createEpic(new Epic("Test-Name", "Test-Description")).getId();
        int numberTask = fileBackedTaskManager.createTask(new Task("TestTask-Name", "TestTask-Description")).getId();
        fileBackedTaskManager.load();
        Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.save());
        fileBackedTaskManager.getEpicById(numberEpic);
        fileBackedTaskManager.getTaskById(numberTask);
    }

    @AfterEach
    public void deleteFile() {
        fileBackedTaskManager.deleteAllTasks();
        new File("./test/resources/" + count++ + "task.csv").delete();
        fileBackedTaskManager = null;
        taskManager = null;

    }
}
