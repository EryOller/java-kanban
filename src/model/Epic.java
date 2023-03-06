package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

    protected ArrayList<SubTask> insideSubTasks = new ArrayList<>();

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
            if (insideSubTask.getStatus().equals(IN_PROGRESS_STATUS) ||
                    insideSubTask.getStatus().equals(DONE_STATUS)) {
                setStatus(IN_PROGRESS_STATUS);
                if(insideSubTask.getStatus().equals(DONE_STATUS)) {
                    countFinishedSubTask++;
                } else {
                    return;
                }
            }
        }
        if (countFinishedSubTask == insideSubTasks.size()) {
            setStatus(DONE_STATUS);
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
}
