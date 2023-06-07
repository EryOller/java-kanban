import org.junit.jupiter.api.BeforeAll;
import service.Managers;

public class InMemoryTaskManagerTest extends TaskManagersTest {

    @BeforeAll
    public static void createTaskManager() {
        taskManager = Managers.getDefault();
    }
}
