import org.junit.jupiter.api.BeforeAll;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

public class InMemoryTaskManagerTest extends TaskManagersTest {

    @BeforeAll
    public static void createTaskManager() {
        taskManager = Managers.getDefault();
    }

}
