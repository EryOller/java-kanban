import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        SubTask subTask;

        TaskManager taskManager = new TaskManager();
        System.out.println("Создание двух задач");
        taskManager.createTask(new Task("Встреча с друзьями", "19.00 Бар Червяк"));
        System.out.println("Create task: " + taskManager.getTaskById(1));
        taskManager.createTask(new Task("Митап", "20.00 Соборная площать. ФАБРИКА"));
        System.out.println("Create task: " + taskManager.getTaskById(2));
        System.out.println();
        System.out.println("Создание первого эпика с двумя подзадачами");
        taskManager.createEpic(new Epic("Закончить Яндекс-Практикум", "Дойти до конца! Путь самурая!"));
        System.out.println("Create epic: " + taskManager.getEpicById(3));
        taskManager.createSubTask(new SubTask("Закрыть спринт 1",
                "Спринт необходимо закрыть до 01.02.23"), taskManager.getEpicById(3));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(4));
        taskManager.createSubTask(new SubTask("Закрыть спринт 2",
                "Спринт необходимо закрыть до 15.02.23"), taskManager.getEpicById(3));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(5));
        System.out.println();
        System.out.println("Создание второго эпика с одной подзадачей");
        taskManager.createEpic(new Epic("Сохранить психическое здоровье",
                "Необходимо предпринять ряд мер по сохранению нервных клеток"));
        System.out.println("Create epic: " + taskManager.getEpicById(6));
        taskManager.createSubTask(new SubTask("Прогулка на улице",
                "Необходимо проводить на улице не менее 2 часов"), taskManager.getEpicById(6));
        System.out.println("Create subTask: " + taskManager.getSubTaskById(7));
        System.out.println();
        System.out.println("Получить список всех тасков");
        System.out.println(taskManager.getListAllTask());
        System.out.println("Получить список всех эпиков");
        System.out.println(taskManager.getListAllEpic());
        System.out.println("Получить списко всех сабтасков");
        System.out.println(taskManager.getListAllSubTask());
        System.out.println();
        System.out.println("Изменение статуса подзадачи для первого эпика");
        subTask = taskManager.getSubTaskById(4);
        subTask.setStatus("DONE");
        taskManager.updateSubTask(4, subTask);
        System.out.println("Update subTask:" + taskManager.getSubTaskById(4));
        System.out.println("Get status epic: " + taskManager.getEpicById(3).getStatus());
        System.out.println();
        System.out.println("Изменение статуса подзадачи для второго эпика");
        subTask = taskManager.getSubTaskById(7);
        subTask.setStatus("DONE");
        taskManager.updateSubTask(7, subTask);
        System.out.println("Update subTask:" + taskManager.getSubTaskById(7));
        System.out.println("Get status epic: " + taskManager.getEpicById(6).getStatus());
        System.out.println();
        System.out.println("Удаление подзадачи со статусом NEW у первого эпика");
        System.out.println("Get epic: " + taskManager.getEpicById(3));
        taskManager.deleteSubTaskById(5);
        System.out.println("Delete subTask: " + taskManager.getSubTaskById(5));
        System.out.println("Get epic: " + taskManager.getEpicById(3));
        System.out.println();
        System.out.println("Удаление второго эпика");
        taskManager.deleteEpicById(6);
        System.out.println("Get epic: " + taskManager.getEpicById(6));
    }
}
