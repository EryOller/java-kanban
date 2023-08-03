package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

    protected ArrayList<SubTask> insideSubTasks = new ArrayList<>();
    private LocalDateTime endTime;
    public Epic(String name, String description) {
        super(name, description);
        type = TaskType.EPIC;
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

    public void calculateEpicDuration() {
        int sumDuration = 0;
        for (SubTask insideSubTask : insideSubTasks) {
            sumDuration = sumDuration + insideSubTask.getDuration();
        }
        this.setDuration(sumDuration);
    }
    public void calculateEpicStartDate() {
        if (insideSubTasks.size() != 0) {
            LocalDateTime startTime = insideSubTasks.get(0).getStartTime();
            for (int i = 1; i < insideSubTasks.size(); i++) {
                if (startTime == null && insideSubTasks.get(i).getStartTime() != null) {
                    startTime = insideSubTasks.get(i).getStartTime();
                    continue;
                }
                if ((startTime == null && insideSubTasks.get(i).getStartTime() == null) ||
                        (startTime != null && insideSubTasks.get(i).getStartTime() == null)) {
                    continue;
                }
                if (insideSubTasks.get(i).getStartTime().isBefore(startTime)) {
                    startTime = insideSubTasks.get(i).getStartTime();
                }
            }
            setStartTime(startTime);
        }
    }
    public LocalDateTime getEndDate() {
        if (insideSubTasks.size() != 0) {
            endTime = insideSubTasks.get(0).getStartTime();
            for (int i = 1; i < insideSubTasks.size(); i++) {
                if ((insideSubTasks.get(i).getEndTime() == null && endTime == null) ||
                insideSubTasks.get(i).getEndTime() == null & endTime != null) {
                    continue;
                }
                if (insideSubTasks.get(i).getEndTime() != null && endTime == null) {
                    endTime = insideSubTasks.get(i).getEndTime();
                    continue;
                }
                if (insideSubTasks.get(i).getEndTime().isAfter(endTime)) {
                    endTime = insideSubTasks.get(i).getStartTime();
                }
            }
            return endTime;
        }
        return null;
    }
    @Override
    public String toString() {
        return "Epic{" +
                "id='" + this.id + '\'' +
                ", name='" + this.name + '\'' +
                ", description='" + this.description+ '\'' +
                ", status='" + this.status + '\'' +
                ", numberSubTasks='" + getSubTasks().size() + '\'' +
                ", subTasks=" + this.getStringSubTasks() +
                ", startTime='" + this.getStartTime() +'\'' +
                ", duration='" + this.getDuration() + '\'' +
                ", endTime='" + this.getEndTime() + '\'' +
                '}';
    }
    @Override
    public TaskType getType() {
        return type;
    }
}
