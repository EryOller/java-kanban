import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.*;
import service.Managers;
import service.TaskManager;
import service.exception.ManagerSaveException;

import java.time.LocalDateTime;

public class EpicTest {
    public static TaskManager inMemoryTaskManager;

    @BeforeAll
    public static void createInMemoryTaskManager() throws ManagerSaveException {
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    public void shouldReturnEmptyListSubtaskAboutNewEpic() {
        int numberEpic = inMemoryTaskManager.createEpic(new Epic("Закончить Яндекс-Практикум",
                "Дойти до конца! Путь самурая!")).getId();
        int numberOfSubtasks = inMemoryTaskManager.getEpicById(numberEpic).getSubTasks().size();
        Assertions.assertEquals(0, numberOfSubtasks);
    }

    @Test
    public void shouldReturnAllSubTasksWithStatusNew() {
        int numberEpic = inMemoryTaskManager.createEpic(new Epic("Закончить Яндекс-Практикум",
                "Дойти до конца! Путь самурая!")).getId();
        int numberSubtaskFirst = inMemoryTaskManager.createSubTask(new SubTask("Первая подзадача",
                "Описание первой подзадачи", LocalDateTime.of(2023,1,1,1,10),
                100), inMemoryTaskManager.getEpicById(numberEpic)).getId();
        int numberSubtaskSecond = inMemoryTaskManager.createSubTask(new SubTask("Вторая подзадача",
                "Описание второй подзадачи", LocalDateTime.of(2023,1,1,4, 20),
                50), inMemoryTaskManager.getEpicById(numberEpic)).getId();
        Assertions.assertEquals(Status.NEW, inMemoryTaskManager.getSubTaskById(numberSubtaskFirst).getStatus());
        Assertions.assertEquals(Status.NEW, inMemoryTaskManager.getSubTaskById(numberSubtaskSecond).getStatus());
    }

    @Test
    public void shouldReturnAllSubTasksWithStatusDone() {
        int numberEpic = inMemoryTaskManager.createEpic(new Epic("Закончить Яндекс-Практикум",
                "Дойти до конца! Путь самурая!")).getId();
        int numberSubtaskFirst = inMemoryTaskManager.createSubTask(new SubTask("Первая подзадача",
                "Описание первой подзадачи", LocalDateTime.of(2023, 1, 1,
                8, 25), 40), inMemoryTaskManager.getEpicById(numberEpic)).getId();
        inMemoryTaskManager.getSubTaskById(numberSubtaskFirst).setStatus(Status.DONE);
        int numberSubtaskSecond = inMemoryTaskManager.createSubTask(new SubTask("Вторая подзадача",
                "Описание второй подзадачи", LocalDateTime.of(2023,1,1,13,0),
                10), inMemoryTaskManager.getEpicById(numberEpic)).getId();
        inMemoryTaskManager.getSubTaskById(numberSubtaskSecond).setStatus(Status.DONE);
        Assertions.assertEquals(Status.DONE, inMemoryTaskManager.getSubTaskById(numberSubtaskFirst).getStatus());
        Assertions.assertEquals(Status.DONE, inMemoryTaskManager.getSubTaskById(numberSubtaskSecond).getStatus());
    }

    @Test
    public void shouldReturnAllSubTasksWithStatusDoneAndNew() {
        int numberEpic = inMemoryTaskManager.createEpic(new Epic("Закончить Яндекс-Практикум",
                "Дойти до конца! Путь самурая!")).getId();
        int numberSubtaskFirst = inMemoryTaskManager.createSubTask(new SubTask("Первая подзадача",
                "Описание первой подзадачи", LocalDateTime.of(2023, 2,1,1,50),
                100), inMemoryTaskManager.getEpicById(numberEpic)).getId();
        inMemoryTaskManager.getSubTaskById(numberSubtaskFirst).setStatus(Status.NEW);
        int numberSubtaskSecond = inMemoryTaskManager.createSubTask(new SubTask("Вторая подзадача",
                "Описание второй подзадачи", LocalDateTime.of(2023,2,1, 10, 20),
                50), inMemoryTaskManager.getEpicById(numberEpic)).getId();
        inMemoryTaskManager.getSubTaskById(numberSubtaskSecond).setStatus(Status.DONE);
        Assertions.assertEquals(Status.NEW, inMemoryTaskManager.getSubTaskById(numberSubtaskFirst).getStatus());
        Assertions.assertEquals(Status.DONE, inMemoryTaskManager.getSubTaskById(numberSubtaskSecond).getStatus());
    }

    @Test
    public void shouldReturnAllSubTasksWithStatusInProgress() {
        int numberEpic = inMemoryTaskManager.createEpic(new Epic("Закончить Яндекс-Практикум",
                "Дойти до конца! Путь самурая!")).getId();
        int numberSubtaskFirst = inMemoryTaskManager.createSubTask(new SubTask("Первая подзадача",
                "Описание первой подзадачи", LocalDateTime.of(2023,3,1,8,5),
                30), inMemoryTaskManager.getEpicById(numberEpic)).getId();
        inMemoryTaskManager.getSubTaskById(numberSubtaskFirst).setStatus(Status.IN_PROGRESS);
        int numberSubtaskSecond = inMemoryTaskManager.createSubTask(new SubTask("Вторая подзадача",
                "Описание второй подзадачи", LocalDateTime.of(2023, 4, 1, 10,15),
                90), inMemoryTaskManager.getEpicById(numberEpic)).getId();
        inMemoryTaskManager.getSubTaskById(numberSubtaskSecond).setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getSubTaskById(numberSubtaskFirst).getStatus());
        Assertions.assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getSubTaskById(numberSubtaskSecond).getStatus());
    }

    @AfterEach
    public void deleteAllTask() {
        inMemoryTaskManager.deleteAllTasks();
    }

    @AfterAll
    public static void deleteManager() {
        inMemoryTaskManager = null;
    }
}
