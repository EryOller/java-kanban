package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks =  new HashMap<>();
    static int sequence = 0;

    public Task createTask(Task task) {
        task.setId(++sequence);
        tasks.put(task.getId(), task);
        return task;
    }

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

    public Epic createEpic(Epic epic) {
        epic.setId(++sequence);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public List<Task> getListAllTask() {
        ArrayList<Task> listTask = new ArrayList<>();

        for (Task task: tasks.values()) {
            listTask.add(task);
        }
        return listTask;
    }

    public List<SubTask> getListAllSubTask() {
        ArrayList<SubTask> listSubTask = new ArrayList<>();

        for (SubTask subTask: subTasks.values()) {
            listSubTask.add(subTask);
        }
        return listSubTask;
    }

    public List<Epic> getListAllEpic() {
        ArrayList<Epic> listEpic = new ArrayList<>();

        for (Epic epic: epics.values()) {
            listEpic.add(epic);
        }
        return listEpic;
    }

    public void deleteAllTasks() {
        epics.clear();
        tasks.clear();
        subTasks.clear();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            return null;
        }
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            return null;
        }
    }

    public SubTask getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else {
            return null;
        }
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id) && task.getId() == id) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id) && epic.getId() == id) {
            Epic saved = epics.get(epic.getId());
            saved.setName(epic.getName());
            saved.setDescription(epic.getDescription());
        }
    }

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
            currentEpic.calculateEpicStatus();
        }
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            for (SubTask subTask : epics.get(id).getSubTasks()) {
                subTasks.remove(subTask.getId());
            }
            epics.remove(id);
        }
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }
}