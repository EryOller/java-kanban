package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

    protected ArrayList<SubTask> insideSubTasks = new ArrayList<>();
    private final TaskType type = TaskType.EPIC;

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<SubTask> getSubTasks() {
        return insideSubTasks;
    }

    public void setSubTasks(SubTask subTask) {
        if (subTask != null) {
            this.insideSubTasks.add(subTask);
        }
    }

    public void calculateEpicStatus() {
        int countFinishedSubTask = 0;
        for (SubTask insideSubTask : insideSubTasks) {
            if (insideSubTask.getStatus().equals(Status.IN_PROGRESS) ||
                    insideSubTask.getStatus().equals(Status.DONE)) {
                setStatus(Status.IN_PROGRESS);
                if(insideSubTask.getStatus().equals(Status.DONE)) {
                    countFinishedSubTask++;
                } else {
                    return;
                }
            }
        }
        if (countFinishedSubTask == insideSubTasks.size()) {
            setStatus(Status.DONE);
        }
    }

    public String getStringSubTasks() {
        String result = "[";
        for (SubTask insideSubTask : insideSubTasks) {
            result += insideSubTask.getName() + ';';
        }
        result += ']';
        return result;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + this.id + '\'' +
                ", name='" + this.name + '\'' +
                ", description='" + this.description+ '\'' +
                ", status='" + this.status + '\'' +
                ", numberSubTasks='" + getSubTasks().size() + '\'' +
                ", subTasks=" + getStringSubTasks() +
                '}';
    }

    @Override
    public TaskType getType() {
        return type;
    }
}
