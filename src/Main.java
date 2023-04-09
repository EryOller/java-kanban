import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        System.out.println("Создание двух задач:");
        System.out.println("Создание эпика с тремя подзадачами");
        taskManager.createEpic(new Epic("Закончить Яндекс-Практикум", "Дойти до конца! Путь самурая!"));
        System.out.println("Create epic: " + taskManager.getEpicById(1));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createSubTask(new SubTask("Закрыть спринт 1",
                "Спринт необходимо закрыть до 01.02.23"), taskManager.getEpicById(1));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(2));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createSubTask(new SubTask("Закрыть спринт 2",
                "Спринт необходимо закрыть до 15.02.23"), taskManager.getEpicById(1));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(3));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createSubTask(new SubTask("Закрыть спринт 3",
                "Спринт необходимо закрыть до 20.02.23"), taskManager.getEpicById(1));
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
    }
}
