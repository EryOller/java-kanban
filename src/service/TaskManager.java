package service;

import model.Epic;
import model.SubTask;
import model.Task;
import java.util.List;

public interface TaskManager {

    Task createTask(Task task);

    SubTask createSubTask(SubTask subTask, Epic subTaskOwner);

    Epic createEpic(Epic epic);

    List<Task> getListAllTask();

    List<SubTask> getListAllSubTask();

    List<Epic> getListAllEpic();

    void deleteAllTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    void updateTask(int id, Task task);

    void updateEpic(int id, Epic epic);

    void updateSubTask(int id, SubTask subTask);

    void deleteSubTaskById(int id);

    void deleteEpicById(int id);

    void deleteTaskById(int id);
}