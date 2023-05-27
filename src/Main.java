import dao.CSVTaskRepository;
import model.Epic;
import model.SubTask;
import model.Task;
import service.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

//        taskManager.createTask(new Task("Test", "test", LocalDateTime.of(2022,1, 1, 10, 30), 100));
//        System.out.println("Task: " + taskManager.getTaskById(1));

        System.out.println("Создание двух задач:");
        System.out.println("Создание эпика с тремя подзадачами");
        taskManager.createEpic(new Epic("Закончить Яндекс-Практикум", "Дойти до конца! Путь самурая!"));
        System.out.println("Create epic: " + taskManager.getEpicById(1));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createSubTask(new SubTask("Закрыть спринт 1",
                "Спринт необходимо закрыть до 01.02.23",
                LocalDateTime.of(2023,1,1,16,10), 100),
                taskManager.getEpicById(1));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(2));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createSubTask(new SubTask("Закрыть спринт 2",
                "Спринт необходимо закрыть до 15.02.23",
                LocalDateTime.of(2023,1,1,11,10), 100),
                taskManager.getEpicById(1));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(3));

        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createSubTask(new SubTask("Закрыть спринт 3",
                "Спринт необходимо закрыть до 20.02.23",
                LocalDateTime.of(2023,1,1,8,10), 100),
                taskManager.getEpicById(1));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(4));
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println("Создание второго эпика без подзадачи");
        taskManager.createEpic(new Epic("Сохранить психическое здоровье",
                "Необходимо предпринять ряд мер по сохранению нервных клеток"));
        System.out.println("Create epic: " + taskManager.getEpicById(5));
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();

        taskManager.getSubTaskById(2);
        taskManager.getSubTaskById(3);
        taskManager.getSubTaskById(4);
        taskManager.getSubTaskById(3);
        taskManager.getEpicById(1);
        taskManager.getEpicById(5);
        taskManager.getSubTaskById(2);

        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        taskManager.deleteSubTaskById(4);
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        taskManager.deleteEpicById(5);
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        taskManager.deleteEpicById(1);
        printHistory(historyManager.getHistory());
    }

    public static void printHistory(List<Task> history) {
        System.out.println("Списко задач в Истории от самой новой , до самой старой:");
        for (int i = history.size() - 1; i >= 0; i--) {
            System.out.println(history.get(i));
        }
        Date date = new Date();
        //date.
    }
}
