import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.LocalDateTime;

public abstract class TaskManagersTest {
    public static TaskManager taskManager;

    /**
     * Тесты со стандартным поведением
     */

    @Test
    public void checkCreateTasksStandardBehavior() {
        int numberFirstTask = taskManager.createTask(new Task("Купить еды", "молоко и хлеб")).getId();
        int numberSecondTask = taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,4,11,10), 100)).getId();

        Assertions.assertEquals("Купить еды", taskManager.getTaskById(numberFirstTask).getName());
        Assertions.assertEquals("Купить одежду", taskManager.getTaskById(numberSecondTask).getName());
    }

    @Test
    public void checkUpdateTasksStandardBehavior() {
        int numberFirstTask = taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,1,12,10), 100)).getId();
        int numberSecondTask = taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,2,11,10), 100)).getId();

         taskManager.updateTask(numberFirstTask, new Task("Купить еды", "молоко, хлеб, колбасу",
                LocalDateTime.of(2023,1,1,12,0), 30));
        taskManager.updateTask(numberSecondTask, new Task("Купить одежду", "Купить в глории БЕЛЫЕ джинсы",
                LocalDateTime.of(2023,1,2,15,10), 60));

        Assertions.assertEquals("молоко, хлеб, колбасу", taskManager.getTaskById(numberFirstTask).getDescription());
        Assertions.assertEquals("2023-01-01T12:00", taskManager.getTaskById(numberFirstTask).getStartTime().toString());
        Assertions.assertEquals("2023-01-01T12:30", taskManager.getTaskById(numberFirstTask).getEndTime().toString());

        Assertions.assertEquals("Купить в глории БЕЛЫЕ джинсы", taskManager.getTaskById(numberSecondTask).getDescription());
        Assertions.assertEquals("2023-01-02T15:10", taskManager.getTaskById(numberSecondTask).getStartTime().toString());
        Assertions.assertEquals("2023-01-02T16:10", taskManager.getTaskById(numberSecondTask).getEndTime().toString());
    }

    @Test
    public void checkDeleteTasksStandardBehavior() {
        int numberFirstTask = taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,5,12,10), 100)).getId();
        int numberSecondTask = taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,6,11,10), 100)).getId();
        taskManager.deleteTaskById(numberFirstTask);
        taskManager.deleteTaskById(numberSecondTask);

        Assertions.assertEquals(null, taskManager.getTaskById(numberFirstTask));
        Assertions.assertEquals(null, taskManager.getTaskById(numberSecondTask));
    }

    @Test
    public void checkDeleteAllTasksStandardBehavior() {
        taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,7,12,10), 100));
        taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,8,11,10), 100));
        taskManager.deleteAllTasks();

        Assertions.assertEquals(0, taskManager.getListAllTask().size());
    }

    @Test
    public void checkGetListAllTasksStandardBehavior() {
        taskManager.deleteAllTasks();
        taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,7,12,10), 100));
        taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,8,11,10), 100));

        Assertions.assertEquals(2, taskManager.getListAllTask().size());
    }

    @Test
    public void checkCreateEpicsStandardBehavior() {
        int numberFirstTask = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        int numberSecondTask = taskManager.createEpic(new Epic("Учеба", "Получение сертификата")).getId();//taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",

        Assertions.assertEquals("Работа", taskManager.getEpicById(numberFirstTask).getName());
        Assertions.assertEquals("Учеба", taskManager.getEpicById(numberSecondTask).getName());
    }

    @Test
    public void checkUpdateEpicStandardBehavior() {
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        taskManager.updateEpic(numberEpic, new Epic("Внеурочная работа", "Написание тестов по проекту №1"));

        Assertions.assertEquals("Внеурочная работа", taskManager.getEpicById(numberEpic).getName());
    }

    @Test
    public void checkGetAllEpicsStandardBehavior() {
        taskManager.deleteAllTasks();
        taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        taskManager.createEpic(new Epic("Учеба", "Получение сертификата")).getId();//taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",

        Assertions.assertEquals(2, taskManager.getListAllEpic().size());
    }

    @Test
    public void checkStatusCalculationStandardBehavior() {
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        int numberSubTaskFirst = taskManager.createSubTask(new SubTask("Провести анализ задачи",
                "Определить входные и выходные данные"/*, LocalDateTime.of(2023,3,3,15,0), 50*/),
                taskManager.getEpicById(numberEpic)).getId();
        int numberSubTaskSecond = taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск", LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic)).getId();
        Assertions.assertEquals(Status.NEW.name(), taskManager.getEpicById(numberEpic).getStatus().name());
        Assertions.assertEquals("Работа", taskManager.getSubTaskById(numberSubTaskFirst).getEpic().getName());
        Assertions.assertEquals("Работа", taskManager.getSubTaskById(numberSubTaskSecond).getEpic().getName());

        taskManager.getSubTaskById(numberSubTaskFirst).setStatus(Status.DONE);
        taskManager.updateSubTask(numberSubTaskFirst, taskManager.getSubTaskById(numberSubTaskFirst));
        Assertions.assertEquals(Status.IN_PROGRESS.name(), taskManager.getEpicById(numberEpic).getStatus().name());

        taskManager.getSubTaskById(numberSubTaskSecond).setStatus(Status.DONE);
        taskManager.updateSubTask(numberSubTaskSecond, taskManager.getSubTaskById(numberSubTaskSecond));
        Assertions.assertEquals(Status.DONE.name(), taskManager.getEpicById(numberEpic).getStatus().name());
    }

    @Test
    public void checkForEpicStandardBehavior() {
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        int numberSubTaskFirst = taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic)).getId();
        int numberSubTaskSecond = taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск", LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic)).getId();
        Assertions.assertEquals("Работа", taskManager.getSubTaskById(numberSubTaskFirst).getEpic().getName());
        Assertions.assertEquals("Работа", taskManager.getSubTaskById(numberSubTaskSecond).getEpic().getName());
    }

    @Test
    public void checkDeleteSubTasksStandardBehavior() {
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        int numberSubTaskFirst = taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic)).getId();
        int numberSubTaskSecond = taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск", LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic)).getId();
        taskManager.deleteSubTaskById(numberSubTaskFirst);
        taskManager.deleteSubTaskById(numberSubTaskSecond);

        Assertions.assertEquals(null, taskManager.getSubTaskById(numberSubTaskFirst));
        Assertions.assertEquals(null, taskManager.getSubTaskById(numberSubTaskSecond));
    }

    @Test
    public void checkDeleteEpicsStandardBehavior() {
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic)).getId();
        taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск", LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic)).getId();
        taskManager.deleteEpicById(numberEpic);

        Assertions.assertEquals(null, taskManager.getEpicById(numberEpic));
    }

    @Test
    public void checkGetAllSubTaskStandardBehavior() {
        taskManager.deleteAllTasks();
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic)).getId();
        taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск", LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic)).getId();
        Assertions.assertEquals(2, taskManager.getListAllSubTask().size());
    }


    /**
     * Тесты с пустым списком задач
     */

   @Test
   public void checkCreateTasksEmptyListTask() {
       taskManager.deleteAllTasks();
       int numberFirstTask = taskManager.createTask(new Task("Купить еды", "молоко и хлеб")).getId();
       int numberSecondTask = taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
               LocalDateTime.of(2023,1,4,11,10), 100)).getId();

       Assertions.assertEquals("Купить еды", taskManager.getTaskById(numberFirstTask).getName());
       Assertions.assertEquals("Купить одежду", taskManager.getTaskById(numberSecondTask).getName());
   }

    @Test
    public void checkUpdateTasksEmptyListTask() {
        taskManager.deleteAllTasks();
        int numberFirstTask = taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,1,12,10), 100)).getId();
        int numberSecondTask = taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,2,11,10), 100)).getId();

        taskManager.updateTask(numberFirstTask, new Task("Купить еды", "молоко, хлеб, колбасу",
                LocalDateTime.of(2023,1,1,12,0), 30));
        taskManager.updateTask(numberSecondTask, new Task("Купить одежду", "Купить в глории БЕЛЫЕ джинсы",
                LocalDateTime.of(2023,1,2,15,10), 60));

        Assertions.assertEquals("молоко, хлеб, колбасу", taskManager.getTaskById(numberFirstTask).getDescription());
        Assertions.assertEquals("2023-01-01T12:00", taskManager.getTaskById(numberFirstTask).getStartTime().toString());
        Assertions.assertEquals("2023-01-01T12:30", taskManager.getTaskById(numberFirstTask).getEndTime().toString());

        Assertions.assertEquals("Купить в глории БЕЛЫЕ джинсы", taskManager.getTaskById(numberSecondTask).getDescription());
        Assertions.assertEquals("2023-01-02T15:10", taskManager.getTaskById(numberSecondTask).getStartTime().toString());
        Assertions.assertEquals("2023-01-02T16:10", taskManager.getTaskById(numberSecondTask).getEndTime().toString());
    }

    @Test
    public void checkDeleteTasksEpmtyListTask() {
        taskManager.deleteAllTasks();
        int numberFirstTask = taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,5,12,10), 100)).getId();
        int numberSecondTask = taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,6,11,10), 100)).getId();
        taskManager.deleteTaskById(numberFirstTask);
        taskManager.deleteTaskById(numberSecondTask);

        Assertions.assertEquals(null, taskManager.getTaskById(numberFirstTask));
        Assertions.assertEquals(null, taskManager.getTaskById(numberSecondTask));
    }

    @Test
    public void checkDeleteAllTasksEmptyListTask() {
        taskManager.deleteAllTasks();
        taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,7,12,10), 100));
        taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,8,11,10), 100));
        taskManager.deleteAllTasks();

        Assertions.assertEquals(0, taskManager.getListAllTask().size());
    }

    @Test
    public void checkGetListAllTasksEmptyListTask() {
        taskManager.deleteAllTasks();
        taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,7,12,10), 100));
        taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,8,11,10), 100));

        Assertions.assertEquals(2, taskManager.getListAllTask().size());
    }

    @Test
    public void checkCreateEpicsEmptyListTask() {
        taskManager.deleteAllTasks();
        int numberFirstTask = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        int numberSecondTask = taskManager.createEpic(new Epic("Учеба", "Получение сертификата")).getId();//taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",

        Assertions.assertEquals("Работа", taskManager.getEpicById(numberFirstTask).getName());
        Assertions.assertEquals("Учеба", taskManager.getEpicById(numberSecondTask).getName());
    }

    @Test
    public void checkUpdateEpicEmptyListTask() {
        taskManager.deleteAllTasks();
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        taskManager.updateEpic(numberEpic, new Epic("Внеурочная работа", "Написание тестов по проекту №1"));

        Assertions.assertEquals("Внеурочная работа", taskManager.getEpicById(numberEpic).getName());
    }

    @Test
    public void checkStatusCalculationEmptyListTask() {
        taskManager.deleteAllTasks();
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        int numberSubTaskFirst = taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic)).getId();
        int numberSubTaskSecond = taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск",
                        LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic)).getId();
        Assertions.assertEquals(Status.NEW.name(), taskManager.getEpicById(numberEpic).getStatus().name());
        Assertions.assertEquals("Работа", taskManager.getSubTaskById(numberSubTaskFirst).getEpic().getName());
        Assertions.assertEquals("Работа", taskManager.getSubTaskById(numberSubTaskSecond).getEpic().getName());

        taskManager.getSubTaskById(numberSubTaskFirst).setStatus(Status.DONE);
        taskManager.updateSubTask(numberSubTaskFirst, taskManager.getSubTaskById(numberSubTaskFirst));
        Assertions.assertEquals(Status.IN_PROGRESS.name(), taskManager.getEpicById(numberEpic).getStatus().name());

        taskManager.getSubTaskById(numberSubTaskSecond).setStatus(Status.DONE);
        taskManager.updateSubTask(numberSubTaskSecond, taskManager.getSubTaskById(numberSubTaskSecond));
        Assertions.assertEquals(Status.DONE.name(), taskManager.getEpicById(numberEpic).getStatus().name());
    }

    @Test
    public void checkForEpicEmptyListTask() {
        taskManager.deleteAllTasks();
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        int numberSubTaskFirst = taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic)).getId();
        int numberSubTaskSecond = taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск",
                        LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic)).getId();
        Assertions.assertEquals("Работа", taskManager.getSubTaskById(numberSubTaskFirst).getEpic().getName());
        Assertions.assertEquals("Работа", taskManager.getSubTaskById(numberSubTaskSecond).getEpic().getName());
    }

    @Test
    public void checkDeleteSubTasksEmptyListTask() {
        taskManager.deleteAllTasks();
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        int numberSubTaskFirst = taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic)).getId();
        int numberSubTaskSecond = taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск",
                        LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic)).getId();
        taskManager.deleteSubTaskById(numberSubTaskFirst);
        taskManager.deleteSubTaskById(numberSubTaskSecond);

        Assertions.assertEquals(null, taskManager.getSubTaskById(numberSubTaskFirst));
        Assertions.assertEquals(null, taskManager.getSubTaskById(numberSubTaskSecond));
    }

    @Test
    public void checkDeleteEpicsEmptyListTask() {
        taskManager.deleteAllTasks();
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic)).getId();
        taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск", LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic)).getId();
        taskManager.deleteEpicById(numberEpic);

        Assertions.assertEquals(null, taskManager.getEpicById(numberEpic));
    }

    /**
     * Тесты с неверным идентификатором задачи
     */

    @Test
    public void checkUpdateTasksInvalidId() {
        taskManager.deleteAllTasks();
        int numberTask = taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,1,12,10), 100)).getId();

        taskManager.updateTask(10, new Task("Купить еды", "молоко, хлеб, колбасу",
                LocalDateTime.of(2023,1,1,12,0), 30));

        Assertions.assertEquals("молоко и хлеб", taskManager.getTaskById(numberTask).getDescription());
        Assertions.assertEquals("2023-01-01T12:10", taskManager.getTaskById(numberTask).getStartTime().toString());
        Assertions.assertEquals("2023-01-01T13:50", taskManager.getTaskById(numberTask).getEndTime().toString());
    }

    @Test
    public void checkDeleteTasksInvalidId() {
        taskManager.deleteAllTasks();
        int numberTask = taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,5,12,10), 100)).getId();
        taskManager.deleteTaskById(10);

        Assertions.assertNotNull(taskManager.getTaskById(numberTask));
    }

    @Test
    public void checkUpdateEpicInvalidId() {
        taskManager.deleteAllTasks();
        int numberTask = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        taskManager.updateEpic(10, new Epic("Внеурочная работа", "Написание тестов по проекту №1"));

        Assertions.assertEquals("Работа", taskManager.getEpicById(numberTask).getName());
    }

    @Test
    public void checkDeleteSubTasksInvalidId() {
        taskManager.deleteAllTasks();
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        int numberSubTask = taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic)).getId();
        taskManager.deleteSubTaskById(10);

        Assertions.assertNotNull(taskManager.getSubTaskById(numberSubTask));
    }

    @Test
    public void checkDeleteEpicsInvalidId() {
        taskManager.deleteAllTasks();
        int numberEpic = taskManager.createEpic(new Epic("Работа", "Закрыть прокет №1")).getId();
        taskManager.createSubTask(new SubTask("Провести анализ задачи",
                        "Определить входные и выходные данные"),
                taskManager.getEpicById(numberEpic));
        taskManager.createSubTask(new SubTask("Реализация алгоритма",
                        "Реализовать бинарный поиск",
                        LocalDateTime.of(2023, 3, 4, 12,0), 60),
                taskManager.getEpicById(numberEpic));
        taskManager.deleteEpicById(10);

        Assertions.assertNotNull(taskManager.getEpicById(numberEpic));
    }
}
