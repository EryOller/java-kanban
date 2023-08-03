package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskData;
import server.KVClient;
import service.exception.ManagerSaveException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {

    private KVClient client;
    private Gson gson = Managers.getGson();

    public HttpTaskManager(int port) throws ManagerSaveException {
        super();
        this.client = new KVClient(port); // PORT KVServer
    }

    @Override
    public TaskData load() throws ManagerSaveException { // запись
        List<Task> listTask = new ArrayList<>();

        String jsonTasks = client.load("tasks");
        tasks = gson.fromJson(jsonTasks,
                new TypeToken<HashMap<Integer, Task>>() {
                }.getType());
        String jsonEpic = client.load("epics");
        epics = gson.fromJson(jsonEpic,
                new TypeToken<HashMap<Integer, Epic>>() {
                }.getType());
        String jsonSubTask = client.load("subtasks");
        subTasks = gson.fromJson(jsonSubTask,
                new TypeToken<HashMap<Integer, SubTask>>() {
                }.getType());
        String jsonHistory = client.load("history");
        List<Integer> history = gson.fromJson(jsonHistory,
                new TypeToken<ArrayList<Integer>>() {
                }.getType());
        addTaskInHistory(history);
        for (Task task : tasks.values()) {
            listTask.add(task);
        }
        for (Task epic : epics.values()) {
            listTask.add(epic);
        }
        for (Task subTask : subTasks.values()) {
            listTask.add(subTask);
        }
        TaskData taskData = new TaskData(listTask, history);

        return taskData;
    }

    @Override
    public void save(TaskData taskData) throws ManagerSaveException {// чтение
        super.save(taskData);
    }

    private void addTaskInHistory(List<Integer> history) {
        for (Integer taskNumber : history) {
            if (tasks.get(taskNumber) != null) {
                historyManager.add(tasks.get(taskNumber));
                continue;
            }
            if (epics.get(taskNumber) != null) {
                historyManager.add(epics.get(taskNumber));
                continue;
            }
            if (subTasks.get(taskNumber) != null) {
                historyManager.add(subTasks.get(taskNumber));
            }
        }
    }

    public List<Task> getHistory() {
        return super.getListHistory();
    }
}
