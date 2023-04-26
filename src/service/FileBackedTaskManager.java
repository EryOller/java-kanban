package service;

import dao.CSVTaskRepository;
import dao.TaskRepository;
import model.Epic;
import model.SubTask;
import model.Task;
import service.exception.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileBackedTaskManager implements TaskManager {

    private final TaskRepository taskRepository;
    private final InMemoryTaskManager taskManager;

    public FileBackedTaskManager(InMemoryTaskManager taskManager, TaskRepository taskRepository) {
        this.taskManager = taskManager;
        this.taskRepository = taskRepository;
    }

    private void save() {
        try {
            taskRepository.save(taskManager.load());
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            taskManager.save(taskRepository.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = taskManager.createTask(task);
        save();
        return newTask;
    }

    @Override
    public SubTask createSubTask(SubTask subTask, Epic subTaskOwner) {
        SubTask newSubTask = taskManager.createSubTask(subTask, subTaskOwner);
        save();
        return newSubTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = taskManager.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public List<Task> getListAllTask() {
        List<Task> listTask = taskManager.getListAllTask();
        save();
        return listTask;
    }

    @Override
    public List<SubTask> getListAllSubTask() {
        List<SubTask> listSubTask = taskManager.getListAllSubTask();
        save();
        return listSubTask;
    }

    @Override
    public List<Epic> getListAllEpic() {
        List<Epic> listEpic = taskManager.getListAllEpic();
        save();
        return listEpic;
    }

    @Override
    public void deleteAllTasks() {
        taskManager.deleteAllTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskManager.getTaskById(id);
        if (task != null) {
            save();
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = taskManager.getEpicById(id);
        if (epic !=null) {
            save();
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = taskManager.getSubTaskById(id);
        if (subTask != null) {
            save();
        }
        return subTask;
    }

    @Override
    public void updateTask(int id, Task task) {
        taskManager.updateTask(id, task);
        save();
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        taskManager.updateEpic(id, epic);
        save();
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) {
        taskManager.updateSubTask(id, subTask);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        taskManager.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        taskManager.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        taskManager.deleteTaskById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryTaskManager(),
                 new CSVTaskRepository(file));
        manager.load();
        return manager;
    }

    public static void main(String[] args) {
        TaskManager taskManagerReload = new FileBackedTaskManager(new InMemoryTaskManager(),
                new CSVTaskRepository(new File("./resources/task.csv")));

        taskManagerReload.createTask(new Task("Купить еды", "молоко и хлеб"));
        taskManagerReload.createTask(new Task("Купить одежду", "Купить в глории серые джинсы"));
        taskManagerReload.createEpic(new Epic("Финальное задание №6", "Сдать задание"));
        taskManagerReload.createSubTask(new SubTask("Создать классы", "Класс по чтению файла"),
                taskManagerReload.getEpicById(3));
        taskManagerReload.createSubTask(new SubTask("Чтение и запись в файл",
                        "Реализовать методы чтения и записи в файл"), taskManagerReload.getEpicById(3));
        taskManagerReload.createEpic(new Epic("Тестирование", "Протестировать трекер задач"));
        taskManagerReload.createSubTask(new SubTask("Запись в файл", "Проверить запись в файл"),
                taskManagerReload.getEpicById(6));
        taskManagerReload.createSubTask(new SubTask("Чтение из файла", "Проверить чтение из файла"),
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