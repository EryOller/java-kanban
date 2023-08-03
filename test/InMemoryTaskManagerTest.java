import org.junit.jupiter.api.BeforeAll;
import service.Managers;
import service.exception.ManagerSaveException;

public class InMemoryTaskManagerTest extends TaskManagersTest {

    @BeforeAll
    public static void createTaskManager() throws ManagerSaveException {
        taskManager = Managers.getDefault();
    }
}
