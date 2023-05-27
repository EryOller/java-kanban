package service;

import dao.CSVTaskRepository;
import dao.TaskRepository;
import model.Epic;
import model.SubTask;
import model.Task;
import service.exception.ManagerSaveException;

import java.io.EOFException;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager{

    private final TaskRepository taskRepository;

    public FileBackedTaskManager( TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public void save() {
        try {
            taskRepository.save(super.load());
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public SubTask createSubTask(SubTask subTask, Epic subTaskOwner) {
        SubTask newSubTask = super.createSubTask(subTask, subTaskOwner);
        save();
        return newSubTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public List<Task> getListAllTask() {
        List<Task> listTask = super.getListAllTask();
        save();
        return listTask;
    }

    @Override
    public List<SubTask> getListAllSubTask() {
        List<SubTask> listSubTask = super.getListAllSubTask();
        save();
        return listSubTask;
    }

    @Override
    public List<Epic> getListAllEpic() {
        List<Epic> listEpic = super.getListAllEpic();
        save();
        return listEpic;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        if (task != null) {
            save();
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        if (epic !=null) {
            save();
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        if (subTask != null) {
            save();
        }
        return subTask;
    }

    @Override
    public void updateTask(int id, Task task) {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        super.updateEpic(id, epic);
        save();
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) {
        super.updateSubTask(id, subTask);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager manager = new FileBackedTaskManager(
                 new CSVTaskRepository(file));
        manager.load();
        return manager;
    }

    public static void main(String[] args) {
        TaskManager taskManagerReload = new FileBackedTaskManager(
                new CSVTaskRepository(new File("./resources/task.csv")));

        taskManagerReload.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,1,12,10), 100));

        taskManagerReload.createTask(new Task("Купить одежду", "Купить в глории серые джинсы",
                LocalDateTime.of(2023,1,2,11,10), 100));
        taskManagerReload.createEpic(new Epic("Финальное задание №6", "Сдать задание"));
        taskManagerReload.createSubTask(new SubTask("Создать классы", "Класс по чтению файла",
                        LocalDateTime.of(2023,1,3,10,10), 100),
                taskManagerReload.getEpicById(3));
        taskManagerReload.createSubTask(new SubTask("Чтение и запись в файл",
                        "Реализовать методы чтения и записи в файл",
                LocalDateTime.of(2023,1,4,9,10), 100),
                taskManagerReload.getEpicById(3));
        taskManagerReload.createEpic(new Epic("Тестирование", "Протестировать трекер задач"));
        taskManagerReload.createSubTask(new SubTask("Запись в файл", "Проверить запись в файл",
                        LocalDateTime.of(2023,1,5,8,10), 100),
                taskManagerReload.getEpicById(6));
        taskManagerReload.createSubTask(new SubTask("Чтение из файла", "Проверить чтение из файла",
                        LocalDateTime.of(2023,1,6,7,10), 100),
                taskManagerReload.getEpicById(6));
        System.out.println(taskManagerReload.getTaskById(1));
        System.out.println(taskManagerReload.getEpicById(3));
        System.out.println(taskManagerReload.getSubTaskById(8));

        TaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("./resources/task.csv"));
        System.out.println("Задачи эквивалентны? " + isEqualsTasks(taskManager.getListAllTask(),
                taskManagerReload.getListAllTask()));
        System.out.println("Епики эквивалентны? " + isEqualsTasks(taskManager.getListAllEpic(),
                taskManagerReload.getListAllEpic()));
        System.out.println("Сабтаски эквивалентны? " + isEqualsTasks(taskManager.getListAllSubTask(),
                taskManagerReload.getListAllSubTask()));
    }

    private static boolean isEqualsTasks(List<? extends Task> firstList, List<? extends Task> secondList) {
        boolean isEquals = true;

        if (firstList != null && secondList != null) {
            if (firstList.size() == secondList.size()) {
                for (int i = 0; i < firstList.size(); i++) {
                    if (!firstList.get(i).equals(secondList.get(i))) {
                        isEquals = false;
                        break;
                    }
                }
            }
        }
        return isEquals;
    }
}