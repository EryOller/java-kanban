package model;

import java.time.LocalDateTime;

public class SubTask extends Task{
    private int epicNumber;

    public SubTask(String name, String description) {
        super(name, description);
        type = TaskType.SUBTASK;
    }

    public SubTask(String name, String description, LocalDateTime startDate, int duration) {
        super(name, description, startDate, duration);
        type = TaskType.SUBTASK;
    }

    public int getEpic() {
        return epicNumber;
    }

    public void setEpic(int epicNumber) {
        this.epicNumber = epicNumber;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id='" + this.id + '\'' +
                ", name='" + this.name + '\'' +
                ", description='" + this.description + '\'' +
                ", status='" + this.status + '\'' +
                ", startTime='" + this.getStartTime() +'\'' +
                ", duration='" + this.getDuration() + '\'' +
                ", endTime='" + this.getEndTime() + '\'' +
                ", epic=" + epicNumber +
                '}';
    }

    @Override
    public TaskType getType() {
        return type;
    }
}