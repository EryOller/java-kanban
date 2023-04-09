package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static service.InMemoryHistoryManager.clearHistory;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks =  new HashMap<>();
    private static int sequence = 0;
    private HistoryManager historyManager =  new InMemoryHistoryManager();

    @Override
    public Task createTask(Task task) {
        task.setId(++sequence);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask, Epic subTaskOwner) {
        if (epics.containsValue(subTaskOwner)) {
            subTask.setId(++sequence);
            subTask.setEpic(subTaskOwner);
            subTaskOwner.setSubTasks(subTask);
            subTasks.put(subTask.getId(), subTask);
            return subTask;
        } else {
            return null;
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++sequence);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public List<Task> getListAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getListAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getListAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        epics.clear();
        tasks.clear();
        subTasks.clear();
        clearHistory();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);

        saveTaskInHistory(task);
        return task;
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);

        saveTaskInHistory(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);

        saveTaskInHistory(subTask);
        return subTask;
    }

    @Override
    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id) && task.getId() == id) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id) && epic.getId() == id) {
            Epic saved = epics.get(epic.getId());
            saved.setName(epic.getName());
            saved.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) {
        if (subTasks.containsKey(id) && subTask.getId() == id) {
            SubTask saved = subTasks.get(subTask.getId());
            saved.setName(subTask.getName());
            saved.setStatus(subTask.getStatus());
            saved.setDescription(subTask.getDescription());
            Epic epic = subTask.getEpic();
            epic.calculateEpicStatus();
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            Epic currentEpic = subTasks.get(id).getEpic();
            for (int i = 0; i < currentEpic.getSubTasks().size(); i++) {
                if (currentEpic.getSubTasks().get(i).getId() == id) {
                    currentEpic.getSubTasks().remove(i);
                    break;
                }
            }
            subTasks.remove(id);
            new InMemoryHistoryManager().remove(id);
            currentEpic.calculateEpicStatus();
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            for (SubTask subTask : epics.get(id).getSubTasks()) {
                subTasks.remove(subTask.getId());
                new InMemoryHistoryManager().remove(subTask.getId());
            }
            epics.remove(id);
            new InMemoryHistoryManager().remove(id);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        new InMemoryHistoryManager().remove(id);
    }

    private void saveTaskInHistory(Task task) {
        historyManager.add(task);
    }

    private List<Task> getListHistory() {
        return historyManager.getHistory();
    }
}
