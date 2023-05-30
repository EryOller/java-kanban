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
        try {
            Assertions.assertEquals(0, fileBackedTaskManager.getListAllTask().size());
            Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.load());
        } finally {
            fileBackedTaskManager.deleteAllTasks();
        }
    }

    @Test
    public void checkRecoveryFromEmptyFileAndEmptyListHistory() {
        taskManager = Managers.getFileBackedTaskManager("./test/resources/" + count + "task.csv");
        fileBackedTaskManager = (FileBackedTaskManager) taskManager;
        try {
            Assertions.assertDoesNotThrow(() -> fileBackedTaskManager.save());
        } finally {
            fileBackedTaskManager.deleteAllTasks();
        }
    }

    @Test
    public void checkSaveEpicWithoutSubTaskInFile() {
        taskManager = Managers.getFileBackedTaskManager("./test/resources/" + count + "task.csv");
        fileBackedTaskManager = (FileBackedTaskManager) taskManager;
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

    @AfterEach
    public void deleteFile() {
        new File("./test/resources/" + count++ + "task.csv").delete();
        fileBackedTaskManager = null;
        taskManager = null;

    }
}
