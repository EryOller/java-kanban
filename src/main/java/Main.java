import model.Epic;
import model.SubTask;
import model.Task;
import server.KVServer;
import service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();

        TaskManager taskManager = Managers.getDefault();
        taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,1,12,10), 100));

        taskManager.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,2,11,10), 100));
        taskManager.createEpic(new Epic("Финальное задание №6", "Сдать задание"));
        taskManager.createSubTask(new SubTask("Создать классы", "Класс по чтению файла",
                        LocalDateTime.of(2023,1,3,10,10), 100),
                taskManager.getEpicById(3));
        taskManager.createSubTask(new SubTask("Чтение и запись в файл",
                        "Реализовать методы чтения и записи в файл",
                LocalDateTime.of(2023,1,4,9,10), 100),
                taskManager.getEpicById(3));
        taskManager.createEpic(new Epic("Тестирование", "Протестировать трекер задач"));
        taskManager.createSubTask(new SubTask("Запись в файл", "Проверить запись в файл",
                        LocalDateTime.of(2023,1,5,8,10), 100),
                taskManager.getEpicById(6));
        taskManager.createSubTask(new SubTask("Чтение из файла", "Проверить чтение из файла",
                        LocalDateTime.of(2023,1,6,7,10), 100),
                taskManager.getEpicById(6));
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getEpicById(3));
        System.out.println(taskManager.getSubTaskById(8));


        kvServer.stop();
/**----------------------------------------------*/

//        TaskManager taskManager = Managers.getDefault();
//        HistoryManager historyManager = Managers.getDefaultHistory();
//
////        taskManager.createTask(new Task("Test", "test", LocalDateTime.of(2022,1, 1, 10, 30), 100));
////        System.out.println("Task: " + taskManager.getTaskById(1));
//
//        System.out.println("Создание двух задач:");
//        System.out.println("Создание эпика с тремя подзадачами");
//        taskManager.createEpic(new Epic("Закончить Яндекс-Практикум", "Дойти до конца! Путь самурая!"));
//        System.out.println("Create epic: " + taskManager.getEpicById(1));
//        printHistory(historyManager.getHistory());
//        System.out.println();
//        taskManager.createSubTask(new SubTask("Закрыть спринт 1",
//                "Спринт необходимо закрыть до 01.02.23",
//                LocalDateTime.of(2023,1,1,16,10), 100),
//                taskManager.getEpicById(1));
//        System.out.println("Create subTask: " + taskManager.getSubTaskById(2));
//        printHistory(historyManager.getHistory());
//        System.out.println();
//        taskManager.createSubTask(new SubTask("Закрыть спринт 2",
//                "Спринт необходимо закрыть до 15.02.23",
//                LocalDateTime.of(2023,1,1,11,10), 100),
//                taskManager.getEpicById(1));
//        System.out.println("Create subTask: " + taskManager.getSubTaskById(3));
//
//        printHistory(historyManager.getHistory());
//        System.out.println();
//        taskManager.createSubTask(new SubTask("Закрыть спринт 3",
//                "Спринт необходимо закрыть до 20.02.23",
//                LocalDateTime.of(2023,1,1,8,10), 100),
//                taskManager.getEpicById(1));
//        System.out.println("Create subTask: " + taskManager.getSubTaskById(4));
//        printHistory(historyManager.getHistory());
//        System.out.println();
//        System.out.println("Создание второго эпика без подзадачи");
//        taskManager.createEpic(new Epic("Сохранить психическое здоровье",
//                "Необходимо предпринять ряд мер по сохранению нервных клеток"));
//        System.out.println("Create epic: " + taskManager.getEpicById(5));
//        printHistory(historyManager.getHistory());
//        System.out.println();
//        System.out.println();
//
//        taskManager.getSubTaskById(2);
//        taskManager.getSubTaskById(3);
//        taskManager.getSubTaskById(4);
//        taskManager.getSubTaskById(3);
//        taskManager.getEpicById(1);
//        taskManager.getEpicById(5);
//        taskManager.getSubTaskById(2);
//
//        printHistory(historyManager.getHistory());
//        System.out.println();
//        System.out.println();
//        taskManager.deleteSubTaskById(4);
//        printHistory(historyManager.getHistory());
//        System.out.println();
//        System.out.println();
//        taskManager.deleteEpicById(5);
//        printHistory(historyManager.getHistory());
//        System.out.println();
//        System.out.println();
//        taskManager.deleteEpicById(1);
//        printHistory(historyManager.getHistory());
    }

    public static void printHistory(List<Task> history) {
        System.out.println("Списко задач в Истории от самой новой , до самой старой:");
        for (int i = history.size() - 1; i >= 0; i--) {
            System.out.println(history.get(i));
        }
    }
}
