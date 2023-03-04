package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

    ArrayList<SubTask> insideSubTasks = new ArrayList<>();

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
        for (int i = 0; i < insideSubTasks.size(); i++) {
            if (insideSubTasks.get(i).getStatus().equals(IN_PROGRESS_STATUS) ||
                    insideSubTasks.get(i).getStatus().equals(DONE_STATUS)) {
                setStatus(IN_PROGRESS_STATUS);
                if(insideSubTasks.get(i).getStatus().equals(DONE_STATUS)) {
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
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", numberSubTasks='" + getSubTasks().size() + '\'' +
                ", subTasks=" + getStringSubTasks() +
                '}';
    }
}
