package service;

import dao.TaskRepository;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static service.InMemoryHistoryManager.clearHistory;

public class InMemoryTaskManager implements TaskManager, TaskRepository {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks =  new HashMap<>();
    private static int sequence = 0;
    private HistoryManager historyManager =  new InMemoryHistoryManager();
    private TreeSet<Task> sortedTasks = new TreeSet<>(new TaskComparatorByStartTime());

    @Override
    public Task createTask(Task task) {
        if (checkIntersection(task, sequence + 1)) {
            task.setId(++sequence);
            tasks.put(task.getId(), task);
            sortedTasks.add(task);
        }
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask, Epic subTaskOwner) {
        if (epics.containsValue(subTaskOwner) && checkIntersection(subTask, sequence + 1)) {
            subTask.setId(++sequence);
            subTask.setEpic(subTaskOwner);
            subTaskOwner.setSubTasks(subTask);
            subTasks.put(subTask.getId(), subTask);
            sortedTasks.add(subTask);
            subTaskOwner.calculateEpicDuration();
            subTaskOwner.calculateEpicStartDate();
            return subTask;
        }
        return null;
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
        sortedTasks.clear();
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
        if (tasks.containsKey(id) && checkIntersection(task, id)) {
            task.setId(id);
            tasks.put(task.getId(), task);
            deleteTaskInSortedTasks(id);
            sortedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            Epic saved = epics.get(id);
            saved.setName(epic.getName());
            saved.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) {
        if (subTasks.containsKey(id) && checkIntersection(subTask, id)) {
            SubTask saved = subTasks.get(subTask.getId());
            saved.setName(subTask.getName());
            saved.setStatus(subTask.getStatus());
            saved.setDescription(subTask.getDescription());
            Epic epic = subTask.getEpic();
            epic.calculateEpicStatus();
            epic.calculateEpicDuration();
            epic.calculateEpicStartDate();
            sortedTasks.add(subTask);
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            deleteTaskInSortedTasks(id);
            Epic currentEpic = subTasks.get(id).getEpic();
            for (int i = 0; i < currentEpic.getSubTasks().size(); i++) {
                if (currentEpic.getSubTasks().get(i).getId() == id) {
                    currentEpic.getSubTasks().remove(i);
                    break;
                }
            }
            subTasks.remove(id);
            if (historyManager.getHistory().contains(getSubTaskById(id))) {
                historyManager.getHistory().remove(id);
            }
            currentEpic.calculateEpicStatus();
            currentEpic.calculateEpicDuration();
            currentEpic.calculateEpicStartDate();
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            for (SubTask subTask : epics.get(id).getSubTasks()) {
                deleteTaskInSortedTasks(subTask.getId());
                subTasks.remove(subTask.getId());
                if (historyManager.getHistory().contains(subTask.getId())) {
                    historyManager.getHistory().remove(id);
                }
            }
            epics.remove(id);
            new InMemoryHistoryManager().remove(id);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        deleteTaskInSortedTasks(id);
        tasks.remove(id);
        if (historyManager.getHistory().contains(getTaskById(id))) {
            historyManager.getHistory().remove(id);
        }
    }

    private void saveTaskInHistory(Task task) {
        historyManager.add(task);
    }

    private List<Task> getListHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TaskData load() { // загрузить в файла
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(this.tasks.values());
        tasks.addAll(this.epics.values());
        tasks.addAll(this.subTasks.values());

        List<Integer> history = new ArrayList<>();
        for (Task task : getListHistory()) {
            history.add(task.getId());
        }
        TaskData taskData = new TaskData(tasks, history);

        return taskData;
    }

    @Override
    public void save(TaskData taskData) { // сохранить из файл
        try {
            for (Task task : taskData.getTasks()) {
                switch (task.getType()) {
                    case TASK : {
                        tasks.put(task.getId(), task);
                        break;
                    }
                    case EPIC: {
                        epics.put(task.getId(), (Epic) task);
                        break;
                    }
                    case SUBTASK: {
                        subTasks.put(task.getId(), (SubTask) task);
                        break;
                    }
                    default: {
                        throw new Exception("Не удалось определить тип файла");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        sequence = taskData.getTasks().stream().max(new TaskComparator()).get().getId();

        for (int id : taskData.getHistory()) {
            if (tasks.get(id) != null) {
                getListHistory().add(tasks.get(id));
            } else if (epics.get(id) != null) {
                getListHistory().add(epics.get(id));
            } else if (subTasks.get(id) != null) {
                getListHistory().add(subTasks.get(id));
            }
        }
    }

    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().collect(Collectors.toUnmodifiableList());
    }

    public boolean checkIntersection(Task task, int idTask) {
        List<Task> prioritizedTasks = getPrioritizedTasks();
        boolean isIntersection = true;
        for (int i = 0; i < prioritizedTasks.size(); i++) {
            if (idTask != prioritizedTasks.get(i).getId()) {
                if (task.getStartTime() == null) {
                    return isIntersection;
                }
                if (prioritizedTasks.get(i).getStartTime() == null) {
                    continue;
                }
                if ((task.getStartTime().isAfter(prioritizedTasks.get(i).getStartTime()) &&
                        task.getStartTime().isBefore(prioritizedTasks.get(i).getEndTime())) ||
                        (task.getEndTime().isAfter(prioritizedTasks.get(i).getStartTime()) &&
                                task.getEndTime().isBefore(prioritizedTasks.get(i).getEndTime()))
                ) {
                    isIntersection = false;
                    break;
                }
            }
        }
        return isIntersection;
    }

    private void deleteTaskInSortedTasks(int taskId) {
        for (Task taskForDelete : sortedTasks) {
            if (taskForDelete.getId() == taskId) {
                sortedTasks.remove(taskForDelete);
                return;
            }
        }
    }
}
