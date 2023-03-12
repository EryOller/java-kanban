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
        SubTask subTask;
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        System.out.println("Создание двух задач");
        taskManager.createTask(new Task("Встреча с друзьями", "19.00 Бар Червяк"));
        System.out.println("Create task: " + taskManager.getTaskById(1));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createTask(new Task("Митап", "20.00 Соборная площать. ФАБРИКА"));
        System.out.println("Create task: " + taskManager.getTaskById(2));
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        System.out.println("Создание первого эпика с двумя подзадачами");
        taskManager.createEpic(new Epic("Закончить Яндекс-Практикум", "Дойти до конца! Путь самурая!"));
        System.out.println("Create epic: " + taskManager.getEpicById(3));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createSubTask(new SubTask("Закрыть спринт 1",
                "Спринт необходимо закрыть до 01.02.23"), taskManager.getEpicById(3));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(4));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createSubTask(new SubTask("Закрыть спринт 2",
                "Спринт необходимо закрыть до 15.02.23"), taskManager.getEpicById(3));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(5));
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        System.out.println("Создание второго эпика с одной подзадачей");
        taskManager.createEpic(new Epic("Сохранить психическое здоровье",
                "Необходимо предпринять ряд мер по сохранению нервных клеток"));
        System.out.println("Create epic: " + taskManager.getEpicById(6));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.createSubTask(new SubTask("Прогулка на улице",
                "Необходимо проводить на улице не менее 2 часов"), taskManager.getEpicById(6));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(7));
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        System.out.println("Получить список всех тасков");
        System.out.println(taskManager.getListAllTask());
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println("Получить список всех эпиков");
        System.out.println(taskManager.getListAllEpic());
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println("Получить списко всех сабтасков");
        System.out.println(taskManager.getListAllSubTask());
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        System.out.println("Изменение статуса подзадачи для первого эпика");
        subTask = taskManager.getSubTaskById(4);
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(4, subTask);
        System.out.println("Update subTask:" + taskManager.getSubTaskById(4));
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println("Get status epic: " + taskManager.getEpicById(3).getStatus());
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        System.out.println("Изменение статуса подзадачи для второго эпика");
        subTask = taskManager.getSubTaskById(7);
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(7, subTask);
        System.out.println("Update subTask:" + taskManager.getSubTaskById(7));
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println("Get status epic: " + taskManager.getEpicById(6).getStatus());
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        System.out.println("Удаление подзадачи со статусом NEW у первого эпика");
        System.out.println("Get epic: " + taskManager.getEpicById(3));
        printHistory(historyManager.getHistory());
        System.out.println();
        taskManager.deleteSubTaskById(5);
        System.out.println("Delete subTask: " + taskManager.getSubTaskById(5));
        System.out.println("Get epic: " + taskManager.getEpicById(3));
        printHistory(historyManager.getHistory());
        System.out.println();
        System.out.println();
        System.out.println("Удаление второго эпика");
        taskManager.deleteEpicById(6);
        System.out.println("Get epic: " + taskManager.getEpicById(6));
        printHistory(historyManager.getHistory());
        System.out.println();
    }

    public static void printHistory(List<Task> history) {
        System.out.println("Списко задач в Истории от самой новой , до самой старой:");
        for (int i = history.size() - 1; i >= 0; i--) {
            System.out.println(history.get(i));
        }
    }
}
